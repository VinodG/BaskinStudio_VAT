package com.winit.baskinrobbin.salesman.webAccessLayer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.conn.ConnectTimeoutException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.winit.baskinrobbin.salesman.MyApplication;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.NetworkUtility;

/**
 * Class to setting url connection
 */
public class ConnectionHelper
{
	public static final int TIMEOUT_CONNECT_MILLIS 	= 30000;
	public static final int TIMEOUT_READ_MILLIS 	= TIMEOUT_CONNECT_MILLIS - 5000;
	//Initializing Variable
	
	public interface ConnectionExceptionListener
	{
		public void onConnectionException(Object msg);
	}
	public interface ConnectionHelperListener
	{
		public void onResponseRetrieved(String msg);
	}
	
	private String strRequest,methodName;
	private DefaultHandler handler;
	/**
	 * Method to setting url connection
	 * @param strUrl
	 * @param handler
	 */
	
	private ConnectionExceptionListener listener;
	
	public ConnectionHelper(ConnectionExceptionListener listener)
	{
		this.listener = listener;
	}
	
	/**
	 * Method to create session for c2dm notifications
	 * @param RegistrationID
	 * @param deviceId
	 * @param appVersion
	 * @param deviceVersion
	 */
	//this class is used to handle UI request by thread
	private final class UIHandler extends Handler
	{
	    public static final int DISPLAY_UI_DIALOG = 1;
	    public UIHandler(Looper looper)
	    {
	        super(looper);
	    }

	    @Override
	    public void handleMessage(Message msg)
	    {
	        switch(msg.what)
	        {
		        case UIHandler.DISPLAY_UI_DIALOG:
		        {
		        	if(listener != null)
					{
						listener.onConnectionException(msg.obj);
					}
		        }
		        default:
		            break;
	        }
	    }
	}

	public void sendRequest(Context mContext, String strRequest,DefaultHandler handler, String methodName, Preference preference) 
	{
		synchronized (MyApplication.SERVICE_LOCK)
		{
			ServiceURLs.MAIN_URL  = getURL(mContext);
			UrlPost objPostforXml = new UrlPost(); 
			UrlPost.TIMEOUT_CONNECT_MILLIS 	= 150000;
			UrlPost.TIMEOUT_READ_MILLIS 	= 150000;
			this.strRequest = strRequest;
			this.methodName = methodName; 
			this.handler 	= handler;
			
			try 
			{
				String url=ServiceURLs.MAIN_URL;
				if(methodName.equalsIgnoreCase(ServiceURLs.GetAppStatus))
				{
					url = ServiceURLs.GETAPPSTATUS_URL;
				}
				if(methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetailsFromXMLWithAuth))
				{
					writeIntoLog("\n--------------------------------------------------------");
					writeIntoLog("\n Posting Time: " + new Date().toString());
					writeIntoLog("\n URL: " + ServiceURLs.MAIN_URL);
					writeIntoLog("\n SoapAction: " + ServiceURLs.SOAPAction+methodName);
					writeIntoLog("\n Request: " + strRequest);
				}
				else if(methodName.equalsIgnoreCase(ServiceURLs.GetVanStockLogDetail)||methodName.equalsIgnoreCase(ServiceURLs.GetAppActiveStatus))
				{
					writeIntoLog("vanstock", methodName, strRequest, "");
				}
				else
				{
					writeIntoLog("all", methodName, strRequest, "");//we can get ShipStockMovementsFromXML from delivery log
				}
				
				Log.e(methodName+" Request", "strRequest - "+strRequest);
				
				InputStream inputStream = objPostforXml.soapPost(strRequest, new URL(url), ServiceURLs.SOAPAction+methodName, preference);
				
				SAXParserFactory spf	=	SAXParserFactory.newInstance();
				SAXParser sp			=	spf.newSAXParser();
				XMLReader xr			=	sp.getXMLReader();
				
				xr.setContentHandler(handler);
				
			//	to print the response change the method name here
				if(methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetailsFromXMLWithAuth)||methodName.equalsIgnoreCase(ServiceURLs.PostStockMovements))
				{
					if(inputStream != null)
						writeIntoLog("\n\n Response: ", inputStream);
					else
						writeIntoLog("\n\n Response: NULL");
					
					if(handler != null)
						xr.parse(new InputSource(new FileInputStream(AppConstants.DATABASE_PATH+"OrderResponse.xml")));
					
					File tempFile = new File(AppConstants.DATABASE_PATH+"OrderResponse.xml");
					if(tempFile.exists())
						tempFile.delete();
				}
				else if(methodName.equalsIgnoreCase(ServiceURLs.GetVanStockLogDetail)||methodName.equalsIgnoreCase(ServiceURLs.ShipStockMovementsFromXML)||methodName.equalsIgnoreCase(ServiceURLs.GetAppActiveStatus)||methodName.equalsIgnoreCase(ServiceURLs.GetUserDeviceStatus))
				{
					if(inputStream != null)
						writeIntoVanStockResponseLog("\n\n Response: ", inputStream);
					else
						writeIntoLog("\n\n Response: NULL");
					
					if(handler != null)
						xr.parse(new InputSource(new FileInputStream(AppConstants.DATABASE_PATH+"OrderResponse.xml")));
					
					File tempFile = new File(AppConstants.DATABASE_PATH+"OrderResponse.xml");
					if(tempFile.exists())
						tempFile.delete();
				}
				else
				{
					if(handler != null)
						xr.parse(new InputSource(inputStream));
				}
			} 
			catch (ConnectTimeoutException e)
			{
				e.printStackTrace();
				sendRequest(mContext, strRequest,handler, methodName, preference);
			}
			catch (SocketTimeoutException e)
			{
				sendException(""+e.getMessage());
				e.printStackTrace();
			}
			catch (Exception e) 
			{
				sendException(""+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * method to handle the single line response
	 * @param strRequest
	 * @param handler
	 * @param methodName
	 */
	public boolean sendDEVRequest(String strRequest, String methodName) 
	{
		UrlPost objPostforXml = new UrlPost();
		UrlPost.TIMEOUT_CONNECT_MILLIS 	= 60000;
		UrlPost.TIMEOUT_READ_MILLIS 	= 60000;
		boolean isAllowed = true;
		try 
		{
			InputStream inputStream = objPostforXml.soapPostDEV(strRequest, new URL(ServiceURLs.DEV_URL), ServiceURLs.SOAPAction+methodName);
			
			//to taking one line
		  	BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
		  	String success = "";
		  	success = r.readLine();
		  	LogUtils.errorLog("success - ", ""+success);
			
		  	writeIntoLog("all", methodName, strRequest, success);
		  	
		  	if(success.contains(">Success<") || success.contains(">true<"))
		  	{
		  		isAllowed =  true;
		  	}
		  	else
		  	{
		  		writeIntoLog("error", methodName, strRequest, success);
		  		isAllowed =  false;
		  	}
		} 
		catch (ConnectTimeoutException e)
		{
			isAllowed =  true;
			//calling handle ui request method to handle ConnectTimeoutException
			handleUIRequest("Socket TimeOut");
		}
		catch (SocketTimeoutException e)
		{
			isAllowed =  true;
			//calling handle ui request method to handle SocketTimeoutException
			handleUIRequest("Socket TimeOut");
		}
		catch (Exception e) 
		{
			isAllowed =  true;
			handleUIRequest("Socket TimeOut");
		}
		return isAllowed;
	}
	
	public void sendRequest_Bulk(Context mContext, String strRequest,DefaultHandler handler, String methodName, Preference preference) 
	{
		synchronized (MyApplication.SERVICE_LOCK)
		{
			ServiceURLs.MAIN_URL  = getURL(mContext);
			UrlPost objPostforXml 			= new UrlPost();
			UrlPost.TIMEOUT_CONNECT_MILLIS 	= 600000;
			UrlPost.TIMEOUT_READ_MILLIS 	= 600000;
			this.strRequest = strRequest;
			this.methodName = methodName;
			this.handler 	= handler;
			
			try 
			{
				if(methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetailsFromXMLWithAuth))
				{
					writeIntoLog("\n--------------------------------------------------------");
					writeIntoLog("\n Posting Time: " + new Date().toString());
					writeIntoLog("\n URL: " + ServiceURLs.MAIN_URL);
					writeIntoLog("\n SoapAction: " + ServiceURLs.SOAPAction+methodName);
					writeIntoLog("\n Request: " + strRequest);
				}
				else
				{
					writeIntoLog("all", methodName, strRequest, "");
				}
				InputStream inputStream = objPostforXml.soapPost(strRequest, new URL(ServiceURLs.MAIN_URL), ServiceURLs.SOAPAction+methodName, preference);
				
//				String responseStr = StringUtils.convertStreamToString(inputStream);
				
				
				
				SAXParserFactory spf	=	SAXParserFactory.newInstance();
				SAXParser sp			=	spf.newSAXParser();
				XMLReader xr			=	sp.getXMLReader();
				
				xr.setContentHandler(handler);
				
				//to print the response change the method name here
				if(methodName.equalsIgnoreCase(ServiceURLs.PostTrxDetailsFromXMLWithAuth))
				{
					if(inputStream != null)
						writeIntoLog("\n\n Response: ", inputStream);
					else
						writeIntoLog("\n\n Response: NULL");
					
					if(handler != null)
						xr.parse(new InputSource(new FileInputStream(AppConstants.DATABASE_PATH+"OrderResponse.xml")));
					
					File tempFile = new File(AppConstants.DATABASE_PATH+"OrderResponse.xml");
					if(tempFile.exists())
						tempFile.delete();
				}
				else
				{
					if(handler != null)
						xr.parse(new InputSource(inputStream));
				}
			} 
			catch (ConnectTimeoutException e)
			{
				sendException(""+e.getMessage());
				e.printStackTrace();
			}
			catch (SocketTimeoutException e)
			{
				sendException(""+e.getMessage());
				e.printStackTrace();
			}
			catch (Exception e) 
			{
				sendException(""+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * method to handle the single line response
	 * @param strRequest
	 * @param handler
	 * @param methodName
	 * 
	 */
	
	public boolean sendRequest(Context mContext, String strRequest, String methodName, Preference preference) 
	{
		synchronized (MyApplication.SERVICE_LOCK)
		{
			ServiceURLs.MAIN_URL  = getURL(mContext);
			
			UrlPost objPostforXml = new UrlPost();
			UrlPost.TIMEOUT_CONNECT_MILLIS 	= 150000;
			UrlPost.TIMEOUT_READ_MILLIS 	= 150000;
			try 
			{
				InputStream inputStream = objPostforXml.soapPost(strRequest, new URL(ServiceURLs.MAIN_URL), ServiceURLs.SOAPAction+methodName, preference);
				
				if(inputStream != null)
				{
					//to taking one line
				  	BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
				  	String success = "";
				  	success = r.readLine();
				  	LogUtils.errorLog("success - ", ""+success);
					
				  	writeIntoLog("all", methodName, strRequest, success);
				  	if(success.contains("<GetClearDataPermissionResult>true</GetClearDataPermissionResult>")){
				  		return true;
				  	}
				  	else if(success.contains("<Status>Success</Status>"))
				  	{
				  		return true;
				  	}
				  	else
				  	{
				  		writeIntoLog("error", methodName, strRequest, success);
				  		return false;
				  	}
				}
			} 
			catch (ConnectTimeoutException e)
			{
				sendException(""+e.getMessage());
				e.printStackTrace();
			}
			catch (SocketTimeoutException e)
			{
				sendException(""+e.getMessage());
				e.printStackTrace();
			}
			catch (Exception e) 
			{
				sendException(""+e.getMessage());
				e.printStackTrace();
			}
			return false;
		}
	}
	
	/**
	 * method to handle the single line response
	 * @param strRequest
	 * @param handler
	 * @param methodName
	 */
	public boolean sendRequest_Bulk(Context mContext, String strRequest, String methodName, Preference preference) 
	{
		synchronized (MyApplication.SERVICE_LOCK)
		{
			ServiceURLs.MAIN_URL  = getURL(mContext);
			
			UrlPost objPostforXml = new UrlPost();
			UrlPost.TIMEOUT_CONNECT_MILLIS 	= 600000;
			UrlPost.TIMEOUT_READ_MILLIS 	= 600000;
			try 
			{
				InputStream inputStream = objPostforXml.soapPost(strRequest, new URL(ServiceURLs.MAIN_URL), ServiceURLs.SOAPAction+methodName, preference);
				
				//to taking one line
			  	BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
			  	String success = "";
			  	success = r.readLine();
			  	LogUtils.errorLog("success - ", ""+success);
				
			  	writeIntoLog("all", methodName, strRequest, success);
			  	
			  	if(success.contains("<Status>Success</Status>"))
			  		return true;
			  	else
			  	{
			  		writeIntoLog("error", methodName, strRequest, success);
			  		return false;
			  	}
			} 
			catch (ConnectTimeoutException e)
			{
				sendException(""+e.getMessage());
				e.printStackTrace();
			}
			catch (SocketTimeoutException e)
			{
				sendException(""+e.getMessage());
				e.printStackTrace();
			}
			catch (Exception e) 
			{
				sendException(""+e.getMessage());
				e.printStackTrace();
			}
			return false;
		}
	}
	//to handle UI request
	 public void handleUIRequest(String message)
	 {
		 try
		 {
			 Thread uiThread = new HandlerThread("UIHandler");
			 uiThread.start();
			 UIHandler uiHandler = new UIHandler(((HandlerThread) uiThread).getLooper());
	
			 Message msg = uiHandler.obtainMessage(UIHandler.DISPLAY_UI_DIALOG);
			 msg.obj = message;
			 uiHandler.sendMessage(msg);
		 }
		 catch (Exception e) 
		 {
			e.printStackTrace();
		 }
	 }
	 
	public static void writeIntoLog(String str, InputStream is) throws IOException
	{
		try
		{
			 BufferedInputStream bis = new BufferedInputStream(is);
			 FileOutputStream fosOrderFile = new FileOutputStream(AppConstants.DATABASE_PATH+"OrderResponse.xml");
			 BufferedOutputStream bossOrderFile = new BufferedOutputStream(fosOrderFile);
			 deleteLogFile(Environment.getExternalStorageDirectory().toString()+"/OrderLog.txt");
			 FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/OrderLog.txt", true);
			 BufferedOutputStream bos = new BufferedOutputStream(fos);
			 
			 bos.write(str.getBytes());
			 
			 byte byt[] = new byte[1024];
			 int noBytes;
			 
			 while((noBytes = bis.read(byt)) != -1)
			 {	 
				 bossOrderFile.write(byt,0,noBytes);
				 bos.write(byt,0,noBytes);
			 }
			 
			 bossOrderFile.flush();
			 bossOrderFile.close();
			 fosOrderFile.close();
			 bos.flush();
			 bos.close();
			 fos.close();
			 bis.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	 }
	
	public static void writeIntoLogForVanStockLog(String str) throws IOException
	{
		try
		{
			deleteLogFile(Environment.getExternalStorageDirectory().toString()+"/VanStockLog.txt");
			FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/VanStockLog.txt", true);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(str.getBytes());
			
			bos.flush();
			bos.close();
			fos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void writeIntoVanStockResponseLog(String str, InputStream is) throws IOException
	{
		try
		{
			File file = new File(AppConstants.DATABASE_PATH);
			if(!file.exists())
				file.mkdirs();
			file = new File(AppConstants.DATABASE_PATH, "OrderResponse.xml");
			if(!file.exists())
				file.createNewFile();
			BufferedInputStream bis = new BufferedInputStream(is);
			FileOutputStream fosOrderFile = new FileOutputStream(file);
			BufferedOutputStream bossOrderFile = new BufferedOutputStream(fosOrderFile);
			deleteLogFile(Environment.getExternalStorageDirectory().toString()+"/VanStockLog.txt");
			FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/VanStockLog.txt", true);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			
			bos.write(str.getBytes());
			
			byte byt[] = new byte[1024];
			int noBytes;
			
			while((noBytes = bis.read(byt)) != -1)
			{	 
				bossOrderFile.write(byt,0,noBytes);
				bos.write(byt,0,noBytes);
			}
			
			bossOrderFile.flush();
			bossOrderFile.close();
			fosOrderFile.close();
			bos.flush();
			bos.close();
			fos.close();
			bis.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}
	public static void writeIntoLog(String str) throws IOException
	{
		try
		{
			deleteLogFile(Environment.getExternalStorageDirectory().toString()+"/OrderLog.txt");
			FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/OrderLog.txt", true);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(str.getBytes());
			
			bos.flush();
			bos.close();
			fos.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	 }
	
	public static void writeIntoLogForDeliveryAgent(String str) throws IOException
	{
		try
		{
			deleteLogFile(Environment.getExternalStorageDirectory().toString()+"/DeliveryLog.txt");
			FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/DeliveryLog.txt", true);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(str.getBytes());
			 
			bos.flush();
			bos.close();
			fos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	 }
	
	public static void writeErrorLogForDeliveryAgent(String str) throws IOException
	{
		try
		{
			 FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/DeliveryErrorLog.txt", true);
			 BufferedOutputStream bos = new BufferedOutputStream(fos);
			 bos.write(str.getBytes());
			 
			 bos.flush();
			 bos.close();
			 fos.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	 }
	 
	 /**
	 * This method stores InputStream data into a file at specified location
	 * @param is
	 */
	public static void convertResponseToFile(InputStream is, String method) throws IOException
	{
		try
		{
			 BufferedInputStream bis 	= 	new BufferedInputStream(is);
			 FileOutputStream fos 		= 	new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/"+method+"Response.xml");
			 BufferedOutputStream bos 	= 	new BufferedOutputStream(fos);
			 
			 byte byt[] = new byte[1024];
			 int noBytes;
			 
			 while((noBytes = bis.read(byt)) != -1)
				 bos.write(byt,0,noBytes);
			 
			 bos.flush();
			 bos.close();
			 fos.close();
			 bis.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	 }
	
	private void writeIntoLog(String type,String methodName, String strRequest, String success)
	{
		try 
		{
			if(type.equalsIgnoreCase("all"))
			{
				writeIntoLogForDeliveryAgent("\n--------------------------------------------------------");
				writeIntoLogForDeliveryAgent("\n Posting Time: " + new Date().toString());
				writeIntoLogForDeliveryAgent("\n URL: " + ServiceURLs.MAIN_URL);
				writeIntoLogForDeliveryAgent("\n SoapAction: " + ServiceURLs.SOAPAction+methodName);
				writeIntoLogForDeliveryAgent("\n--------------------------------------------------------");
				writeIntoLogForDeliveryAgent("\n Request: " + strRequest);
				writeIntoLogForDeliveryAgent("\n Response: " + success);
			}
			else if(type.equalsIgnoreCase("vanstock"))
			{
				writeIntoLogForVanStockLog("\n--------------------------------------------------------");
				writeIntoLogForVanStockLog("\n Posting Time: " + new Date().toString());
				writeIntoLogForVanStockLog("\n URL: " + ServiceURLs.MAIN_URL);
				writeIntoLogForVanStockLog("\n SoapAction: " + ServiceURLs.SOAPAction+methodName);
				writeIntoLogForVanStockLog("\n--------------------------------------------------------");
				writeIntoLogForVanStockLog("\n Request: " + strRequest);
				writeIntoLogForVanStockLog("\n Response: " + success);
			}
			else
			{
				writeErrorLogForDeliveryAgent("\n--------------------------------------------------------");
		  		writeErrorLogForDeliveryAgent("\n Posting Time: " + new Date().toString());
		  		writeErrorLogForDeliveryAgent("\n URL: " + ServiceURLs.MAIN_URL);
		  		writeErrorLogForDeliveryAgent("\n SoapAction: " + ServiceURLs.SOAPAction+methodName);
		  		writeErrorLogForDeliveryAgent("\n--------------------------------------------------------");
		  		writeErrorLogForDeliveryAgent("\n Request: " + strRequest);
		  		writeErrorLogForDeliveryAgent("\n Response: " + success);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	public static void deleteLogFile(String path)
	{
		try
		{
			File file = new File(path);
			if(file.exists())
			{
				long sizeInMB = file.length()/1048576;
				if(sizeInMB >= 5)
					file.delete();
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private String getURL(Context mContext)
	{
		String URL = new Preference(mContext).getStringFromPreference(Preference.MAIN_URL, "");
		
		if(NetworkUtility.isWifiConnected(mContext) && AppConstants.IS_BR_NETWORK_REACHABLE)
			URL = ServiceURLs.MAIN_LOCAL_URL;
		
		if(URL == null || URL.length() <= 0)
			URL = ServiceURLs.MAIN_GLOBAL_URL;
		
		return URL;
	}
	
	
	public static void writeIntoLogForOrder(String str) 
	{
		try
		{
			deleteLogFile(Environment.getExternalStorageDirectory().toString()+"/OrderLog.txt");
			FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/OrderLog.txt", true);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			bos.write(str.getBytes());
			
			bos.flush();
			bos.close();
			fos.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	 }
	
	private void sendException(String msg)
	{
		if(listener != null)
			listener.onConnectionException(msg);
	}
}

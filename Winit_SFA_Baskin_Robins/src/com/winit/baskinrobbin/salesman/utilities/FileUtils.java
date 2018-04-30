package com.winit.baskinrobbin.salesman.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;

import com.winit.baskinrobbin.salesman.webAccessLayer.EasySSLSocketFactory;

public class FileUtils 
{
	public interface DownloadListner 
	 {
		 public void onProgrss(int count);
		 public void onComplete();
		 public void onError();
	 }
	public static synchronized String downloadSQLITE(String sUrl,String directory, Context context,  String sqliteName,DownloadListner downloadListener)
	{
		
		if(!sUrl.contains(".zip"))
			sUrl  = sUrl.replace("sqlite","zip");
		System.gc();
		
		if ( sUrl == null || sUrl.length() <= 0)
			return null;
		
//		String LocalFilePath = sqliteName+".sqlite";
		String LocalFilePath = sqliteName+".zip";
		
		File file =  new File(directory, LocalFilePath);
		
		if(file.exists())
			file.delete();
		
		int bytesread = 0;
		byte[] bytes = new byte[1024];	
		InputStream strm = null;
		HttpResponse response = null;
		HttpEntity entity = null;
		String LocalFile = null;
		FileOutputStream foStream;
		BufferedOutputStream writer = null;
		int downloadPercentage = 0;
		try
		{	
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
			
			int BytesSize = 0;			
			sUrl = sUrl.replace(" ", "%20");
			DefaultHttpClient httpclient = getAnyHostURL();
			HttpGet httpget = new HttpGet(sUrl);
			acquireWifi(context, wl);
			response = httpclient.execute(httpget);
			if (response == null) 
			{
				return null;
			}
			entity = response.getEntity();
			BytesSize=(int) entity.getContentLength();	
			strm = entity.getContent();			
			
			File dire = new File(directory);
			if (!dire.isDirectory()) 
			{
				dire.mkdirs();
			}
			LocalFile = directory+LocalFilePath;
//					directory = null;
			foStream = new FileOutputStream(LocalFile);
			writer = new BufferedOutputStream(foStream);			
			long byteallread = 0;						
			do 
			{
				if(NetworkUtility.isNetworkConnectionAvailable(context))
				{
					bytesread = strm.read(bytes, 0, bytes.length);
					if(bytesread > 0)
					{	
						writer.write(bytes, 0, bytesread);
						writer.flush();
					}
				}
				else
				{
					file.delete();
					break;
				}
				byteallread = byteallread + bytesread;
				
				downloadPercentage = (int) ((byteallread*100)/BytesSize);
				downloadListener.onProgrss(downloadPercentage);
				
			} while (bytesread > 0);					
			
			if(BytesSize > file.length())
			{
				file.delete();
				return null;
			}
			
			ZipUtils.upZipFile(file, directory);
			
//			File zipFile = new File(LocalFile);
			
//			if(zipFile.exists())
//				zipFile.delete();
				
			writer.close();
			foStream.close();
			
			strm.close();		
			
			count = 0;
			downloadListener.onComplete();
			return LocalFile;
		}
		catch (Exception e) 
		{
			file.delete();
			
			if(count < 3)
			{
				count++;
				e.printStackTrace();
				return downloadSQLITE(sUrl,directory, context,  sqliteName,downloadListener);
			}
			else
			{
				downloadListener.onError();
				return null;
			}
		}
	}
	
	public static synchronized String downloadSQLITE(String sUrl,String directory, Context context,  String sqliteName)
	{
		System.gc();
		
		if ( sUrl == null || sUrl.length() <= 0)
			return null;
		
		String LocalFilePath = sqliteName+".sqlite";
		
		File file =  new File(directory+"/", LocalFilePath);
		int bytesread = 0;
		byte[] bytes = new byte[1024];	
		InputStream strm = null;
		HttpResponse response = null;
		HttpEntity entity = null;
		String LocalFile = null;
		FileOutputStream foStream;
		BufferedOutputStream writer = null;
		
		try
		{	
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
			
			int BytesSize = 0;			
			sUrl = sUrl.replace(" ", "%20");
			DefaultHttpClient httpclient = getAnyHostURL();
			HttpGet httpget = new HttpGet(sUrl);
			acquireWifi(context, wl);
			response = httpclient.execute(httpget);
			if (response == null) 
			{
				return null;
			}
			entity = response.getEntity();
			BytesSize=(int) entity.getContentLength();	
			strm = entity.getContent();			
			
			File dire = new File(directory);
			if (!dire.isDirectory()) 
			{
				dire.mkdirs();
			}
			LocalFile = directory+"/"+LocalFilePath;
//					directory = null;
			foStream = new FileOutputStream(LocalFile);
			writer = new BufferedOutputStream(foStream);			
			long byteallread = 0;						
			do 
			{
				if(NetworkUtility.isNetworkConnectionAvailable(context))
				{
					bytesread = strm.read(bytes, 0, bytes.length);
					if(bytesread > 0)
					{	
						writer.write(bytes, 0, bytesread);
						writer.flush();
					}
				}
				else
				{
					file.delete();
					break;
				}
				byteallread = byteallread + bytesread;
			} while (bytesread > 0);					
			
			if(BytesSize > file.length())
			{
				file.delete();
				return null;
			}
			writer.close();
			foStream.close();
			
			strm.close();		
			
			count = 0;
			return LocalFile;
		}
		
		catch (Exception e) 
		{
			file.delete();
			
			if(count < 3)
			{
				count++;
				e.printStackTrace();
				return downloadSQLITE(sUrl,directory, context,  sqliteName);
			}
			else
				return null;
		}
	}
	
	public static int count = 0;
	
	
	/**
	 * Method, provide any host URL liberty to url
	 * @return
	 */
	public static DefaultHttpClient getAnyHostURL()
	{
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 200000*60*60);
		HttpConnectionParams.setSoTimeout(params, 200000*60*60);

//		Log.e("DefaultHttpClient", "  SocketBufferSize  "+HttpConnectionParams.getSocketBufferSize(params));
		// registers schemes for both http and https
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

		// Set verifier
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

			@Override
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		});

		EasySSLSocketFactory sslSocketFactory = new EasySSLSocketFactory();

		registry.register(new Scheme("https", sslSocketFactory, 443));

		ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(
				params, registry);

		DefaultHttpClient httpclient = new DefaultHttpClient(manager,
				params);
		
		return httpclient;
	}
	
	public static String SaveInputStreamAsFile(Context ctx,String SdcardPath, String source, String fileName) {
		try {
			  File myFile = new File(SdcardPath,"Themes.xml");

              myFile.createNewFile();

              FileOutputStream fOut = new FileOutputStream(myFile);

              OutputStreamWriter myOutWriter =

                      new OutputStreamWriter(fOut);

              myOutWriter.append(source);

              myOutWriter.close();

              fOut.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void inputStream2File(InputStream inputStream, String fileName,String SdcardPath)
	 {
	  try
	  {
		  File themeFile = new File(SdcardPath);
		  if(!themeFile.exists())
		  {
			  new File(SdcardPath).mkdirs();
		  }
		  File file =new File(SdcardPath + fileName);
		  if(file.exists())
		  {
			  file.delete();
		  }
		  
		   BufferedInputStream bis = new BufferedInputStream(inputStream);
	       FileOutputStream fos = new FileOutputStream(SdcardPath+fileName);
	       BufferedOutputStream bos = new BufferedOutputStream(fos);
	       byte byt[] = new byte[1024];
	       int noBytes;
	       while((noBytes=bis.read(byt)) != -1)
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
	
	public static InputStream getFileFromSDcard(String SDcardpath, String fileName)
	 {
		 InputStream is = null;
		  try 
		  {
			    File myFile = new File(SDcardpath,fileName);
			    if(!myFile.exists())
			    {
			    	myFile.createNewFile();
			    }
				FileInputStream fIn = new FileInputStream(myFile);
				BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
				String aDataRow = "";
				String aBuffer = "";
				while ((aDataRow = myReader.readLine()) != null) {
					aBuffer += aDataRow + "\n";
				}
//				txtData.setText(aBuffer);
				 is = new ByteArrayInputStream(aBuffer.getBytes());
				myReader.close();
		  } 
		  catch (Exception e)
		  {
		   e.printStackTrace();
		  }
		return is;
	 }
	
	
	
	public static void copyDirectory(File sourceLocation , File targetLocation) throws IOException {
	    if (sourceLocation.isDirectory()) {
	        if (!targetLocation.exists()) {
	            targetLocation.mkdir();
	        }

	        String[] children = sourceLocation.list();
	        for (int i=0; i<children.length; i++) {
	            copyDirectory(new File(sourceLocation, children[i]),
	                    new File(targetLocation, children[i]));
	        }
	    } else {

	        InputStream in = new FileInputStream(sourceLocation);
	        OutputStream out = new FileOutputStream(targetLocation);

	        // Copy the bits from instream to outstream
	        byte[] buf = new byte[1024];
	        int len;
	        while ((len = in.read(buf)) > 0) {
	            out.write(buf, 0, len);
	        }
	        in.close();
	        out.close();
	    }
	}
	
	private static void acquireWifi(Context context, PowerManager.WakeLock mWifiLock)
	{
	   mWifiLock.acquire();
	   Log.e("acquire", "DONE");
	}
	
	
	public static class productDO 
	{
		String firstValue;
		String SecondValue;
		String Start;
		String End;
	}
	
	public static void deleteOldBackups(String dirPath)
	{
		try 
		{
		    File f = new File(dirPath);
		    File[] files  = f.listFiles();

		    if(files != null && files.length > 5)
		    {
			    for(int i=0; i < files.length; i++)
			    {
			        File file   = files[i];
			        String name = file.getName();
			        if(name.contains(".sqlite"))
			        	name = name.replace(".sqlite", "");
			        
			        long days =  CalendarUtils.getDateDifferenceInDays(name, CalendarUtils.getOrderPostDate());
			        
			        if(days > 5)
			        	file.delete();
			     }
		    }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	   
	}
	
	public static String convertFileToZip(String filepath, String outFilePath)
	{
		try 
		{
			byte[] buffer = new byte[1024];
			FileOutputStream fos = new FileOutputStream(outFilePath);
			ZipOutputStream zos = new ZipOutputStream(fos);
			ZipEntry ze= new ZipEntry("salesman.sqlite");
			zos.putNextEntry(ze);
			FileInputStream in = new FileInputStream(filepath);

			int len;
			while ((len = in.read(buffer)) > 0)
			{
				zos.write(buffer, 0, len);
			}

			in.close();
			zos.closeEntry();

			//remember close it
			zos.close();

			System.out.println("Done");
			return outFilePath;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
	}
	public static File getApkFilePath(String folder)
	{
	 
        File captureImagesStorageDir = new File(Environment.getExternalStorageDirectory()+"/Baskin/"+folder);
 
        if (!captureImagesStorageDir.exists()) 
        {
            if (!captureImagesStorageDir.mkdirs())
            {
                Log.d("Alseer", "Oops! Failed create ");
                return null;
            }
        }
 
        String timestamp = System.currentTimeMillis()+""; 
        File imageFile = new File(captureImagesStorageDir.getPath() + File.separator
                    + "Baskin_SFA_" + timestamp + ".apk");
            
        
        return imageFile;
	}
	public static String downloadAPKFile(String sUrl,Context context) {
		// TODO Auto-generated method stub

		System.gc();
		
		if ( sUrl == null || sUrl.length() <= 0)
			return null;
		
//		String LocalFilePath = apkFileName+".apk";
		
		File file =  getApkFilePath("APKFiles");
		int bytesread = 0;
		byte[] bytes = new byte[1024];	
		InputStream strm = null;
		HttpResponse response = null;
		HttpEntity entity = null;
//		String LocalFile = null;
		FileOutputStream foStream;
		BufferedOutputStream writer = null;
		
		try
		{	
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
			
			int BytesSize = 0;			
			sUrl = sUrl.replace(" ", "%20");
			DefaultHttpClient httpclient = getAnyHostURL();
			HttpGet httpget = new HttpGet(sUrl);
			acquireWifi(context, wl);
			response = httpclient.execute(httpget);
			if (response == null) 
			{
				return null;
			}
			entity = response.getEntity();
			BytesSize=(int) entity.getContentLength();	
			strm = entity.getContent();			
			
//			File dire = new File(directory);
//			if (!dire.isDirectory()) 
//			{
//				dire.mkdirs();
//			}
//			LocalFile = directory+"/"+LocalFilePath;
//					directory = null;
			foStream = new FileOutputStream(file.getPath());
			writer = new BufferedOutputStream(foStream);			
			long byteallread = 0;						
			do 
			{
				if(NetworkUtility.isNetworkConnectionAvailable(context))
				{
					bytesread = strm.read(bytes, 0, bytes.length);
					if(bytesread > 0)
					{	
						writer.write(bytes, 0, bytesread);
						writer.flush();
					}
				}
				else
				{
					file.delete();
					break;
				}
				byteallread = byteallread + bytesread;
			} while (bytesread > 0);					
			
			if(BytesSize > file.length())
			{
				file.delete();
				return null;
			}
			writer.close();
			foStream.close();
			
			strm.close();		
			
			count = 0;
			return file.getPath();
		}
		
		catch (Exception e) 
		{
			file.delete();
			
			if(count < 3)
			{
				count++;
				e.printStackTrace();
				return downloadAPKFile(sUrl, context);
			}
			else
				return null;
		}
	
	}
}

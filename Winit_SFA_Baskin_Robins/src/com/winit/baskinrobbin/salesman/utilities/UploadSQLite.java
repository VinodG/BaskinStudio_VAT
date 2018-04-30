package com.winit.baskinrobbin.salesman.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.winit.baskinrobbin.parsers.SQLiteUploadParser;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.MultiPartEntity;
import com.winit.baskinrobbin.salesman.common.MultiPartEntity.ProgressListener;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.listeners.UploadDataListener;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class UploadSQLite 
{
	long totalSize = 0 ;
	UploadDataListener uploadDataListener = null;
	public void uploadSQLite(Context context, String module, UploadDataListener uploadDataListener) 
	{
		totalSize = 0;
		String URL = getSQLiteURL(context);
		String fileName = AppConstants.DATABASE_NAME;
		InputStream is = null;
		this.uploadDataListener = uploadDataListener;
		
		try {
			HttpClient httpclient = new DefaultHttpClient();
			Log.e("SQLitePath", "SQLite = " + AppConstants.DATABASE_PATH + AppConstants.DATABASE_NAME);
//			URL = "http://10.20.53.117/BR/";
			HttpPost httppost = new HttpPost(URL + "uploadfile/upload.aspx?Module="+module);
			
			File filePath   = new File(AppConstants.DATABASE_PATH+"/"+AppConstants.DATABASE_NAME);
			
			String zipPath 	= Environment.getExternalStorageDirectory()+"/Baskin/salesman.zip";
			
			String path  	= FileUtils.convertFileToZip(filePath.getAbsolutePath().toString(), zipPath);
					
			MultiPartEntity muEntity = new MultiPartEntity(new ProgressListener()
			{
				@Override
				public void transferred(long num)
				{
					updateProgress(num, "Uploading..");
				}
			});

			filePath  = new File(path);
			
			if (filePath.exists()) 
			{
				Log.e("Upload", "Called");
				ContentBody cbFile = new FileBody(filePath, "application/octet-stream");
				muEntity.addPart("FileName", cbFile);
				
				totalSize = muEntity.getContentLength();
				
				httppost.setEntity(muEntity);
				
				HttpResponse response;
				response = httpclient.execute(httppost);
				HttpEntity resEntity = response.getEntity();
				is = resEntity.getContent();
				String str2 =is.toString();
				String myString = getStringFromInputStream(is);
				fileName = parseSQLiteUploadResponse(context, is);
			}
			Log.e("SQLitePath", "image = " + fileName);
			if(!fileName.equalsIgnoreCase(""))
			{
//				updateProgress(AppStatus.TOTAL_PROGRESS, "");
//				this.uploadDataListener = null;
			}
			else
			{
				updateProgress(AppStatus.NOT_AVAIL, "");
				this.uploadDataListener = null;
			}
			
		} 
		catch (Exception e) 
		{
			updateProgress(AppStatus.NOT_AVAIL, "");
			this.uploadDataListener = null;
			e.printStackTrace();
		}
	}
	
	private static String getStringFromInputStream(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}
	public static String parseSQLiteUploadResponse(Context context,
			InputStream inputStream) {
		try {
			SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
			XMLReader xr = sp.getXMLReader();
			SQLiteUploadParser handler = new SQLiteUploadParser(context);
			xr.setContentHandler(handler);
			xr.parse(new InputSource(inputStream));
			return handler.getUploadedFileName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	private String getSQLiteURL(Context mContext)
	{
		String URL = new Preference(mContext).getStringFromPreference(Preference.DATABASE_URL, "");
		
		if(!NetworkUtility.isWifiConnected(mContext))
			URL = ServiceURLs.IMAGE_GLOBAL_URL;
		
		if(URL == null || URL.length() <= 0)
			URL = ServiceURLs.IMAGE_GLOBAL_URL;
		
		return URL;
	}
	
	private void updateProgress(long num, String status) 
	{
		if(uploadDataListener != null)
			uploadDataListener.updateStatus((int) ((num / (float) totalSize) * AppStatus.TOTAL_PROGRESS), status);
	}
}

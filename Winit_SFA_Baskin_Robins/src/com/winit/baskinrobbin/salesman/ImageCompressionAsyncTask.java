package com.winit.baskinrobbin.salesman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.winit.baskinrobbin.salesman.listeners.CaptureCopressedImageListner;
import com.winit.baskinrobbin.salesman.utilities.ImageLoadingUtils;

public class ImageCompressionAsyncTask extends AsyncTask<String, Void, String>
{
	private Uri contentUri;
	private ImageLoadingUtils utils;
	private Context context;
	private CaptureCopressedImageListner copressedImageListner;
	private String filePath = "";
	public ImageCompressionAsyncTask(Context context, Uri contentUri, CaptureCopressedImageListner copressedImageListner) 
	{
		this.contentUri = contentUri;
		this.context = context;
		this.copressedImageListner = copressedImageListner;
		this.utils = new ImageLoadingUtils(context);
	}
	@Override
	protected String doInBackground(String... params)
	{
		filePath = compressImage(params[0]);
		return filePath;
	}
	
	@Override
	protected void onPostExecute(String result) 
	{			 
		super.onPostExecute(result);
		copressedImageListner.capturedSucceshully(filePath);
	}

	public String compressImage(String imageUri) {
		
		String filePath = getRealPathFromURI(imageUri);
		Bitmap scaledBitmap = null;
		String filename = "";
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;						
		Bitmap bmp = BitmapFactory.decodeFile(filePath,options);
		
		if(bmp != null)
		{
			int actualHeight = options.outHeight>0?options.outHeight : 1;
			int actualWidth = options.outWidth>0?options.outWidth : 1;
			float maxHeight = 816.0f;
			float maxWidth = 612.0f;
			float imgRatio = actualWidth / actualHeight;
			float maxRatio = maxWidth / maxHeight;

			if (actualHeight > maxHeight || actualWidth > maxWidth) {
				if (imgRatio < maxRatio) {
					imgRatio = maxHeight / actualHeight;
					actualWidth = (int) (imgRatio * actualWidth);
					actualHeight = (int) maxHeight;
				} else if (imgRatio > maxRatio) {
					imgRatio = maxWidth / actualWidth;
					actualHeight = (int) (imgRatio * actualHeight);
					actualWidth = (int) maxWidth;
				} else {
					actualHeight = (int) maxHeight;
					actualWidth = (int) maxWidth;     
					
				}
			}
					
			options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
			options.inJustDecodeBounds = false;
			options.inDither = false;
			options.inPurgeable = true;
			options.inInputShareable = true;
			options.inTempStorage = new byte[16*1024];
				
			try{	
				bmp = BitmapFactory.decodeFile(filePath,options);
			}
			catch(OutOfMemoryError exception){
				exception.printStackTrace();
				
			}
			try{
				scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
			}
			catch(OutOfMemoryError exception){
				exception.printStackTrace();
			}
							
			float ratioX = actualWidth / (float) options.outWidth;
			float ratioY = actualHeight / (float)options.outHeight;
			float middleX = actualWidth / 2.0f;
			float middleY = actualHeight / 2.0f;
				
			Matrix scaleMatrix = new Matrix();
			scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

			Canvas canvas = new Canvas(scaledBitmap);
			canvas.setMatrix(scaleMatrix);
			canvas.drawBitmap(bmp, middleX - bmp.getWidth()/2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

							
			ExifInterface exif;
			try {
				exif = new ExifInterface(filePath);
			
				int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
				Log.d("EXIF", "Exif: " + orientation);
				Matrix matrix = new Matrix();
				if (orientation == 6) {
					matrix.postRotate(90);
					Log.d("EXIF", "Exif: " + orientation);
				} else if (orientation == 3) {
					matrix.postRotate(180);
					Log.d("EXIF", "Exif: " + orientation);
				} else if (orientation == 8) {
					matrix.postRotate(270);
					Log.d("EXIF", "Exif: " + orientation);
				}
				scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			FileOutputStream out = null;
			filename = getFilename();
			try {
				out = new FileOutputStream(filename);
				scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return filename;
	}
	
	private String getRealPathFromURI(String contentURI)
	{
		
		Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
		if (cursor == null) {
			return contentUri.getPath();
		} 
		else if(cursor.moveToFirst()) 
		{
			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			return cursor.getString(idx);
		}
		else
			return contentUri.getPath();
	}
	
	public String getFilename() {
		File file = new File(Environment.getExternalStorageDirectory().getPath(), "Bskin/Images");
		if (!file.exists()) {
			file.mkdirs();
		}
		String uriSting = (file.getAbsolutePath() + "/"+ System.currentTimeMillis() + ".jpg");
		return uriSting;

	}
}

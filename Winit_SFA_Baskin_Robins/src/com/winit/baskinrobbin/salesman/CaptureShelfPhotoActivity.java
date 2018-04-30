package com.winit.baskinrobbin.salesman;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.AppStatus;
import com.winit.baskinrobbin.salesman.common.Base64;
import com.winit.baskinrobbin.salesman.common.LocationUtility;
import com.winit.baskinrobbin.salesman.common.LocationUtility.LocationResult;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.MyActivitiesDA;
import com.winit.baskinrobbin.salesman.dataobject.EditDO;
import com.winit.baskinrobbin.salesman.dataobject.JourneyPlanDO;
import com.winit.baskinrobbin.salesman.dataobject.MyActivityDO;
import com.winit.baskinrobbin.salesman.utilities.BitmapsUtiles;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.UploadImage;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;


public class CaptureShelfPhotoActivity extends BaseActivity implements LocationResult{

	private LinearLayout llCaptureShelfphoto;
	private TextView tv_CaptureShelfPhoto;
	private ImageView iv_capturepic;
	private EditText et_AddNotes;
	private String imagepath = "";
	private Button btnsubmit;
	private String taskId;
	private String camera_imagepath;
	public static String APPFOLDERNAME ="Baskin";
	public static String APPFOLDERPATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+APPFOLDERNAME+"/";
	public static String APPMEDIAFOLDERNAME = "BaskinImages";
	public static String APPMEDIAFOLDERPATH = APPFOLDERPATH+APPMEDIAFOLDERNAME+"/"; 
	private int  CAMERA_REQUESTCODE = 2;
	private String taskName;
	private String strTitle;
	private String lat = "", lang = "";
	private LocationUtility locationUtility;
	private Bitmap bitmapProcessed;
	private EditDO editDO ;
	private boolean isEdit = false;
	private JourneyPlanDO journeyPlanDO;
	
//	private Bitmap mBtBitmap = null;
	@Override
	public void initialize() 
	{
		if(getIntent().getExtras() != null)
			journeyPlanDO = (JourneyPlanDO) getIntent().getExtras().get("object");
		
		llCaptureShelfphoto = (LinearLayout) inflater.inflate(R.layout.captureshelfphoto, null);
		tv_CaptureShelfPhoto = (TextView) llCaptureShelfphoto.findViewById(R.id.tv_CaptureShelfPhoto);
		iv_capturepic = (ImageView) llCaptureShelfphoto.findViewById(R.id.iv_capturepic);
		et_AddNotes = (EditText) llCaptureShelfphoto.findViewById(R.id.et_AddNotes);
		btnsubmit = (Button) llCaptureShelfphoto.findViewById(R.id.btnsubmit);
		
		llBody.addView(llCaptureShelfphoto, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		imagepath = getIntent().getStringExtra("imagepath");
		taskId	  = getIntent().getStringExtra("taskId");
		strTitle  = getIntent().getStringExtra("strTitle");
		taskName  = getIntent().getStringExtra("taskName");
		isEdit	  = getIntent().getBooleanExtra("isEdit", false);
		
		
		btnsubmit.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if(!et_AddNotes.getText().toString().trim().equalsIgnoreCase(""))
				{
					showLoader("Please wait...");
					new Thread(new Runnable() 
					{
						@Override
						public void run() 
						{
							MyActivityDO activityDO = new MyActivityDO();
							
							activityDO.activityId     = System.currentTimeMillis()+"";
							activityDO.taskID	      = taskId;
							activityDO.isVerified     = "false";
							activityDO.pushStatus 	  = "0";
							activityDO.desccription   = et_AddNotes.getText().toString();
							activityDO.customerSiteID = journeyPlanDO.customerId;
							activityDO.salemanCode	  = preference.getStringFromPreference(Preference.EMP_NO, "");
							activityDO.imagePath	  = getBsae64Image(bitmapProcessed);
//							String serverimage = getBsae64Image(bitmapProcessed);
							String serverimagePath = BitmapsUtiles.saveVerifySignature(bitmapProcessed);
							activityDO.serverimagePath    = new UploadImage().uploadImage(CaptureShelfPhotoActivity.this, serverimagePath, ServiceURLs.assetservicerequest, true);
							activityDO.latitude		 = lat;
							activityDO.langitude     = lang;
							activityDO.strDate       = CalendarUtils.getCurrentDate();
							activityDO.taskName      = taskName;
							activityDO.strCustomerName  = journeyPlanDO.siteName;;
							new MyActivitiesDA().insertUserInfo(activityDO);
							new MyActivitiesDA().updateTask(taskId, CalendarUtils.getCurrentDateAsString());
							runOnUiThread(new Runnable() 
							{
								@Override
								public void run()
								{
									showCustomDialog(CaptureShelfPhotoActivity.this, "Success!", "You have executed the task. The details have been submitted to your manager for review.", getString(R.string.OK), null, "success");
									hideLoader();
									uploadData(AppStatus.ALL, AppStatus.TODAY_DATA);
								}
							});
						}
					}).start();
				}
				else
				{
					showCustomDialog(CaptureShelfPhotoActivity.this, getString(R.string.warning), "Please add notes.", getString(R.string.OK), null, "");
				}
			}
		});
		
		tv_CaptureShelfPhoto.setText(strTitle);
		
		locationUtility  = new LocationUtility(CaptureShelfPhotoActivity.this);
		
		locationUtility.getLocation(CaptureShelfPhotoActivity.this);
		
		if(isEdit)
		{
			showLoader("Please wait...");
			new Thread(new Runnable() {
				
				@Override
				public void run()
				{
					editDO = new MyActivitiesDA().getNote(taskId);
//					final String note = new MyActivitiesDA().getNote(taskId);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							hideLoader();
							et_AddNotes.setText(editDO.strNote);
							iv_capturepic.setImageBitmap(bitmapProcessed = getFromBsae64Image(editDO.imagePath));
						}
					});
				}
			}).start();
		}
		else
		{
			File f = new File(imagepath);
			Bitmap bmp = BitmapsUtiles.decodeSampledBitmapFromResource(f, 720,1280);
		        bitmapProcessed = BitmapsUtiles.processBitmap2(bmp, "29.378799", "47.979784", "");
		    if(bmp!=null && !bmp.isRecycled())
		    	bmp.recycle();
		    
		    iv_capturepic.setBackgroundResource(0);
		    iv_capturepic.setImageBitmap(bitmapProcessed);
		}
		
		iv_capturepic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0)
			{
				camera_imagepath = APPMEDIAFOLDERPATH+ "baskin"+ System.currentTimeMillis()+ ".jpg";
				Uri outputFileUri = Uri.fromFile(new File(camera_imagepath));
				Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileUri);
				startActivityForResult(intent,CAMERA_REQUESTCODE);
				
			}
		});
		
		
	    
//		Bitmap bitmap = Bitmap.createScaledBitmap(getbitmap(imagepath),350, 200, true);
//		iv_capturepic.setImageBitmap(bitmap);
//		Bitmap bitmap = Bitmap.createScaledBitmap(getbitmap(imagepath),350, 200, true);
//		iv_capturepic.setImageBitmap(bitmap);
	}
	
	private Bitmap getbitmap(String imagepath) 
	{
		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
		bmpFactoryOptions.inScaled = false;
		Bitmap bitmap = BitmapFactory.decodeFile(imagepath, bmpFactoryOptions);
		return bitmap;
	}
	
	
	@Override
	public void onButtonYesClick(String from) 
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("success"))
		{
			finish();
			AppConstants.isTaskCompleted = true;
		}
	}

	
	public void processImageAndUpload()
	{
		Bitmap bitmapThumbnail = null;
		
    	BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 8;
	    File f = new File(imagepath);
	    Bitmap bmp = decodeFile(f);// BitmapFactory.decodeFile(f.getPath(), options);
	   
	    bmp = flip(bmp, getImageRotation(f.getPath()));
	    
	    
	    
	    if(bmp!=null && ! bmp.isRecycled())
		 {
			WeakReference<Bitmap> weakBitmap = new WeakReference<Bitmap>(bmp);
			if(weakBitmap.get() != null && !weakBitmap.get().isRecycled())
			{
				weakBitmap.get().recycle();
			}
		 }
		
    	try 
	    {
	    	DropBoxUploader dropboxUploader = new DropBoxUploader("");
	    	new Thread(dropboxUploader).start();
	    }
	    catch (Exception e) 
	    {
	    	e.printStackTrace();
	    }
	}
	
	class DropBoxUploader implements Runnable
	{
		private String imageName;
		
		public DropBoxUploader(String imageName)
		{
			this.imageName = imageName;
		}
		
		@Override
		public void run() 
		{
			try
			{
				
				File f = new File(imagepath);
	   	        Bitmap bmp = decodeFile(f);//BitmapFactory.decodeFile(f.getPath());
	   	        
	   	        bmp = flip(bmp, getImageRotation(f.getPath()));
	   	        
	   	        bmp = BitmapsUtiles.getResizedBmp(bmp, 720, 1280);
	   	        
			    Bitmap bitmapProcessed = BitmapsUtiles.processBitmap2(bmp, lat, lang, "");
			    if(bmp!=null && !bmp.isRecycled())
			    	bmp.recycle();
		    	
		    	 if(bitmapProcessed!=null && ! bitmapProcessed.isRecycled())
				 {
					WeakReference<Bitmap> weakBitmap = new WeakReference<Bitmap>(bitmapProcessed);
					if(weakBitmap.get() != null && !weakBitmap.get().isRecycled())
					{
						weakBitmap.get().recycle();
					}
				 }
		    	 
		    	 
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	private Bitmap decodeFile(File f)
	{
	    try 
	    {
	        //Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

	        //The new size we want to scale to
	        final int REQUIRED_SIZE=90;

	        //Find the correct scale value. It should be the power of 2.
	        int scale=1;
	        	Log.e("outWidth", ""+o.outWidth);
	        	Log.e("scale", ""+scale);
//	        	Log.e("devicewidth", ""+AppConstants.DEVICE_WIDTH);
	        	
	        	if(AppConstants.DEVICE_WIDTH == 0)
	        	{
	        		Display display = ((WindowManager)CaptureShelfPhotoActivity.this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	        		AppConstants.DEVICE_WIDTH 	= display.getWidth();
	        		AppConstants.DEVICE_HEIGHT 	= display.getHeight();
//		    		Log.e("changed devicewidth", ""+AppConstants.DEVICE_WIDTH);
	        	}
		        	while(o.outWidth/scale/2>=AppConstants.DEVICE_WIDTH /*&& o.outHeight/scale/2>=AppConstants.DEVICE_HEIGHT*/)
			            scale*=2;

	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize=scale;
	        
	        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	        
	    } catch (FileNotFoundException e) {}
	    return null;
	}
	
	public int getImageRotation(String ImagePath)
	{
		
		  int rotate = 0;
          try 
          {
//              getContentResolver().notifyChange(imageUri, null);
              File imageFile = new File(ImagePath);
              ExifInterface exif = new ExifInterface(
                      imageFile.getAbsolutePath());
              int orientation = exif.getAttributeInt(
                      ExifInterface.TAG_ORIENTATION,
                      ExifInterface.ORIENTATION_NORMAL);

              switch (orientation) {
              case ExifInterface.ORIENTATION_ROTATE_270:
                  rotate = 270;
                  break;
              case ExifInterface.ORIENTATION_ROTATE_180:
                  rotate = 180;
                  break;
              case ExifInterface.ORIENTATION_ROTATE_90:
                  rotate = 90;
                  break;
              }
              Log.v("TAG", "Exif orientation: " + orientation);
          } 
          catch (Exception e) 
          {
              e.printStackTrace();
          }
          Log.v("TAG", "rotation " + rotate);
          return rotate;

	}
	
	Bitmap flip(Bitmap d, int rotate)
	{
	    Matrix m = new Matrix();
	    m.postRotate(rotate);
	    Bitmap src = d;
	    Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
//	    dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
	    return dst;
	}
	
	private static String getBsae64Image(Bitmap mBitmap)
	{
		
		ByteArrayOutputStream streams = new ByteArrayOutputStream();
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, streams);
		
		return Base64.encodeBytes(streams.toByteArray());
	}
	
	private static Bitmap getFromBsae64Image(String imagePath)
	{
		//customer signature
		
		byte[] bytes = null;
		try {
			bytes = Base64.decode(imagePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes , 0, bytes .length);
		
		
//		
//		BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
//		bmpFactoryOptions.inScaled = false;
//		bitmap = BitmapFactory.decodeFile(imagePath, bmpFactoryOptions);
//		
//		ByteArrayOutputStream streams = new ByteArrayOutputStream();
//		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, streams);
		
		Bitmap bmp = BitmapsUtiles.getResizedBitmap(bitmap, 500, 800);
		return bmp;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		
		 if (resultCode == RESULT_OK && requestCode == CAMERA_REQUESTCODE)
		{
			switch (resultCode) 
			{
			case RESULT_CANCELED:
				Log.i("Camera", "User cancelled");
				break;

			case RESULT_OK:
				
				imagepath = camera_imagepath;
				
				File f = new File(imagepath);
				Bitmap bmp = BitmapsUtiles.decodeSampledBitmapFromResource(f, 720,1280);
	   	        bitmapProcessed = BitmapsUtiles.processBitmap2(bmp, lat, lang, "");
			    if(bmp!=null && !bmp.isRecycled())
			    	bmp.recycle();
			    
			    iv_capturepic.setBackgroundResource(0);
			    iv_capturepic.setImageBitmap(bitmapProcessed);
			    
//				Bitmap bitmap1 = Bitmap.createScaledBitmap(getbitmap(imagepath),350, 200, true);
//				iv_capturepic.setImageBitmap(bitmap1);
//				processImageAndUpload();
				break;
			}

		}
		 
		}
	
	@Override
	public void gotLocation(Location loc) 
	{
		if(loc!=null)
		{
			lat	 = diffLagLong.format(loc.getLatitude());
			lang = diffLagLong.format(loc.getLongitude());
			
			
			DisplayMetrics metrics = getResources().getDisplayMetrics();
			
			Log.e("Density ", metrics.density+"");
			
			AppConstants.DEVICE_DENSITY = metrics.density;
//			processImageAndUpload();
			
			runOnUiThread(new Runnable()
			{
				@Override
				public void run() 
				{
					if(!isEdit)
					{
						File f = new File(imagepath);
						Bitmap bmp = BitmapsUtiles.decodeSampledBitmapFromResource(f, 720,1280);
						bitmapProcessed = BitmapsUtiles.processBitmap2(bmp, lat, lang, "");
						if(bmp!=null && !bmp.isRecycled())
							bmp.recycle();
						iv_capturepic.setBackgroundResource(0);
						iv_capturepic.setImageBitmap(bitmapProcessed);
					}
				    
				    
//					tv_longi.setText("Lattitude :"+lat);
//					tv_latti.setText("Longitude :"+lang);
//					tv_date.setText("Date :"+CalendarUtils.getCurrentDate());
				}
			});
		}
	}
	
//	@Override
//	public void onBackPressed() 
//	{
//		super.btnCheckOut.performClick();
//	}
}

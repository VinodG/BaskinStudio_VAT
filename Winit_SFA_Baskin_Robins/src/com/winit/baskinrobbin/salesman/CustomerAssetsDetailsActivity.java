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
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Base64;
import com.winit.baskinrobbin.salesman.common.LocationUtility;
import com.winit.baskinrobbin.salesman.common.LocationUtility.LocationResult;
import com.winit.baskinrobbin.salesman.dataaccesslayer.AssetDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.AssetTrackingDA;
import com.winit.baskinrobbin.salesman.dataobject.AssetDO;
import com.winit.baskinrobbin.salesman.imageloader.UrlImageViewCallback;
import com.winit.baskinrobbin.salesman.imageloader.UrlImageViewHelper;
import com.winit.baskinrobbin.salesman.utilities.BitmapsUtiles;
import com.winit.baskinrobbin.salesman.utilities.UploadImage;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class CustomerAssetsDetailsActivity extends BaseActivity implements OnClickListener, LocationResult
{

	private LinearLayout llAssetsList,llAssetImg,llAssetTempImg ;
	private TextView tvCustomerName,tvAssetName,tvCapacity,tvInstallation,tvLastService,tv_assetCapture,tvsubject,tvdescription,tv_assetTemp;
	private Button btnAssetCapture , btnContinue ,btnServiceRequest,btnCaptureTemp;
	private AssetDO asset;
	private ImageView ivAssetImage,ivAssetPic,ivAssetCaptureImage,ivAssettempImage;
	private EditText etAssetTemp;
	private String camera_imagepath;
	private String camera_imagepathtemp;
	public static String APPFOLDERNAME ="Assets";
	public static String APPFOLDERPATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+APPFOLDERNAME+"/";
	public static String APPMEDIAFOLDERNAME = "Assets Images";
	public static String APPMEDIAFOLDERPATH = APPFOLDERPATH+APPMEDIAFOLDERNAME+"/";
	private int  CAMERA_REQUESTCODE = 2;
	private int  CAMERA_REQUESTCODE1 = 3;
	private String imagepath = "";
	private String temparatureimagepath = "";
	private String imagepathfromdb = "";
	private String imagepathformultipart = "";
	private String imagepathformultipart1 = "";
	private String imagepathforassetservice = "";
	private String subjectforassetservice ="";
	private String descriptionforassetservice ="";
	private String assetTemp ="";
	private Bitmap bitmapProcessed ;
	private String lat = "", lang = "";
	private LocationUtility locationUtility;
	private String siteName = "";
	
	@Override
	public void initialize() 
	{
		llAssetsList			=	(LinearLayout)inflater.inflate(R.layout.asset_details,null);
		llBody.addView(llAssetsList,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		initializeControls();
		
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		btnAssetCapture.setOnClickListener(this);
		btnServiceRequest.setOnClickListener(this);
		btnContinue.setOnClickListener(this);
		btnCaptureTemp.setOnClickListener(this);
		
		if(getIntent().hasExtra("asset"))
			asset = (AssetDO) getIntent().getSerializableExtra("asset");
		if(getIntent().hasExtra("siteName"))
			siteName = getIntent().getStringExtra("siteName");
		
		if(asset != null)
		{
			AssetDO assetDO = new AssetDA().getAssetByAssetId(asset.assetId);
			assetDO.siteNo = asset.siteNo;
			asset = assetDO;
			
			tvCustomerName.setText(siteName);
			tvAssetName.setText(asset.name);
			tvCapacity.setText(asset.capacity);
			tvInstallation.setText(asset.installationDate);
			tvLastService.setText(asset.lastServiceDate);
			
			if(asset.assetType.equalsIgnoreCase("Freezer"))
			{
				ivAssetPic.setBackgroundResource(R.drawable.freez);
			}
			else if(asset.assetType.equalsIgnoreCase("Chiller"))
			{
				ivAssetPic.setBackgroundResource(R.drawable.chiller);
			}
			
//			if(asset.imagePath != null && asset.imagePath.length() > 0)
//			{
				if(asset.imagePath.startsWith("BRAsset"))
				{
					
					imagepathfromdb = "http://uat.winitsoftware.com/baskinrobbinsv3/Data/AssetImage/"+asset.imagePath;
					 
						UrlImageViewHelper.setUrlDrawable(ivAssetImage, imagepathfromdb, R.drawable.freez, new UrlImageViewCallback() {
			                @Override
			                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
			                    if (!loadedFromCache) {
			                        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
			                        scale.setDuration(300);
			                        scale.setInterpolator(new OvershootInterpolator());
			                        imageView.startAnimation(scale);
			                    }
			                }
			            });
				}
//				else
//				{
//					imagepathfromdb = asset.imagePath;
//					setImage(imagepathfromdb);
//				}
//				
//				
//			}
		}
		
		
		if(AppConstants.assetbarcodeimagePath!=null && AppConstants.assetbarcodeimagePath.toString().length()>0)
		{
			ivAssetImage.setBackgroundResource(0);
		    ivAssetImage.setImageBitmap(AppConstants.assetbarcodeimagePath);
		}
		if(AppConstants.assettempimagePath!=null && AppConstants.assettempimagePath.toString().length()>0)
		{
			ivAssettempImage.setBackgroundResource(0);
			ivAssettempImage.setImageBitmap(AppConstants.assettempimagePath);
		}
		setTypeFace(llAssetsList);
		
		try
		{
			new File(APPMEDIAFOLDERPATH).mkdirs();
		} 
		catch (Exception e) {
			// TODO: handle exception
		}
		
		locationUtility  = new LocationUtility(CustomerAssetsDetailsActivity.this);
		
		locationUtility.getLocation(CustomerAssetsDetailsActivity.this);
	}
	
	private void initializeControls()
	{
		tvAssetName 		= (TextView)llAssetsList.findViewById(R.id.tvAssetName);
		tvCustomerName 		= (TextView)llAssetsList.findViewById(R.id.tvCustomerName);
		tvCapacity 			= (TextView)llAssetsList.findViewById(R.id.tvCapacity);
		tvInstallation 		= (TextView)llAssetsList.findViewById(R.id.tvInstallation);
		tvLastService 		= (TextView)llAssetsList.findViewById(R.id.tvLastService);
		ivAssetImage		= (ImageView)llAssetsList.findViewById(R.id.ivAssetImage);
		ivAssettempImage	= (ImageView)llAssetsList.findViewById(R.id.ivAssettempImage);
		ivAssetPic			= (ImageView)llAssetsList.findViewById(R.id.ivAssetPic);
		btnAssetCapture		= (Button)llAssetsList.findViewById(R.id.btnAssetCapture);
		btnContinue			= (Button)llAssetsList.findViewById(R.id.btnContinue);
		btnCaptureTemp		= (Button)llAssetsList.findViewById(R.id.btnCaptureTemp);
		btnServiceRequest   = (Button)llAssetsList.findViewById(R.id.btnServiceRequest);
		ivAssetCaptureImage	= (ImageView)llAssetsList.findViewById(R.id.ivAssetCaptureImage);
		tv_assetCapture     = (TextView)llAssetsList.findViewById(R.id.tv_assetCapture);
		tv_assetTemp    	= (TextView)llAssetsList.findViewById(R.id.tv_assetTemp);
		llAssetImg    		= (LinearLayout)llAssetsList.findViewById(R.id.llAssetImg);
		llAssetTempImg		= (LinearLayout)llAssetsList.findViewById(R.id.llAssetTempImg);
		tvsubject     		= (TextView)llAssetsList.findViewById(R.id.tvsubject);
		etAssetTemp         = (EditText)llAssetsList.findViewById(R.id.etAssetTemp);
		tvdescription		= (TextView)llAssetsList.findViewById(R.id.tvdescription);
	}

	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			case R.id.btnAssetCapture:
				performCaptureImage();
				break;
			case R.id.btnServiceRequest:
				performServiceRequest();
				break;
			case R.id.btnCaptureTemp:
				performCaptureTemparature();
				break;
			case R.id.btnContinue:
				if(imagepath != null && imagepath.length()>0)
				{
					new AssetDA().updateAssetImage(asset.assetId, imagepath);
					
				}
				
				finish();
					break;
		}
	}


	private void performCaptureTemparature()
	{
		if(!etAssetTemp.getText().toString().trim().equalsIgnoreCase(""))
		{
			assetTemp = etAssetTemp.getText().toString();
			camera_imagepathtemp = APPMEDIAFOLDERPATH+ "baskin"+ System.currentTimeMillis()+ ".jpg";
			Uri outputFileUri = Uri.fromFile(new File(camera_imagepathtemp));
			Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileUri);
			startActivityForResult(intent,CAMERA_REQUESTCODE1);
		}
		else
		{
			showCustomDialog(CustomerAssetsDetailsActivity.this, getString(R.string.warning), "Please enter Asset temperature before capturing.", getString(R.string.OK), null, "");
		}
			

		
		
	}

	private void performServiceRequest()
	{

		Intent in = new Intent(CustomerAssetsDetailsActivity.this, CustomerAssetService.class);
		in.putExtra("Siteno", asset.siteNo);
		startActivityForResult(in,2000);
	}

	private void performCaptureImage()
	{
		camera_imagepath = APPMEDIAFOLDERPATH+ "baskin"+ System.currentTimeMillis()+ ".jpg";
		Uri outputFileUri = Uri.fromFile(new File(camera_imagepath));
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileUri);
		startActivityForResult(intent,CAMERA_REQUESTCODE);
		
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
				
				setImage(imagepath);
				
				
//				imagepathformultipart=imagepath;
//			    String serverimagepath = new UploadImage().uploadImage(CustomerAssetsDetailsActivity.this, imagepathformultipart, ServiceURLs. assetservicerequest, true);
//				
//				Log.e("multipart url", serverimagepath);
//				new AssetTrackingDA().updateAssetPlanogramImage(asset.siteNo,serverimagepath);
				
//				Bitmap bitmap1 = Bitmap.createScaledBitmap(getbitmap(imagepath),350, 200, true);
//				iv_capturepic.setImageBitmap(bitmap1);
//				processImageAndUpload();
				
				break;
			}

		}
		 else if (resultCode == RESULT_OK && requestCode == CAMERA_REQUESTCODE1)
			{
				switch (resultCode) 
				{
				case RESULT_CANCELED:
					Log.i("Camera", "User cancelled");
					break;

				case RESULT_OK:
					
					temparatureimagepath = camera_imagepathtemp;
					tv_assetTemp.setVisibility(View.VISIBLE);
					llAssetTempImg.setVisibility(View.VISIBLE);
					setTempImage(temparatureimagepath);
					
					
//					imagepathformultipart=imagepath;
//				    String serverimagepath = new UploadImage().uploadImage(CustomerAssetsDetailsActivity.this, imagepathformultipart, ServiceURLs. assetservicerequest, true);
//					
//					Log.e("multipart url", serverimagepath);
//					new AssetTrackingDA().updateAssetPlanogramImage(asset.siteNo,serverimagepath);
					
//					Bitmap bitmap1 = Bitmap.createScaledBitmap(getbitmap(imagepath),350, 200, true);
//					iv_capturepic.setImageBitmap(bitmap1);
//					processImageAndUpload();
					
					break;
				}

			}
		 else if (resultCode == RESULT_OK && requestCode == 2000)
			{
			 
			 	imagepathforassetservice	 = data.getStringExtra("imagepath");
				subjectforassetservice	  	 = data.getStringExtra("subject");
				descriptionforassetservice   = data.getStringExtra("description");
				
				if(imagepathforassetservice !=null && imagepathforassetservice.length()>0)
				{
					llAssetImg.setVisibility(View.VISIBLE);
					tv_assetCapture.setVisibility(View.VISIBLE);
					setImage1(imagepathforassetservice);
					tvsubject.setText(subjectforassetservice);
					tvdescription.setText(descriptionforassetservice);
				}
			 
			 
			}
		}
	
	private void setImage(String imagePath)
	{
		File f = new File(imagePath);
		Bitmap bmp = BitmapsUtiles.decodeSampledBitmapFromResource(f, 720,1280);
	        bitmapProcessed = BitmapsUtiles.processBitmap2(bmp, lat, lang, "");
	    if(bmp!=null && !bmp.isRecycled())
	    	bmp.recycle();
	    
	    ivAssetImage.setBackgroundResource(0);
	    ivAssetImage.setImageBitmap(bitmapProcessed);
	    
	    imagepathformultipart=BitmapsUtiles.saveVerifySignature(bitmapProcessed);
		AppConstants.assetbarcodeimagePath = bitmapProcessed;
		showLoader(getString(R.string.loading));
		new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				  
			String serverimagepath = new UploadImage().uploadImage(CustomerAssetsDetailsActivity.this, imagepathformultipart, ServiceURLs. assetservicerequest, true);
					
			Log.e("multipart url", serverimagepath);
			new AssetTrackingDA().updateAssetPlanogramImage(asset.siteNo,serverimagepath);
				runOnUiThread(new  Runnable() 
				{
					public void run() 
					{
						hideLoader();
						
					}
				});
			}
		}).start();
		
//	    imagepathformultipart=imagePath;
//	    String serverimagepath = new UploadImage().uploadImage(CustomerAssetsDetailsActivity.this, imagepathformultipart, ServiceURLs. assetservicerequest, true);
//		
//		Log.e("multipart url", serverimagepath);
//		new AssetTrackingDA().updateAssetPlanogramImage(asset.siteNo,serverimagepath);
//	    
	}
	private void setImage1(String imagePath)
	{
		File f = new File(imagePath);
		Bitmap bmp = BitmapsUtiles.decodeSampledBitmapFromResource(f, 720,1280);
	        bitmapProcessed = BitmapsUtiles.processBitmap2(bmp, lat, lang, "");
	    if(bmp!=null && !bmp.isRecycled())
	    	bmp.recycle();
	    
	    ivAssetCaptureImage.setBackgroundResource(0);
	    ivAssetCaptureImage.setImageBitmap(bitmapProcessed);
	}

	private void setTempImage(String imagePath)
	{
		File f = new File(imagePath);
		Bitmap bmp = BitmapsUtiles.decodeSampledBitmapFromResource(f, 720,1280);
	        bitmapProcessed = BitmapsUtiles.processBitmap2(bmp, lat, lang, "");
	    if(bmp!=null && !bmp.isRecycled())
	    	bmp.recycle();
	    
	    ivAssettempImage.setBackgroundResource(0);
	    ivAssettempImage.setImageBitmap(bitmapProcessed);
	    
	    imagepathformultipart1=BitmapsUtiles.saveVerifySignature(bitmapProcessed);
		AppConstants.assettempimagePath = bitmapProcessed;
		showLoader(getString(R.string.loading));
		new Thread(new Runnable() {
			
			@Override
			public void run() 
			{
				  
			String serverimagepath1 = new UploadImage().uploadImage(CustomerAssetsDetailsActivity.this, imagepathformultipart1, ServiceURLs. assetservicerequest, true);
					
			Log.e("multipart url", serverimagepath1);
			new AssetTrackingDA().updateAssetTemparatureImage(asset.siteNo,serverimagepath1,assetTemp);
				runOnUiThread(new  Runnable() 
				{
					public void run() 
					{
						hideLoader();
						
					}
				});
			}
		}).start();
		
		
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
	        		Display display = ((WindowManager)CustomerAssetsDetailsActivity.this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
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
//						File f = new File(imagepath);
//						Bitmap bmp = BitmapsUtiles.decodeSampledBitmapFromResource(f, 720,1280);
//						bitmapProcessed = BitmapsUtiles.processBitmap2(bmp, lat, lang, "");
//						if(bmp!=null && !bmp.isRecycled())
//							bmp.recycle();
//						ivAssetImage.setBackgroundResource(0);
//						ivAssetImage.setImageBitmap(bitmapProcessed);
					
				    
				    
//					tv_longi.setText("Lattitude :"+lat);
//					tv_latti.setText("Longitude :"+lang);
//					tv_date.setText("Date :"+CalendarUtils.getCurrentDate());
				}
			});
		}
	}
}
			

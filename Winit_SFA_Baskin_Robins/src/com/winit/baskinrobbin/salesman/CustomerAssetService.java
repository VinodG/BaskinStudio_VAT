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

import com.winit.baskinrobbin.parsers.PostAssetServiceRequestParser;
import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Base64;
import com.winit.baskinrobbin.salesman.common.LocationUtility;
import com.winit.baskinrobbin.salesman.common.LocationUtility.LocationResult;
import com.winit.baskinrobbin.salesman.dataaccesslayer.AssetServiceDA;
import com.winit.baskinrobbin.salesman.dataaccesslayer.CustomerDA;
import com.winit.baskinrobbin.salesman.dataobject.AssetServiceDO;
import com.winit.baskinrobbin.salesman.dataobject.SurveyCustomerDeatislDO;
import com.winit.baskinrobbin.salesman.utilities.BitmapsUtiles;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;
import com.winit.baskinrobbin.salesman.utilities.UploadImage;
import com.winit.baskinrobbin.salesman.webAccessLayer.BuildXMLRequest;
import com.winit.baskinrobbin.salesman.webAccessLayer.ConnectionHelper;
import com.winit.baskinrobbin.salesman.webAccessLayer.ServiceURLs;

public class CustomerAssetService extends BaseActivity implements LocationResult{

	private LinearLayout llCustomerAssetService;
	private TextView tv_assestservice,tv_textforcapture;
	private ImageView iv_assetcapturepic;
	private EditText et_Subject,et_Description;
	private String imagepath = "";
	private String imagepathformultipart = "";
	private String siteNo = "";
	private Button btnsubmit;
	private String camera_imagepath;
	public static String APPFOLDERNAME ="Baskin";
	public static String APPFOLDERPATH = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+APPFOLDERNAME+"/";
	public static String APPMEDIAFOLDERNAME = "BaskinImages";
	public static String APPMEDIAFOLDERPATH = APPFOLDERPATH+APPMEDIAFOLDERNAME+"/"; 
	private int  CAMERA_REQUESTCODE = 2;
	private String subject="",description="";
	private String lat = "", lang = "";
	private LocationUtility locationUtility;
	private Bitmap bitmapProcessed;
	private SurveyCustomerDeatislDO cusdetailDo;
//	private Bitmap mBtBitmap = null;
	@Override
	public void initialize() 
	{
		
		llCustomerAssetService = (LinearLayout) inflater.inflate(R.layout.assetservice, null);
		tv_assestservice = (TextView) llCustomerAssetService.findViewById(R.id.tv_assestservice);
		tv_textforcapture = (TextView) llCustomerAssetService.findViewById(R.id.tv_textforcapture);
		iv_assetcapturepic = (ImageView) llCustomerAssetService.findViewById(R.id.iv_assetcapturepic);
		et_Subject = (EditText) llCustomerAssetService.findViewById(R.id.et_Subject);
		et_Description = (EditText) llCustomerAssetService.findViewById(R.id.et_Description);
		
		btnsubmit = (Button) llCustomerAssetService.findViewById(R.id.btnsubmit);
		btnCheckOut.setVisibility(View.GONE);
		ivLogOut.setVisibility(View.GONE);
		llBody.addView(llCustomerAssetService, LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		
		try {
			new File(APPMEDIAFOLDERPATH).mkdirs();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(getIntent().hasExtra("Siteno"))
			siteNo = getIntent().getStringExtra("Siteno");
		
		btnsubmit.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				if(!et_Subject.getText().toString().trim().equalsIgnoreCase(""))
				{
					subject = et_Subject.getText().toString();
				}
				else
				{
					showCustomDialog(CustomerAssetService.this, getString(R.string.warning), "Please enter subject.", getString(R.string.OK), null, "");
				}
				
				if(!et_Description.getText().toString().trim().equalsIgnoreCase(""))
				{
					description = et_Description.getText().toString();
				}
				else
				{
					showCustomDialog(CustomerAssetService.this, getString(R.string.warning), "Please enter description.", getString(R.string.OK), null, "");
				}
					
				
				Log.e("subject", subject);
				Log.e("description", description);
				
				imagepathformultipart = imagepath;
				
				cusdetailDo = new SurveyCustomerDeatislDO();
				cusdetailDo = new CustomerDA().getCustomerSurveyDetails(preference.getStringFromPreference(preference.EMP_NO, ""));
				
				if(cusdetailDo.visitCode.contains("-"))
					cusdetailDo.visitCode = cusdetailDo.visitCode.replace("-", "");
				if(cusdetailDo.visitCode.contains("T"))
					cusdetailDo.visitCode = cusdetailDo.visitCode.replace("T", "");
				if(cusdetailDo.visitCode.contains(":"))
					cusdetailDo.visitCode = cusdetailDo.visitCode.replace(":", "");
				
				
				else
				{
					showLoader("Please wait...");
					new Thread(new Runnable() 
					{
						@Override
						public void run() 
						{
							AssetServiceDO assetserDo = new AssetServiceDO();
							
							assetserDo.assetServiceRequestId="0";
							
							assetserDo.userCode		=preference.getStringFromPreference(preference.EMP_NO, "");
							assetserDo.siteNo	=siteNo;
							assetserDo.multipart_Url=imagepath;
								String serverimagepath = new UploadImage().uploadImage(CustomerAssetService.this, imagepathformultipart, ServiceURLs.assetservicerequest, true);
									Log.e("multipart url", serverimagepath);
							assetserDo.requestDate = CalendarUtils.getCurrentDateTime();
							assetserDo.requestImage=serverimagepath;
							assetserDo.journeyCode =CalendarUtils.getCurrentDateAsString();
							assetserDo.visitCode =cusdetailDo.visitCode;
							assetserDo.status="0";
							assetserDo.notes=subject+":"+description;
							assetserDo.isApproved="false";
							assetserDo.serviceRequestAppId=StringUtils.getUniqueUUID();
							if(assetserDo.serviceRequestAppId.contains("-"))
								assetserDo.serviceRequestAppId = assetserDo.serviceRequestAppId.replace("-", "");
							
							
							new AssetServiceDA().insertAsset(assetserDo);
							if(assetserDo != null)
							{
								final PostAssetServiceRequestParser postAssetServiceRequestParser 	= new PostAssetServiceRequestParser(CustomerAssetService.this);
								new ConnectionHelper(null).sendRequest(CustomerAssetService.this, BuildXMLRequest.PostAssetServiceRequest(assetserDo), postAssetServiceRequestParser, ServiceURLs.PostAssetServiceRequest, preference);
							}
							runOnUiThread(new Runnable() 
							{
								@Override
								public void run()
								{
									showCustomDialog(CustomerAssetService.this, "Successful", "You have executed the Service Request. The details have been submitted to your manager for review.", getString(R.string.OK), null, "success");
									hideLoader();
								}
							});
						}
					}).start();
				}
					
				}
				
				
				
					
		});
		
		
		locationUtility  = new LocationUtility(CustomerAssetService.this);
		
		locationUtility.getLocation(CustomerAssetService.this);
		
//		
		
		iv_assetcapturepic.setOnClickListener(new OnClickListener() {
			
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
			Intent in = new Intent(CustomerAssetService.this, CustomerAssetsDetailsActivity.class);
			in.putExtra("subject", subject);
			in.putExtra("description", description);
			in.putExtra("imagepath", imagepath);
			setResult(RESULT_OK, in);
			finish();
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
	        		Display display = ((WindowManager)CustomerAssetService.this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
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
			    
			    iv_assetcapturepic.setBackgroundResource(0);
			    iv_assetcapturepic.setImageBitmap(bitmapProcessed);
			    
			    tv_textforcapture.setVisibility(View.GONE);
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
//					if(!isEdit)
//					{
//						File f = new File(imagepath);
//						Bitmap bmp = BitmapsUtiles.decodeSampledBitmapFromResource(f, 720,1280);
//						bitmapProcessed = BitmapsUtiles.processBitmap2(bmp, lat, lang, "");
//						if(bmp!=null && !bmp.isRecycled())
//							bmp.recycle();
//						iv_capturepic.setBackgroundResource(0);
//						iv_capturepic.setImageBitmap(bitmapProcessed);
//					}
				    
				    
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

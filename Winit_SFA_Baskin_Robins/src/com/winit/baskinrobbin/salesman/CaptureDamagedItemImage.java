package com.winit.baskinrobbin.salesman;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.LocationUtility;
import com.winit.baskinrobbin.salesman.common.LocationUtility.LocationResult;
import com.winit.baskinrobbin.salesman.utilities.BitmapsUtiles;
import com.winit.baskinrobbin.salesman.utilities.ImageLoadingUtils;

public class CaptureDamagedItemImage extends BaseActivity implements LocationResult
{
	private GridView gvImages;
	private LinearLayout llCaptureImages;
	
	private  Uri mCapturedImageURI;
	private static final int CAMERA_PIC_REQUEST = 2500;
	
	private ArrayList<String> vecImagePaths;
	private GridViewAdapter gridViewAdapter;
	
	private Button btnTakeNew, btnContinue;
	private String camera_imagepath = "";
	
//	private ProductDO productDO;
	private int position;
	private TextView tvCaptureDamageTitle;
	private int mPosition = 0;
	private boolean fromActivity = false;
	private String itemCode = "", desc = "";
	private boolean isButton = false;
	private LocationUtility locationUtility;
	private String lat, lang;
	
	private int width = 0;
	
	
	private ImageLoadingUtils utils;
	private LruCache<String, Bitmap> memoryCache;
	@Override
	public void initialize()
	{
		llCaptureImages = (LinearLayout) inflater.inflate(R.layout.capture_damaeged_image, null);
		llBody.addView(llCaptureImages, new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		isButton = false;
		if(getIntent().getExtras() != null)
		{
			vecImagePaths = (ArrayList<String>) getIntent().getExtras().get("vecImagePaths");
			position      = getIntent().getExtras().getInt("position");	
			fromActivity  = getIntent().getExtras().getBoolean("fromActivity");
			
			itemCode      = getIntent().getExtras().getString("itemCode");	
			desc      = getIntent().getExtras().getString("desc");	
			
			lat       = getIntent().getExtras().getString("lat");
			lang      = getIntent().getExtras().getString("long");
			
		}
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		
		width = displaymetrics.widthPixels;
		
		initializeControles();
		
		locationUtility  = new LocationUtility(CaptureDamagedItemImage.this);
		locationUtility.getLocation(CaptureDamagedItemImage.this);
		
		
		tvCaptureDamageTitle.setText(itemCode+"- "+desc);
		gvImages.setAdapter(gridViewAdapter = new GridViewAdapter(new ArrayList<String>()));
		
		if(vecImagePaths != null && vecImagePaths.size() > 0)
		{
			gridViewAdapter.refreshGridView(vecImagePaths);
		}
		
		
		utils = new ImageLoadingUtils(CaptureDamagedItemImage.this);
//		startCamera();
		captureImage();
		
		btnTakeNew.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				isButton = true;
//				startCamera();
				captureImage();
			}
		});
		
		btnContinue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent objIntent = new Intent();
				objIntent.putExtra("vecImagePaths", vecImagePaths);
				objIntent.putExtra("position", position);
				objIntent.putExtra("fromActivity", fromActivity);
				setResult(500, objIntent);
				finish();
			}
		});
	}
	
	private void initializeControles()
	{
		gvImages = (GridView) llCaptureImages.findViewById(R.id.gvImages);
		btnTakeNew = (Button) llCaptureImages.findViewById(R.id.btnTakeNew);
		btnContinue = (Button) llCaptureImages.findViewById(R.id.btnContinue);
		
		tvCaptureDamageTitle = (TextView) llCaptureImages.findViewById(R.id.tvCaptureDamageTitle);
		
		btnTakeNew.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnContinue.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		
		tvCaptureDamageTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
	}
	
	private class GridViewAdapter extends BaseAdapter
	{

		private ArrayList<String> vecImagePaths;
		public GridViewAdapter(ArrayList<String> vecImagePaths)
		{
			this.vecImagePaths = vecImagePaths;
		}
		@Override
		public int getCount() 
		{
			if(vecImagePaths != null && vecImagePaths.size() > 0)
				return vecImagePaths.size();
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) 
		{
			if(convertView == null)
				convertView = inflater.inflate(R.layout.capture_image_content, null);
			ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
			
//			loadBitmap(vecImagePaths.get(position), ivImage);
			
			setBitmapImage(ivImage, vecImagePaths.get(position));
			
//			UrlImageViewHelper.setUrlDrawable(ivImage, vecImagePaths.get(position).replace("../", ServiceURLs.IMAGE_LOCAL_URL), R.drawable.app_logo, new UrlImageViewCallback() {
//                @Override
//                public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
//                    if (!loadedFromCache) {
//                        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
//                        scale.setDuration(300);
//                        scale.setInterpolator(new OvershootInterpolator());
//                        imageView.startAnimation(scale);
//                    }
//                }
//            });
			
			convertView.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v)
				{
					mPosition = position;
					showCustomDialog(CaptureDamagedItemImage.this, "Warning!", "Are you sure you want to delete this image?", "Yes", "No", "delete");
					return true;
				}
			});
			
			return convertView;
		}
		
		private void refreshGridView(ArrayList<String> vecImagePaths)
		{
			this.vecImagePaths = vecImagePaths;
			notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		boolean isFileExits = false;
		if(!TextUtils.isEmpty(camera_imagepath))
		{
			File f = new File(camera_imagepath);
			isFileExits = f.isFile();
		}
		if ((requestCode == CAMERA_PIC_REQUEST) && isFileExits && resultCode == RESULT_OK ) 
    	{
    		try
    		{
//    			showLoader("Please wait...");
//            	new Thread(new Runnable()
//            	{
//    				@Override
//    				public void run()
//    				{
//    					System.gc();
//    		        	String[] projection = { MediaStore.Images.Media.DATA}; 
//    		            Cursor cursor = managedQuery(mCapturedImageURI, projection, null, null, null); 
//    		            int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); 
//    		            cursor.moveToFirst(); 
//    		            String capturedImageFilePath = cursor.getString(column_index_data);
//    		            
//    		            if(vecImagePaths == null)
//		            		vecImagePaths = new ArrayList<String>();
//    		            vecImagePaths.add(capturedImageFilePath);
//    		            
//    		            runOnUiThread(new Runnable() {
//							@Override
//							public void run() {
//								hideLoader();
//								gridViewAdapter.refreshGridView(vecImagePaths);
//							}
//						});
//    				}
//    			}).start();
    			
    			showLoader("Please wait...");
    			new Thread(new Runnable() 
    			{
					@Override
					public void run() 
					{
						DisplayMetrics metrics = getResources().getDisplayMetrics();
						File f = new File(camera_imagepath);
						BitmapsUtiles.decodeSampledBitmapFromResource(f, 500,800,CaptureDamagedItemImage.this,metrics.density);
						runOnUiThread(new Runnable() 
						{
							
							@Override
							public void run() 
							{
								hideLoader();
								if(vecImagePaths == null)
				            		vecImagePaths = new ArrayList<String>();
						            vecImagePaths.add(camera_imagepath);
									gridViewAdapter.refreshGridView(vecImagePaths);
							}
						});
					}
				}).start();
    			
//    			new ImageCompressionAsyncTask(CaptureDamagedItemImage.this, mCapturedImageURI, new CaptureCopressedImageListner()
//    			{
//					@Override
//					public void capturedSucceshully(final String filePath) 
//					{
//						runOnUiThread(new Runnable() 
//						{
//							@Override
//							public void run() 
//							{
//								hideLoader();
//								if(vecImagePaths == null)
//				            		vecImagePaths = new ArrayList<String>(); 
//		    		            vecImagePaths.add(filePath);
//								gridViewAdapter.refreshGridView(vecImagePaths);
//							}
//						});
//					}
//				}).execute("");
			}
    		catch (OutOfMemoryError e)
    		{
    			hideLoader();
    			showCustomDialog(CaptureDamagedItemImage.this, "Alert !", "Capturing of image has been cancelled.", "OK", "", "", false);
				e.printStackTrace();
			}
    		catch (Exception e)
    		{
    			hideLoader();
    			showCustomDialog(CaptureDamagedItemImage.this, "Alert !", "Capturing of image has been cancelled.", "OK", "", "", false);
				e.printStackTrace();
			}
    	} 
		else if((requestCode == CAMERA_PIC_REQUEST) && !isFileExits && resultCode == RESULT_OK)
			showCustomDialog(CaptureDamagedItemImage.this, "Alert !", "Error occurred while capturing image, please try again.", "OK", "", "", false);
	}
	
	public void startCamera()
	{
		String fileName = "temp.jpg";  
		ContentValues values = new ContentValues();  
		values.put(MediaStore.Images.Media.TITLE, fileName);  
		mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);  
		startActivityForResult(intent, CAMERA_PIC_REQUEST);
	}
	
	@Override
	public void onButtonYesClick(String from) 
	{
		
		if(from.equalsIgnoreCase("delete"))
		{
			vecImagePaths.remove(mPosition);
			gridViewAdapter.refreshGridView(vecImagePaths);
		}
		super.onButtonYesClick(from);
	}
	
	private void setBitmapImage(final ImageView imageView, String capturedImageFilePath)
	{
		Bitmap bitmapProcessed = decodeFile(new File(capturedImageFilePath), (int)(120 * px), (int)(120 * px));
		
////		Bitmap bitmapProcessed = BitmapsUtiles.processBitmap2(stampBitmap, lat, lang, "", width);
//		if(stampBitmap!=null && !stampBitmap.isRecycled())
//			stampBitmap.recycle();
		
        if(bitmapProcessed != null)
        {
   	    	
   	    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
   	    	bitmapProcessed.compress(Bitmap.CompressFormat.PNG, 100, stream);
			final WeakReference<Bitmap> reference = new WeakReference<Bitmap>(bitmapProcessed);
   	    	runOnUiThread(new Runnable()
   	    	{
				@Override
				public void run() 
				{
					imageView.setImageBitmap(reference.get());
					hideLoader();
				}
			});
        }
	}
	/////////////////////////////////////////////////////////////////////////////////
	public void loadBitmap(String filePath, ImageView imageView) {
		if (cancelPotentialWork(filePath, imageView)) {
			final Bitmap bitmap = getBitmapFromMemCache(filePath);
			if(bitmap != null){
				imageView.setImageBitmap(bitmap);
			}
			else{
		        final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
		        final AsyncDrawable asyncDrawable = new AsyncDrawable(getResources(), utils.icon, task);
		        imageView.setImageDrawable(asyncDrawable);
		        task.execute(filePath);
			}
	    }
	}
	
	public boolean cancelPotentialWork(String filePath, ImageView imageView) {
		
	    final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
	    
	    if (bitmapWorkerTask != null) {
	        final String bitmapFilePath = bitmapWorkerTask.filePath;
	        if (bitmapFilePath != null && !bitmapFilePath.equalsIgnoreCase(filePath)) {
	            bitmapWorkerTask.cancel(true);
	        } else {
	            return false;
	        }
	    }
	    return true;
	}
	
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap>{
		private final WeakReference<ImageView> imageViewReference;
		public String filePath;
		
		public BitmapWorkerTask(ImageView imageView){
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			filePath = params[0];
			Bitmap bitmap = utils.decodeBitmapFromPath(filePath);
			addBitmapToMemoryCache(filePath, bitmap);
			return bitmap;
		}
		
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
	            bitmap = null;
	        }
			if(imageViewReference != null && bitmap != null){
				final ImageView imageView = imageViewReference.get();
				final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
	            if (this == bitmapWorkerTask && imageView != null) {
	                imageView.setImageBitmap(bitmap);
	            }
			}
		}
	}
	
	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	    	memoryCache.put(key, bitmap);
	    }
	}
	
	public Bitmap getBitmapFromMemCache(String key) {
	    return memoryCache.get(key);
	}
	
	private BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		   if (imageView != null) {
		       final Drawable drawable = imageView.getDrawable();
		       if (drawable instanceof AsyncDrawable) {
		           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
		           return asyncDrawable.getBitmapWorkerTask();
		       }
		    }
		    return null;
	}
	class AsyncDrawable extends BitmapDrawable {
		
	    private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

	    public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask bitmapWorkerTask) {
	        super(res, bitmap);
	        bitmapWorkerTaskReference = new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
	    }

	    public BitmapWorkerTask getBitmapWorkerTask() {
	        return bitmapWorkerTaskReference.get();
	    }
	}
	public boolean isDeviceSupportCamera() 
	{
		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) 
		{
			return true;
		} 
		else
		{
			return false;
		}
	}
	public static File getOutputImageFile(String folder)
	{
	 
        File captureImagesStorageDir = new File(Environment.getExternalStorageDirectory()+"/Hector/"+folder);
 
        if (!captureImagesStorageDir.exists()) 
        {
            if (!captureImagesStorageDir.mkdirs())
            {
                Log.d("Hector", "Oops! Failed create ");
                return null;
            }
        }
 
        String timestamp = System.currentTimeMillis()+""; 
        File imageFile = new File(captureImagesStorageDir.getPath() + File.separator+ "CAPTURE_" + timestamp + ".jpg");
        return imageFile;
	}
	/**
	 * For Capturing Images
	 */
	private void captureImage()
	{
		if(isDeviceSupportCamera())
		{
			File file    = getOutputImageFile("Images");
			if(file!=null)
			{
				camera_imagepath   = file.getAbsolutePath();
				Uri fileUri  = Uri.fromFile(file);

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
				intent.putExtra("fileName",file.getName());
				intent.putExtra("filePath", file.getAbsolutePath());
				startActivityForResult(intent, CAMERA_PIC_REQUEST);
			}
		}
		else
		{
			Toast.makeText(CaptureDamagedItemImage.this,"Sorry Device not supported to camera", Toast.LENGTH_SHORT).show();
		}
	}
	///////////////////////////////////////////////////////////////////////////////////
	
	public static Bitmap decodeFile(File f, int WIDTH, int HIGHT) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// The new size we want to scale to
			final int REQUIRED_WIDTH = WIDTH;
			final int REQUIRED_HIGHT = HIGHT;
			// Find the correct scale value. It should be the power of 2.
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
					&& o.outHeight / scale / 2 >= REQUIRED_HIGHT)
				scale *= 2;

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	@Override
	public void gotLocation(Location loc) 
	{
		
	}
}

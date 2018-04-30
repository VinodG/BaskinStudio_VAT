package com.winit.baskinrobbin.salesman;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.winit.baskinrobbin.salesman.common.AppConstants;
import com.winit.baskinrobbin.salesman.common.Base64;
import com.winit.baskinrobbin.salesman.common.Preference;
import com.winit.baskinrobbin.salesman.dataaccesslayer.NotesDA;
import com.winit.baskinrobbin.salesman.dataobject.NotesObject;
import com.winit.baskinrobbin.salesman.utilities.CalendarUtils;
import com.winit.baskinrobbin.salesman.utilities.LogUtils;
import com.winit.baskinrobbin.salesman.utilities.StringUtils;

public class PresellerNotes extends BaseActivity
{
	//declaration of variables
	private LinearLayout llNotesMain, llTakePhotoClick;
	private Button btnNotesSubmit;
	private TextView tvFromGallery, tvFromCamera, tvPhotosTitle, tvSubjectNotes, tvSubjectDes, tvTakePhoto, tvTitlePhoto;
	private ImageView ivPhoto;
	private Bitmap image;
	private EditText etDesc,etTitele;
	private static final int CAMERA_PIC_REQUEST1 = 2500;
	private static final int SELECT_IMAGE = 1;
	private String selectedImagePath;
	private  Uri mCapturedImageURI;
	private NotesObject objNotesObject;
	private NotesDA notesBL;

	@Override
	public void initialize() 
	{
		
		//inflate the notes layout
		llNotesMain	= (LinearLayout)inflater.inflate(R.layout.notes, null);
		llBody.addView(llNotesMain,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		
		initializeControls();
		
		setTypeFace(llNotesMain);
		etTitele=(EditText)findViewById(R.id.etSubjectNotes);
		etDesc=(EditText)findViewById(R.id.etSubDes);
        objNotesObject = new NotesObject();
        notesBL = new NotesDA();
		
		btnNotesSubmit.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				// Toast to display the u have submitted
				if(etTitele.getText().toString().equals("") || etDesc.getText().toString().equals(""))
				{
					showCustomDialog(PresellerNotes.this, getResources().getString(R.string.warning), "Please enter all the fields.", getResources().getString(R.string.OK), null, "");
					etTitele.requestFocus();
				}
				else
				{
					if(!objNotesObject.image.equalsIgnoreCase(""))
					{
						objNotesObject.Note_Title 		= 	etTitele.getText().toString();
						objNotesObject.Note_Description =	etDesc.getText().toString();
						objNotesObject.Emp_Id			=	preference.getStringFromPreference(Preference.EMP_NO, "");
						objNotesObject.DateAndTime 		= 	CalendarUtils.getCurrentDateAsString()+", "+CalendarUtils.getCurrentTime();
						objNotesObject.Customer_ID		= 	preference.getStringFromPreference(Preference.CUSTOMER_SITE_ID, "");
						showLoader(getResources().getString(R.string.loading));
						/** Calling inserNotes here, if the request is successful than insert from handler else insert from here.*/
						new Thread(new Runnable()
						{
							@Override
							public void run() 
							{
								 runOnUiThread(new Runnable() 
								 {
									@Override
									public void run() 
									{
								        notesBL.insertNotes(objNotesObject);
								        hideLoader();
									}
								});
							}
						}).start();
						ivPhoto.setVisibility(View.GONE);
						showCustomDialog(PresellerNotes.this, getResources().getString(R.string.successful), "Notes has been added successfully.", getResources().getString(R.string.OK), null ,"notes");
					}
					else
					{
						showCustomDialog(PresellerNotes.this, getResources().getString(R.string.successful), "Please select an image either from gallery or capture.", getResources().getString(R.string.OK), null, "");
					}
				}
			}
		});
		
		llTakePhotoClick.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				 LayoutInflater inflater = ((Activity)PresellerNotes.this).getLayoutInflater();
					
				 LinearLayout llTakePhoto=(LinearLayout)inflater.inflate(R.layout.takephoto,null);
				 final Dialog dialog = new Dialog(PresellerNotes.this,R.style.Dialog);
				 dialog.setContentView(llTakePhoto);
				 
				 tvFromGallery	= (TextView)llTakePhoto.findViewById(R.id.tvFromGallery);
				 tvFromCamera	= (TextView)llTakePhoto.findViewById(R.id.tvFromCamera);
				 tvTitlePhoto   = (TextView)llTakePhoto.findViewById(R.id.tvTitlePhoto);
				 
				 tvFromGallery.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
				 tvFromCamera.setTypeface(AppConstants.Helvetica_LT_57_Condensed);
				 tvTitlePhoto.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
				 
				 tvFromGallery.setOnClickListener(new OnClickListener()
				 {
					@Override
					public void onClick(View v) 
					{
						dialog.dismiss();
						openGallery();
					}
				 });
				 
				 tvFromCamera.setOnClickListener(new OnClickListener()
				 {
					@Override
					public void onClick(View v) 
					{
					    dialog.dismiss();
					    startCamera();
					}
				});
				 
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			}
		});
		
		ivPhoto.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				 LayoutInflater inflater = ((Activity)PresellerNotes.this).getLayoutInflater();
					
				 LinearLayout llZoomImage=(LinearLayout)inflater.inflate(R.layout.fullimage2,null);
				 final Dialog dialog = new Dialog(PresellerNotes.this,R.style.Dialog);
				 dialog.setContentView(llZoomImage);
				 
				 ImageView ivZoom=(ImageView)llZoomImage.findViewById(R.id.ivZoom);

				 ivZoom.setBackgroundDrawable(v.getBackground());
				 dialog.show();
					
				 ivZoom.setOnTouchListener(new OnTouchListener()
				 {
					@Override
					public boolean onTouch(View v, MotionEvent event)
					{
						if(dialog.isShowing())
							dialog.dismiss();
						return false;
					}
				 });
				
				 dialog.setCanceledOnTouchOutside(true);	
			}
		});
	}
	
	private void initializeControls()
	{
		llTakePhotoClick		=	(LinearLayout)llNotesMain.findViewById(R.id.llTakePhotoClick);
		btnNotesSubmit			=	(Button)llNotesMain.findViewById(R.id.btnNotesSubmit);
		ivPhoto					=	(ImageView)llNotesMain.findViewById(R.id.ivPhoto);
		
		etTitele 				= (EditText)findViewById(R.id.etSubjectNotes);
		etDesc 					= (EditText)findViewById(R.id.etSubDes);
		
		tvPhotosTitle  			= (TextView) llNotesMain.findViewById(R.id.tvPhotosTitle);
		tvSubjectNotes 			= (TextView) llNotesMain.findViewById(R.id.tvSubjectNotes);
		tvSubjectDes   			= (TextView) llNotesMain.findViewById(R.id.tvSubjectDes);
		tvTakePhoto    			= (TextView) llNotesMain.findViewById(R.id.tvTakePhoto);
		
		/*tvPhotosTitle.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvSubjectNotes.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvSubjectDes.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		tvTakePhoto.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);
		btnNotesSubmit.setTypeface(AppConstants.Helvetica_LT_Condensed_Bold);*/
		
        objNotesObject = new NotesObject();
        notesBL = new NotesDA();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
        super.onActivityResult(requestCode, resultCode, data);
        
        	if (requestCode == SELECT_IMAGE && data!=null) 
        	{
        		Uri selectedImageUri = data.getData();
        		selectedImagePath    = getPath(selectedImageUri);
        		
        		BitmapFactory.Options options2 = new BitmapFactory.Options();
        		options2.inJustDecodeBounds = false;
        		options2.inDither = false;
        		options2.inPreferredConfig = Bitmap.Config.RGB_565;
        	    	
        		Bitmap bmp = BitmapFactory.decodeFile(selectedImagePath, options2);
        		if(bmp != null)
        		{
        			int height =  bmp.getHeight(), width = bmp.getWidth();
        			float HEIGHT =100, WIDTH = 100;
        			
        			float scale = 0f;
        			
        			ExifInterface exif = null;
        			try
        			{
        				exif = new ExifInterface(selectedImagePath);
        			} 
        			catch (IOException e) 
        			{
        				e.printStackTrace();
        			}
	                 
	     			Matrix matr = new Matrix();
	     			boolean blnInterchangeHeightAndWidth = false;
	     			if(exif != null)
	     			{
	     				int orientation = StringUtils.getInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION));
	     				
	     				switch(orientation)
	     				{
	     					case ExifInterface.ORIENTATION_NORMAL:
	     						
	     						break;
	     					case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
	     						
	     						break;
	     					case ExifInterface.ORIENTATION_ROTATE_180:
	     						matr.postRotate(180);
	     						break;
	     					case ExifInterface.ORIENTATION_FLIP_VERTICAL:
	     						
	     						break;
	     					case ExifInterface.ORIENTATION_TRANSPOSE:
	     						
	     						break;
	     					case ExifInterface.ORIENTATION_ROTATE_90:
	     						blnInterchangeHeightAndWidth = true;
	     						matr.postRotate(90);
	     						break;
	     					case ExifInterface.ORIENTATION_TRANSVERSE:
	     						
	     						break;
	     					case ExifInterface.ORIENTATION_ROTATE_270:
	     						blnInterchangeHeightAndWidth = true;
	     						matr.postRotate(270);
	     						break;
	     					default:
	     				}
	     			}	
	     			
	     			if(blnInterchangeHeightAndWidth)
	     			{
	     				int temp = width;
	     				width = height;
	     				height = temp;
	     			}
	     			
	     			if(HEIGHT / height > WIDTH / width)
	                 	scale = WIDTH / width;
	                 else
	                 	scale = HEIGHT / height;
	                 
	                 height = (int)(scale * height);
	                 width = (int)(scale * width);
	                 
	                 if(blnInterchangeHeightAndWidth)
	     			{
	     				int temp = width;
	     				width = height;
	     				height = temp;
	     			}
	                 
	                 ivPhoto.setImageResource(0);
	                 if(image != null && !image.isRecycled())
	                 {
	                	 WeakReference<Bitmap> bmps = new WeakReference<Bitmap>(image);
	                	 image = null;
	                	 bmps.get().recycle();
	                 }
	                 
	//                 Bitmap bmp2 = null;
	                 image = Bitmap.createScaledBitmap(bmp, width, height, true);
	                 ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
	                 bmp.compress(Bitmap.CompressFormat.PNG, 100, stream1);
//	                 objNotesObject.ImageLarge =stream1.toByteArray();
	                 bmp.recycle();
	                 	
	                 ByteArrayOutputStream stream = new ByteArrayOutputStream();
	                 (Bitmap.createBitmap(image, 8, 0, image.getWidth() - 8, image.getHeight(), matr, true)).compress(Bitmap.CompressFormat.JPEG, 100, stream);
	                 LogUtils.infoLog("base",""+ Base64.encodeBytes(stream.toByteArray()));
	                 objNotesObject.image = Base64.encodeBytes(stream.toByteArray());
	                 ivPhoto.setImageBitmap(image);
        		 }
//                 bmp2.recycle();
        }
        else if (requestCode == CAMERA_PIC_REQUEST1) 
    	{
        	System.gc();
        	String[] projection = { MediaStore.Images.Media.DATA}; 
            Cursor cursor = managedQuery(mCapturedImageURI, projection, null, null, null); 
            int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); 
            cursor.moveToFirst(); 
            String capturedImageFilePath = cursor.getString(column_index_data);

            LogUtils.infoLog("capturedImageFilePath",""+capturedImageFilePath);
            
   	        BitmapFactory.Options options2 = new BitmapFactory.Options();
   	        options2.inJustDecodeBounds = false;
   	        options2.inDither = false;
   	        options2.inPreferredConfig = Bitmap.Config.RGB_565;

   	    	Bitmap bmp = BitmapFactory.decodeFile(capturedImageFilePath, options2);
   	    	if(bmp!=null)
   	    	{
	   	    	int height =  bmp.getHeight(), width = bmp.getWidth();
	   	    	float HEIGHT =100, WIDTH = 100;
	   	    	
	   	    	float scale = 0f;
	            
	            ExifInterface exif = null;
	            try
	            {
					exif = new ExifInterface(capturedImageFilePath);
				} 
	            catch (IOException e) 
	            {
					e.printStackTrace();
				}
	            
				Matrix matr = new Matrix();
				boolean blnInterchangeHeightAndWidth = false;
				if(exif != null)
				{
					int orientation = StringUtils.getInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION));
					
					switch(orientation)
					{
						case ExifInterface.ORIENTATION_NORMAL:
							
							break;
						case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
							
							break;
						case ExifInterface.ORIENTATION_ROTATE_180:
							matr.postRotate(180);
							break;
						case ExifInterface.ORIENTATION_FLIP_VERTICAL:
							
							break;
						case ExifInterface.ORIENTATION_TRANSPOSE:
							
							break;
						case ExifInterface.ORIENTATION_ROTATE_90:
							blnInterchangeHeightAndWidth = true;
							matr.postRotate(90);
							break;
						case ExifInterface.ORIENTATION_TRANSVERSE:
							
							break;
						case ExifInterface.ORIENTATION_ROTATE_270:
							blnInterchangeHeightAndWidth = true;
							matr.postRotate(270);
							break;
						default:
					}
				}	
				
				if(blnInterchangeHeightAndWidth)
				{
					int temp = width;
					width = height;
					height = temp;
				}
				
				if(HEIGHT / height > WIDTH / width)
	            	scale = WIDTH / width;
	            else
	            	scale = HEIGHT / height;
	            
	            height = (int)(scale * height);
	            width = (int)(scale * width);
	            
	            if(blnInterchangeHeightAndWidth)
				{
					int temp = width;
					width = height;
					height = temp;
				}
	            
	            ivPhoto.setImageResource(0);
                if(image != null && !image.isRecycled())
                {
               	 WeakReference<Bitmap> bmps = new WeakReference<Bitmap>(image);
               	 image = null;
               	 bmps.get().recycle();
                }
	            
//	            Bitmap bmp2 = null;
                image = Bitmap.createScaledBitmap(bmp, width, height, true);
	            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
	            (Bitmap.createBitmap(bmp, 8, 0, bmp.getWidth() - 8, bmp.getHeight(), matr, true)).compress(Bitmap.CompressFormat.JPEG, 100, stream1);
//                objNotesObject.ImageLarge =stream1.toByteArray();
	            bmp.recycle();
	           	 	
	            	
	            ByteArrayOutputStream stream = new ByteArrayOutputStream();
	            (Bitmap.createBitmap(image, 8, 0, image.getWidth() - 8, image.getHeight(), matr, true)).compress(Bitmap.CompressFormat.JPEG, 100, stream);
	            objNotesObject.image = Base64.encodeBytes(stream.toByteArray());
	            ivPhoto.setImageBitmap(image);
//	            image.recycle();
   	    	}
    	}  
        else
        {
        	LogUtils.infoLog("Camera", "Result code was " + resultCode);
        }
	}
	
	public String getPath(Uri uri)
	{
	    String[] projection = { MediaStore.Images.Media.DATA };
	    Cursor cursor = managedQuery(uri, projection, null, null, null);
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
	    return cursor.getString(column_index);
	}
	
	public void startCamera()
	{
		String fileName = "temp.jpg";  
		ContentValues values = new ContentValues();  
		values.put(MediaStore.Images.Media.TITLE, fileName);  
		mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);  
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);  
		startActivityForResult(intent, CAMERA_PIC_REQUEST1);
	}
	public void openGallery()
	{
		Intent intent =getIntent(); 
        intent.setType("image/*");
	    intent.setAction(Intent.ACTION_GET_CONTENT);
	    startActivityForResult(Intent.createChooser(intent,"Select Images"),SELECT_IMAGE);
	}

	
	@Override
	public void onDestroy() 
	{
		super.onDestroy();
		
		ivPhoto.setImageResource(0);
		if(image != null && !image.isRecycled())
		{
			WeakReference<Bitmap> bmp = new WeakReference<Bitmap>(image);
			image = null;
			bmp.get().recycle();
		}
	}
	
	@Override
	public void onButtonYesClick(String from)
	{
		super.onButtonYesClick(from);
		if(from.equalsIgnoreCase("notes"))
		{
			LogUtils.infoLog("ok","onClick");
			Intent intent = new Intent();
			intent.putExtra("object", objNotesObject);
			setResult(200);
			finish();
		}
	}
}
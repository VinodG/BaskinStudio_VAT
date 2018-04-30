package com.winit.baskirobin.coverflow;

import android.app.Activity;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

import com.winit.baskinrobbin.salesman.common.AppConstants;
@SuppressWarnings("deprecation")
public class CoverFlow extends Gallery {
	  /**
     * Graphics Camera used for transforming the matrix of ImageViews
     */
    private Camera mCamera = new Camera();

    /**
     * The maximum angle the Child ImageView will be rotated by
     */    
    private int mMaxRotationAngle = 60;
    
    /**
     * The maximum zoom on the centre Child
     */
    private int mMaxZoom = 100;
    
    /**
     * The Centre of the Coverflow 
     */   
    private int mCoveflowCenter;
   
 public CoverFlow(Context context) {
  super(context);
  //setting device width and height
	Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
	AppConstants.DIVICE_WIDTH = display.getWidth();
	AppConstants.DIVICE_HEIGHT = display.getHeight();
  this.setStaticTransformationsEnabled(true);
 }

public CoverFlow(Context context, AttributeSet attrs) {
  super(context, attrs);
  //setting device width and height
	Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
	AppConstants.DIVICE_WIDTH = display.getWidth();
	AppConstants.DIVICE_HEIGHT = display.getHeight();
        this.setStaticTransformationsEnabled(true);
 }
 
  public CoverFlow(Context context, AttributeSet attrs, int defStyle) {
   super(context, attrs, defStyle);
   //setting device width and height
 	Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
 	AppConstants.DIVICE_WIDTH = display.getWidth();
 	AppConstants.DIVICE_HEIGHT = display.getHeight();
   this.setStaticTransformationsEnabled(true);   
  }
  
    /**
     * Get the max rotational angle of the image
  * @return the mMaxRotationAngle
  */
 public int getMaxRotationAngle() {
  return mMaxRotationAngle;
 }

 /**
  * Set the max rotational angle of each image
  * @param maxRotationAngle the mMaxRotationAngle to set
  */
 public void setMaxRotationAngle(int maxRotationAngle) {
  mMaxRotationAngle = maxRotationAngle;
 }

 /**
  * Get the Max zoom of the centre image
  * @return the mMaxZoom
  */
 public int getMaxZoom() {
  return mMaxZoom;
 }

 /**
  * Set the max zoom of the centre image
  * @param maxZoom the mMaxZoom to set
  */
 public void setMaxZoom(int maxZoom) {
  mMaxZoom = maxZoom;
 }

 /**
     * Get the Centre of the Coverflow
     * @return The centre of this Coverflow.
     */
    private int getCenterOfCoverflow() {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }
    
    /**
     * Get the Centre of the View
     * @return The centre of the given view.
     */
    private static int getCenterOfView(View view) {
        return view.getLeft() + view.getWidth() / 2;
    }  
    /**
  * {@inheritDoc}
  *
  * @see #setStaticTransformationsEnabled(boolean) 
  */ 
    protected boolean getChildStaticTransformation(View child, Transformation t) 
    {
    	final int childCenter = getCenterOfView(child);
    	float zoom = 0;
	  
    	t.clear();
    	t.setTransformationType(Transformation.TYPE_MATRIX);
    	
    	float offset = Math.abs(childCenter - mCoveflowCenter);
    	
    	if(offset < AppConstants.DIVICE_WIDTH/3)
    	{
    		zoom = 1.2f *((AppConstants.DIVICE_WIDTH/3) - offset);
    	}
    	
    	transformImageBitmap(child, t, zoom);
    	if(Build.VERSION.SDK_INT >= 16)
    		child.invalidate();
    	return true;
	}

 /**
  * This is called during layout when the size of this view has changed. If
  * you were just added to the view hierarchy, you're called with the old
  * values of 0.
  *
  * @param w Current width of this view.
  * @param h Current height of this view.
  * @param oldw Old width of this view.
  * @param oldh Old height of this view.
     */
     protected void onSizeChanged(int w, int h, int oldw, int oldh) {
      mCoveflowCenter = getCenterOfCoverflow();
      super.onSizeChanged(w, h, oldw, oldh);
     }
  
     /**
      * Transform the Image Bitmap by the Angle passed 
      * 
      * @param imageView ImageView the ImageView whose bitmap we want to rotate
      * @param t transformation 
      * @param zoom the Angle by which to rotate the Bitmap
      */
     private void transformImageBitmap(View child, Transformation t, float zoom) 
     {            
	      mCamera.save();
	      final Matrix imageMatrix = t.getMatrix();
	      final int imageHeight = child.getLayoutParams().height;
	      final int imageWidth = child.getLayoutParams().width;
	      mCamera.translate(0.0f, 0.0f, -zoom);  
	      mCamera.getMatrix(imageMatrix);               
	      imageMatrix.preTranslate(-(imageWidth/2), -(imageHeight/2)); 
	      imageMatrix.postTranslate((imageWidth/2), (imageHeight/2));
	      mCamera.restore();
 }

}

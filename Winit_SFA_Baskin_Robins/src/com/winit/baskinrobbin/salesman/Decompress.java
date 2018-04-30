package com.winit.baskinrobbin.salesman;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;

public class Decompress 
{
	private InputStream _zipFile; 
	  private String _location; 
	 
	  public Decompress(InputStream zipFile, String location) 
	  { 
	    _zipFile = zipFile; 
	    _location = location; 
	 
	    _dirChecker(""); 
	  } 
	  public void unzip() 
	  { 
	    try  
	    { 
	      ZipInputStream zin = new ZipInputStream(_zipFile); 
	      ZipEntry ze = null; 
	      File file;
	      while ((ze = zin.getNextEntry()) != null)
	      { 
	        if(ze.isDirectory()) 
	        { 
	          _dirChecker(ze.getName()); 
	        } 
	        else 
	        { 
		          file = new File(_location + ze.getName());
		          FileOutputStream fout = new FileOutputStream(_location + ze.getName()); 
		          for (int c = zin.read(); c != -1; c = zin.read()) 
		          { 
		            fout.write(c); 
		          } 
		          Log.v("Decompress", "Unzipping " + ze.getName()); 
		          zin.closeEntry(); 
		          fout.close();
	        } 
	         
	      } 
	      zin.close(); 
	    } 
	    catch(Exception e) 
	    { 
	    	e.printStackTrace();
	    } 
	  } 
	 
	  private void _dirChecker(String dir) 
	  { 
	    File f = new File(_location + dir); 
	 
	    if(!f.isDirectory())
	    { 
	      f.mkdirs(); 
	    } 
	  } 
}

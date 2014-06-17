package com.viton.libs.aws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.viton.libs.aws.model.AWSApplication;

public class AWSImageUpload {

	public static boolean uploadImage(AWSApplication app, Context context, Bitmap bitmap, String fileName) throws IOException{
		File outputDir = context.getCacheDir();
		File outputFile = File.createTempFile("img", "jpg", outputDir);
		FileOutputStream outStream = new FileOutputStream(outputFile);
		getRotateBitmap(bitmap, context).compress(Bitmap.CompressFormat.JPEG, 25, outStream);
		
		return uploadImage(app, 
				outputFile, 
				fileName);
	}
	
	private static Bitmap getRotateBitmap(Bitmap source, Context context){
		float angle = 90;
		if(getScreenOrientation(context) == 0){
			angle = 180;
		}else if(getScreenOrientation(context) == 1){
			angle = 270;
		}else if(getScreenOrientation(context) == 8){
			angle = 0;
		}
		
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
	
	public static boolean uploadImage(AWSApplication app, String filePath, String fileName){
		return uploadImage(app, new File(filePath), fileName);
	}
	
	public static boolean uploadImage(AWSApplication app, Context context, Uri uri, String fileName){
		return uploadImage(app, new File(getRealPathFromURI(context, uri)), fileName);
	}
	
	public static boolean uploadImage(AWSApplication app, File file, String fileName){
		PutObjectResult result = null;
		try{
			AmazonS3Client s3Client = new AmazonS3Client(app.getCredentials());
			PutObjectRequest por = new PutObjectRequest(
					app.getBucketName(),
					fileName+".jpg", 
					file);
			
			por.setCannedAcl(app.getAccess());
			
			result = s3Client.putObject(por);
		}catch(Exception e){
			return false;
		}

		if(result != null) 
			return true;
		return false;
	}
	
	private static String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try { 
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	private static int getScreenOrientation(Context context) {
	    Activity a = (Activity) context;
		int rotation = a.getWindowManager().getDefaultDisplay().getRotation();
	    DisplayMetrics dm = new DisplayMetrics();
	    a.getWindowManager().getDefaultDisplay().getMetrics(dm);
	    int width = dm.widthPixels;
	    int height = dm.heightPixels;
	    int orientation;
	    // if the device's natural orientation is portrait:
	    if ((rotation == Surface.ROTATION_0
	            || rotation == Surface.ROTATION_180) && height > width ||
	        (rotation == Surface.ROTATION_90
	            || rotation == Surface.ROTATION_270) && width > height) {
	        switch(rotation) {
	            case Surface.ROTATION_0:
	                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	                break;
	            case Surface.ROTATION_90:
	                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	                break;
	            case Surface.ROTATION_180:
	                orientation =
	                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
	                break;
	            case Surface.ROTATION_270:
	                orientation =
	                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
	                break;
	            default:
	                Log.e("", "Unknown screen orientation. Defaulting to " +
	                        "portrait.");
	                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	                break;              
	        }
	    }
	    // if the device's natural orientation is landscape or if the device
	    // is square:
	    else {
	        switch(rotation) {
	            case Surface.ROTATION_0:
	                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	                break;
	            case Surface.ROTATION_90:
	                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
	                break;
	            case Surface.ROTATION_180:
	                orientation =
	                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
	                break;
	            case Surface.ROTATION_270:
	                orientation =
	                    ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
	                break;
	            default:
	                Log.e("", "Unknown screen orientation. Defaulting to " +
	                        "landscape.");
	                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
	                break;              
	        }
	    }

	    return orientation;
	}
	
}

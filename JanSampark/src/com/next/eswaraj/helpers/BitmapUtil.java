package com.next.eswaraj.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.ExifInterface;


public class BitmapUtil {

    public static Bitmap decodeFileAndResize(File f, int requiredSize) {
        try {
     
        	int rotation = 0;
        	try {
				ExifInterface exif = new ExifInterface(f.getAbsolutePath());
				int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
				rotation = getRotation(orientation);
			} catch (IOException e) {
				e.printStackTrace();
			}
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = requiredSize;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }
            // decode with inSampleSize
            return createScaledBitmap(f, rotation, scale); 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

	private static Bitmap createScaledBitmap(File f, int rotation, int scale) throws FileNotFoundException {
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		if(0 < rotation ) {
			Matrix matrix = new Matrix();
			matrix.postRotate(rotation);
			Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			if(rotatedBitmap != bitmap){
				bitmap.recycle();
				bitmap = null;
			}
			return rotatedBitmap;
		} 
		return bitmap;
	}

	private static int getRotation(int orientation) {
		switch (orientation) {
		case ExifInterface.ORIENTATION_ROTATE_270:
			return 270;

		case ExifInterface.ORIENTATION_ROTATE_90:
			return 90;

		default:
			return 0;
		}
	}

    public static Point getImageDimensions(File f) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(f), null, o);
        return new Point(o.outWidth, o.outHeight);
    }

    public static void cropImage(File original, File output, Rect imageRect, Rect cropRect) throws FileNotFoundException, IOException {
        Point dimen = getImageDimensions(original);
        float scaledW = imageRect.right - imageRect.left;
        float scaledH = imageRect.bottom - imageRect.top;
        float scale = Math.min((float) dimen.x / scaledW, (float) dimen.y / scaledH);
       
        Rect cropRegion = getIntrinsicCropRegion(dimen, cropRect, scale);
        BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(original.getPath(), false);
        Bitmap bitmap = decoder.decodeRegion(cropRegion, null);
        saveBitmap(output, bitmap);
    }

    private static Rect getIntrinsicCropRegion(Point imageDimen, Rect cropRect, float scale) {
        Rect cropRegion = new Rect();
        cropRegion.left = (int) (cropRect.left * scale);
        cropRegion.top = (int) (cropRect.top  * scale);
        cropRegion.right = (int) (cropRect.right * scale + cropRegion.left);
        cropRegion.bottom = (int) (cropRect.bottom * scale + cropRegion.top);
        checkBoundaryCondition(imageDimen, cropRegion);
        return cropRegion;
    }

	private static void checkBoundaryCondition(Point imageDimen, Rect cropRegion) {
		checkVerticalBoundary(imageDimen, cropRegion);
        checkHorizontalBoundary(imageDimen, cropRegion);
	}

	private static void checkHorizontalBoundary(Point imageDimen,
			Rect cropRegion) {
		if(cropRegion.left < 0) {
        	cropRegion.right -= cropRegion.left;
        	cropRegion.left = 0;
        } else if(imageDimen.x < cropRegion.right) {
        	int diff = cropRegion.right - imageDimen.x;
        	cropRegion.left -= diff;
        	cropRegion.right -= diff;
        }
	}

	private static void checkVerticalBoundary(Point imageDimen, Rect cropRegion) {
		if(cropRegion.top < 0) {
        	cropRegion.bottom -= cropRegion.top ;
        	cropRegion.top = 0;
        } else if (imageDimen.y < cropRegion.bottom ) {
        	int diff = cropRegion.bottom - imageDimen.y;
        	cropRegion.top -= diff;
        	cropRegion.bottom -= diff;
        }
	}

    public static void saveBitmap(File output, Bitmap bitmap) throws FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(output);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        fos.flush();
        fos.close();
        bitmap.recycle();
        bitmap = null;
    }
    
    
    
  

}
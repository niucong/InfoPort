package net.tsz.afinal.customUtil;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class L {
	private static String TAG = "L";
	public static void setImgHeight(ImageView img, Bitmap oriBmp, int maxWidth) {
		if (null != img && null != oriBmp) {
			int outHeight = oriBmp.getHeight();
			int outWidth = oriBmp.getWidth();
			Log.i(TAG,   "Final setHeight" + ", outWidth=" + outWidth
					+ ",outHeight=" + outHeight + "     max " + maxWidth);
			
			float rotal = outHeight / (float) outWidth;
			if(outWidth > maxWidth){
			img.setLayoutParams(new LinearLayout.LayoutParams(maxWidth,
					(int) (maxWidth * rotal)));
			}else{
				img.setLayoutParams(new LinearLayout.LayoutParams(outWidth,
						outHeight));
			}
			img.requestLayout();
		}
	}
}

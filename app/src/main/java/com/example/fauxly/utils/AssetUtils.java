package com.example.fauxly.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;
import java.io.InputStream;

public class AssetUtils {
    public static void loadImageFromAssets(Context context, String assetPath, ShapeableImageView imageView) {
        try {
            // Get input stream for the asset
            InputStream ims = context.getAssets().open(assetPath);

            // Create Drawable from the input stream
            Drawable drawable = Drawable.createFromStream(ims, null);

            // Set the Drawable to the ImageView
            imageView.setImageDrawable(drawable);

            // Close the input stream
            ims.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ImageLoadError", "Error loading image from assets: " + assetPath);
        }
    }

}

package uk.co.sszymanski.cinema.core;

/**
 * Created by rex on 10/14/2016.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.squareup.picasso.Transformation;

public class BlurTransform implements Transformation {
    private int radius =0;
    RenderScript rs;
    public BlurTransform(Context context, int radius) {
        super();
        rs = RenderScript.create(context);
        this.radius = radius;
    }

    @Override
    public Bitmap transform(Bitmap bitmap) {

        // Create another bitmap that will hold the results of the filter.
        Bitmap blurredBitmap = Bitmap.createBitmap(bitmap);

        // Allocate memory for Renderscript to work with
        Allocation input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
        Allocation output = Allocation.createTyped(rs, input.getType());

        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setInput(input);

        // Set the blur radius
        if(radius>0 && radius<25){
            script.setRadius(radius);
        }else{
            script.setRadius(1);
        }


        // Start the ScriptIntrinisicBlur
        script.forEach(output);

        // Copy the output to the blurred bitmap
        output.copyTo(blurredBitmap);
        bitmap.recycle();
        return blurredBitmap;
    }

    @Override
    public String key() {
        return "blur";
    }

}
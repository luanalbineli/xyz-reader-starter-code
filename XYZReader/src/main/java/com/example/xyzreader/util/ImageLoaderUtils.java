package com.example.xyzreader.util;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

public abstract class ImageLoaderUtils {
    public static void loadImage(SimpleDraweeView simpleDraweeView, String imageUrl, int width, float aspectRatio) {
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUrl))
                .setResizeOptions(new ResizeOptions(width, (int) (width * aspectRatio)))
                .setProgressiveRenderingEnabled(true)
                .build();

        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(imageRequest)
                .setTapToRetryEnabled(true)
                .build();

        simpleDraweeView.setController(draweeController);
    }
}


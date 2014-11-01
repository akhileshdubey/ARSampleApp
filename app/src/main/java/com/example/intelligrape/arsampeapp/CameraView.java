package com.example.intelligrape.arsampeapp;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class CameraView extends SurfaceView implements Callback {
    private Camera camera;

    public CameraView(Context context) {
        super(context);
        // We're implementing the Callback interface and want to get notified
        // about certain surface events.
        getHolder().addCallback(this);
        // We're changing the surface to a PUSH surface, meaning we're receiving
        // all buffer data from another component - the camera, in this case.
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // Once the surface is created, simply open a handle to the camera hardware.
        camera = Camera.open();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // This method is called when the surface changes, e.g. when it's size is set.
        // We use the opportunity to initialize the camera preview display dimensions.
        Camera.Parameters p = camera.getParameters();
        Camera.Size size = getBestPreviewSize(width, height, p);
        p.setPreviewSize(size.width, size.height);
        camera.setParameters(p);

        // We also assign the preview display to this surface...
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ...and start previewing. From now on, the camera keeps pushing preview
        // images to the surface.
        camera.startPreview();
    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size bestSize = null;
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();

        bestSize = sizeList.get(0);

        for (int i = 1; i < sizeList.size(); i++) {
            if ((sizeList.get(i).width * sizeList.get(i).height) >
                    (bestSize.width * bestSize.height)) {
                bestSize = sizeList.get(i);
            }
        }

        return bestSize;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Once the surface gets destroyed, we stop the preview mode and release
        // the whole camera since we no longer need it.
        camera.stopPreview();
        camera.release();
        camera = null;
    }


    public void toggleCamera(boolean isFront) {

        int cameraId = -1;
        if (isFront)
            cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        else
            cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
        camera = Camera.open(cameraId);
        if (camera != null) {
            try {
                camera.setPreviewDisplay(this.getHolder());
                camera.startPreview();
            } catch (Exception e) {

            }
            // camera.
        }
    }


}
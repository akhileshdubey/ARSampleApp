package com.example.intelligrape.arsampeapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;

public class VirtualSponser extends Activity {
    /**
     * Called when the activity is first created.
     */

    Integer[] imageIDs = {
            R.drawable.icon,
            R.drawable.mutan_mini,
    };
    //    public static boolean invertPosition = false;
//    public static boolean isScreenshot = false;
//    public static Bitmap bmp = null;
    GLClearRenderer glClearRenderer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // When working with the camera, it's useful to stick to one orientation.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Next, we disable the application's title bar...
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // ...and the notification bar. That way, we can use the full screen.
        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN,
                LayoutParams.FLAG_FULLSCREEN);

        // Now let's create an OpenGL surface.
        GLSurfaceView glView = new GLSurfaceView(this);
        // To see the camera preview, the OpenGL surface has to be created translucently.
        // See link above.
        glView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        // The renderer will be implemented in a separate class, GLView, which I'll show next.
        glClearRenderer = new GLClearRenderer(this);
        glClearRenderer.mResouceId = this.getIntent().getExtras().getInt("resource_id");
        glClearRenderer.mAutoRotate = true;
        glView.setRenderer(glClearRenderer);
        glView.setZOrderOnTop(true);
        // Now set this as the main view.
        setContentView(glView);


        // Now also create a view which contains the camera preview...
        CameraView cameraView = new CameraView(this);
        // ...and add it, wrapping the full screen size.
        addContentView(cameraView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        RelativeLayout lContainerLayout = new RelativeLayout(this);
        lContainerLayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        lContainerLayout.addView(loadToolBox());
        addContentView(lContainerLayout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

    }


    private View loadToolBox() {

        RelativeLayout.LayoutParams lButtonParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lButtonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);


        LayoutInflater loiViewInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        loiViewInflater = LayoutInflater.from(getApplicationContext());
        View mToolBox = loiViewInflater.inflate(R.layout.toolbox, null);

        mToolBox.findViewById(R.id.zoomIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                glClearRenderer.mZ++;

            }
        });
        mToolBox.findViewById(R.id.zoomOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                glClearRenderer.mZ--;

            }
        });

        mToolBox.findViewById(R.id.visitUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = VirtualSponser.this.getIntent().getExtras().getString("url");
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });

        mToolBox.setLayoutParams(lButtonParams);

        return mToolBox;
    }

//    public class ImageAdapter extends BaseAdapter
//    {
//        private Context context;
//        private int itemBackground;
//        public ImageAdapter(Context c)
//        {
//            context = c;
//            //---setting the style---
//            TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
//            itemBackground = a.getResourceId(
//                    R.styleable.Gallery1_android_galleryItemBackground, 0);
//            a.recycle();
//        }
//        //---returns the number of images---
//        public int getCount()
//        {
//            return imageIDs.length;
//        }
//        //---returns the ID of an item---
//        public Object getItem(int position)
//        {
//            return position;
//        }
//        //---returns the ID of an item---
//        public long getItemId(int position)
//        {
//            return position;
//        }
//        //---returns an ImageView view---
//        public View getView(int position, View convertView, ViewGroup parent)
//        {
//            ImageView imageView = new ImageView(context);
//            imageView.setImageResource(imageIDs[position]);
//            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//            imageView.setLayoutParams(new Gallery.LayoutParams(150, 120));
//            imageView.setBackgroundResource(itemBackground);
//
//            if(isScreenshot)
//                imageView.setImageBitmap(bmp);
//
//            return imageView;
//        }
//    }


//    @Override
//    public boolean onTouchEvent(final MotionEvent evt) {
//
//
//        if(invertPosition)
//            invertPosition = false;
//        else
//            invertPosition = true;
//
//        if(isScreenshot)
//            isScreenshot = false;
//        else
//            isScreenshot = true;
//
////        float currentX = evt.getX();
////        float currentY = evt.getY();
////        float deltaX, deltaY;
////        switch (evt.getAction()) {
////            case MotionEvent.ACTION_MOVE:
////                // Modify rotational angles according to movement
////                deltaX = currentX - previousX;
////                deltaY = currentY - previousY;
////                renderer.angleX += deltaY * TOUCH_SCALE_FACTOR;
////                renderer.angleY += deltaX * TOUCH_SCALE_FACTOR;
////        }
////        // Save current x, y
////        previousX = currentX;
////        previousY = currentY;
//        return true;  // Event handled
//    }
}
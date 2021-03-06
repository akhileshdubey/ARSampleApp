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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class VirtualAssistant extends Activity implements View.OnTouchListener {

    private boolean isFront;
    private float mPreviousX, mPreviousY;
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
    CameraView cameraView = null;


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
        glClearRenderer.mDemoId = this.getIntent().getExtras().getInt("demo_id");
        glView.setOnTouchListener(this);

        glClearRenderer.mY = 0.0f;
        glClearRenderer.mX = -1.0f;
        glClearRenderer.mZ = -10.f;
        glView.setRenderer(glClearRenderer);
        glClearRenderer.mAutoRotate = false;
        glView.setZOrderOnTop(true);
        // Now set this as the main view.
        setContentView(glView);


        // Now also create a view which contains the camera preview...
        cameraView = new CameraView(this);

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
        mToolBox.findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(VirtualAssistant.this, "In Progress...", Toast.LENGTH_SHORT).show();

            }
        });

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


        mToolBox.findViewById(R.id.bt_capture_screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(VirtualAssistant.this, "In Progress...", Toast.LENGTH_SHORT).show();
            }
        });

        mToolBox.findViewById(R.id.btn_show_hide).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View viewVisibility = findViewById(R.id.zoomIn);

                switch (viewVisibility.getVisibility()) {
                    case View.VISIBLE:
                        findViewById(R.id.zoomIn).setVisibility(View.INVISIBLE);
                        findViewById(R.id.zoomOut).setVisibility(View.INVISIBLE);
                        findViewById(R.id.bt_capture_screen).setVisibility(View.INVISIBLE);
                        findViewById(R.id.share).setVisibility(View.INVISIBLE);
                        findViewById(R.id.visitUs).setVisibility(View.INVISIBLE);
                        findViewById(R.id.toggle).setVisibility(View.INVISIBLE);
                        findViewById(R.id.zoomIn).setVisibility(View.INVISIBLE);
                        break;
                    case View.INVISIBLE:
                        findViewById(R.id.zoomIn).setVisibility(View.VISIBLE);
                        findViewById(R.id.zoomOut).setVisibility(View.VISIBLE);
                        findViewById(R.id.bt_capture_screen).setVisibility(View.VISIBLE);
                        findViewById(R.id.share).setVisibility(View.VISIBLE);
                        findViewById(R.id.visitUs).setVisibility(View.VISIBLE);
                        findViewById(R.id.toggle).setVisibility(View.VISIBLE);
                        findViewById(R.id.zoomIn).setVisibility(View.VISIBLE);
                        break;
                }
            }
        });


//        ((Button)mToolBox.findViewById(R.id.rLeft)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                glClearRenderer.mRotate++;
//
//            }
//        });
//        ((Button)mToolBox.findViewById(R.id.rRight)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                glClearRenderer.mRotate--;
//
//            }
//        });

        mToolBox.findViewById(R.id.visitUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = "http://intelligrape.com/mobile-consultants-developers.html";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });

        mToolBox.findViewById(R.id.toggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cameraView.bringToFront();


                if (isFront)
                    isFront = false;
                else
                    isFront = true;

                cameraView.toggleCamera(isFront);


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


    @Override
    public boolean onTouch(View view, MotionEvent event) {


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mPreviousX = event.getX();
                mPreviousY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:

                float x = event.getX();
                float y = event.getY();

                if (glClearRenderer != null) {
                    float deltaX = (x - mPreviousX) / 100 / 2f;
                    float deltaY = (mPreviousY - y) / 100 / 2f;

                    glClearRenderer.mX += deltaX;
                    glClearRenderer.mY += deltaY;
                }

                mPreviousX = x;
                mPreviousY = y;
        }


        return true;


    }
}
package com.example.intelligrape.arsampeapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLClearRenderer implements Renderer {

    public int mResouceId = R.raw.intelligrape;
    public float mY = 8.0f;
    public float mX = 7.0f;
    public float mZ = -20.f;
    public float mRotate = 0.0f;
    public int mDemoId = 0;
    public boolean mAutoRotate = true;
    private TextureCube mVSponser = new TextureCube();
    private Context context;


    GLClearRenderer(Context ctx) {

        context = ctx;
    }

    //    void saveAlbum(GL10 gl)
//    {
//        Bitmap bmp = SavePixels(0, 0, windowWIDTH, windowHEIGHT, gl);  // display wid,height must be
//        bmp = rotate(bmp,DefinallyROTATION);
//        path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, "hi", null);
//        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
//        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
//        intentFilter.addDataScheme("file");
//        context.registerReceiver(mReceiver, intentFilter);
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"
//                + Environment.getExternalStorageDirectory())));
//    }
    public static Bitmap SavePixels(int x, int y, int w, int h, GL10 gl) {
        int b[] = new int[w * (y + h)];
        int bt[] = new int[w * h];
        IntBuffer ib = IntBuffer.wrap(b);
        ib.position(0);
        gl.glReadPixels(x, 0, w, y + h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ib);

        for (int i = 0, k = 0; i < h; i++, k++) {//remember, that OpenGL bitmap is incompatible with Android bitmap
            //and so, some correction need.
            for (int j = 0; j < w; j++) {
                int pix = b[i * w + j];
                int pb = (pix >> 16) & 0xff;
                int pr = (pix << 16) & 0x00ff0000;
                int pix1 = (pix & 0xff00ff00) | pr | pb;
                bt[(h - k - 1) * w + j] = pix1;
            }
        }


        Bitmap sb = Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
        return sb;
    }

    public void onDrawFrame(GL10 gl) {
        // This method is called per frame, as the name suggests.
        // For demonstration purposes, I simply clear the screen with a random translucent gray.
        //float c = 1.0f / 256 * ( System.currentTimeMillis() % 256 );
        //gl.glClearColor( c, c, c, 0.5f );
        //gl.glClear( GL10.GL_COLOR_BUFFER_BIT );
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(mX, mY, mZ);
        gl.glRotatef(mRotate, 1.0f, 1.0f, 1.0f);

        // gl.glScalef(2.0f,2.0f,2.0f);

        mVSponser.draw(gl);


        gl.glLoadIdentity();

        if (mAutoRotate)
            mRotate -= 0.15f;

//        if (CameraProjectActivity.isScreenshot) {
//
//            storeImage(gl);
//            CameraProjectActivity.isScreenshot=false;
//
//        }


    }

    private boolean storeImage(GL10 gl) {
        //get path to external storage (SD card)
        String iconsStoragePath = Environment.getExternalStorageDirectory() + "/SampleImages";
        File sdIconStorageDir = new File(iconsStoragePath);

        //create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();

        try {
            String filePath = sdIconStorageDir.toString() + "/sample.png";
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            //choose another format if PNG doesn't suit you
            Bitmap imageData = SavePixels(0, 0, 400, 400, gl);
            imageData.compress(Bitmap.CompressFormat.PNG, 100, bos);

            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        }

        return true;
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // This is called whenever the dimensions of the surface have changed.
        // We need to adapt this change for the GL viewport.
        gl.glViewport(0, 0, width, height);
        //gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // No need to do anything here.
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                GL10.GL_NICEST);

        mVSponser.loadTexture(gl, context, mResouceId);    // Load image into Texture (NEW)
        gl.glEnable(GL10.GL_TEXTURE_2D);
    }

//    public Float[] GetWorldCoords(GL10 gl,float x, float y)
//    {
//        // Initialize auxiliary variables.
//        // SCREEN height & width (ej: 320 x 480)
//        float screenW = x;
//        float screenH = y;
//
//        // Auxiliary matrix and vectors
//        // to deal with ogl.
//        float[] invertedMatrix, transformMatrix,
//                normalizedInPoint, outPoint;
//        invertedMatrix = new float[16];
//        transformMatrix = new float[16];
//        normalizedInPoint = new float[4];
//        outPoint = new float[4];
//
//        // Invert y coordinate, as android uses
//        // top-left, and ogl bottom-left.
//        int oglTouchY = (int) (screenH - touch.Y());
//
//       /* Transform the screen point to clip
//       space in ogl (-1,1) */
//        normalizedInPoint[0] =
//                (float) ((touch.X()) * 2.0f / screenW - 1.0);
//        normalizedInPoint[1] =
//                (float) ((oglTouchY) * 2.0f / screenH - 1.0);
//        normalizedInPoint[2] = - 1.0f;
//        normalizedInPoint[3] = 1.0f;
//
//       /* Obtain the transform matrix and
//       then the inverse. */
//        Print("Proj", gl.getCurrentProjection(gl));
//        Print("Model", getCurrentModelView(gl));
//        Matrix.multiplyMM(
//                transformMatrix, 0,
//               GL10. getCurrentProjection(gl), 0,
//                getCurrentModelView(gl), 0);
//        Matrix.invertM(invertedMatrix, 0,
//                transformMatrix, 0);
//
//       /* Apply the inverse to the point
//       in clip space */
//        Matrix.multiplyMV(
//                outPoint, 0,
//                invertedMatrix, 0,
//                normalizedInPoint, 0);
//
//        if (outPoint[3] == 0.0)
//        {
//            // Avoid /0 error.
//            Log.e("World coords", "ERROR!");
//            return worldPos;
//        }
//
//        // Divide by the 3rd component to find
//        // out the real position.
//        worldPos.Set(
//                outPoint[0] / outPoint[3],
//                outPoint[1] / outPoint[3]);
//
//        return worldPos;
//    }
}
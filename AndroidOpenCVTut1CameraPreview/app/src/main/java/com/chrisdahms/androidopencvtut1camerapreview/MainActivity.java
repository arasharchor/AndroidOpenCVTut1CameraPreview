package com.chrisdahms.androidopencvtut1camerapreview;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.imgproc.Imgproc;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

///////////////////////////////////////////////////////////////////////////////////////////////////
public class MainActivity extends Activity implements CvCameraViewListener2 {

    // member variables ///////////////////////////////////////////////////////////////////////////
    private static final String TAG = "OCVSample::Activity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;

    private static final int REQUEST_CAMERA = 0;

    ///////////////////////////////////////////////////////////////////////////////////////////////
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    // constructor ////////////////////////////////////////////////////////////////////////////////
    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        // if device is running android API 23 or later . . .
        if(Build.VERSION.SDK_INT >= 23) {
            // check if camera permission has not yet been granted, if we don't have camera permission yet we will need to ask
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                // ask user permission to use the camera
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            }
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);

        // set resolution to 640x480, may seem to slow if we used full resolution
        try {
            mOpenCvCameraView.setMaxFrameSize(640, 480);
        } catch (Exception exception) {
            Log.e(TAG, "unable to change resolution");
        }

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void onCameraViewStarted(int width, int height) {
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void onCameraViewStopped() {
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        Mat imgOriginal = inputFrame.rgba();
        Mat imgCanny = new Mat();
        Imgproc.Canny(imgOriginal, imgCanny, 70, 100);
        return(imgCanny);
    }
}


/*
package com.chrisdahms.androidopencvtut1camerapreview;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.opencv.android.OpenCVLoader;

///////////////////////////////////////////////////////////////////////////////////////////////////
public class MainActivity extends Activity {

    ///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), " OpenCVLoader.initDebug(), not working.");
        } else {
            Log.d(this.getClass().getSimpleName(), " OpenCVLoader.initDebug(), working.");
        }
    }

}
*/



package com.test.app.instagram.Activity;

import android.content.Context;
import android.graphics.Camera;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ViewSwitcher;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.CameraView;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.test.app.instagram.R;
import com.test.app.instagram.View.RevealBackgroundView;

import butterknife.InjectView;

/**
 * Author：DJ
 * Time：2016/4/3 0003 23:33
 * Name：Instagram
 * Description：
 */
public class TakePhotoActivity extends BaseActivity implements RevealBackgroundView.OnStateChangeListener
{

    private final static Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator ();
    private final static Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator ();

    @InjectView(R.id.vUpperPanel)
    ViewSwitcher vUpperPanel;
    @InjectView(R.id.vLowerPanel)
    ViewSwitcher vLowerPanel;
    @InjectView(R.id.vPhotoRoot)
    View vTakePhotoRoot;
    @InjectView(R.id.cameraView)
    CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);

        vUpperPanel.getViewTreeObserver ().addOnPreDrawListener (new ViewTreeObserver.OnPreDrawListener ()
        {
            @Override
            public boolean onPreDraw()
            {
                vUpperPanel.getViewTreeObserver ().removeOnPreDrawListener (this);
                vUpperPanel.setTranslationY (-vUpperPanel.getHeight ());
                vLowerPanel.setTranslationY (vLowerPanel.getHeight ());
                return true;
            }
        });
    }

    @Override
    public void onStateChange(int state)
    {
        if (RevealBackgroundView.STATE_FINISH == state)
        {
            vTakePhotoRoot.setVisibility (View.VISIBLE);
            startIntroAnimation ();
        }else
        {
            vTakePhotoRoot.setVisibility (View.INVISIBLE);
        }
    }

    private void startIntroAnimation()
    {
        vUpperPanel.animate ().translationX (0).setDuration (400).setInterpolator (DECELERATE_INTERPOLATOR);
        vLowerPanel.animate ().translationX (0).setDuration (400).setInterpolator (DECELERATE_INTERPOLATOR).start ();
    }

    @Override
    protected void onResume()
    {
        super.onResume ();
        cameraView.onResume ();
    }

    @Override
    protected void onPause()
    {
        super.onPause ();
        cameraView.onPause ();
    }


    class MyCameraHost extends SimpleCameraHost
    {

        private Camera.Size previewSize;

        public MyCameraHost(Context ctxt)
        {
            super (ctxt);
        }
    }
}

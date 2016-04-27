package com.test.app.instagram.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.test.app.instagram.R;

import butterknife.InjectView;

/**
 * Author：DJ
 * Time：2016/4/26 20:26
 * Name：Instagram
 * Description：
 */
public class PublishActivity extends BaseActivity
{
    public static final String ARG_TAKEN_PHOTO_URI = "arg_taken_photo_uri";

    @InjectView(R.id.tbFollowers)
    ToggleButton tbFollowers;
    @InjectView(R.id.tbDirect)
    ToggleButton tbDirect;
    @InjectView(R.id.ivPhoto)
    ImageView ivPhoto;

    private boolean propagatingToggleState = false;
    private Uri photoUri;
    private int photoSize;

    public static void openWithPhotoUri(Activity openActivity, Uri photoUri)
    {
        Intent intent = new Intent (openActivity, PublishActivity.class);
        intent.putExtra (ARG_TAKEN_PHOTO_URI, photoUri);
        openActivity.startActivity (intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
    }
}

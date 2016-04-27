package com.test.app.instagram.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewTreeObserver;

import com.test.app.instagram.Adapter.UserProfileAdapter;
import com.test.app.instagram.R;
import com.test.app.instagram.View.RevealBackgroundView;

import butterknife.InjectView;

/**
 * Author：DJ
 * Time：2016/4/2 0002 11:46
 * Name：Instagram
 * Description：
 */
public class UserProfileActivity extends BaseActivity implements RevealBackgroundView.OnStateChangeListener
{
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";

    @InjectView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    @InjectView(R.id.rvUserProfile)
    RecyclerView rvUserProfile;

    private UserProfileAdapter userPhotosAdapter;

    public static void startUserProfileFromLocation(int[] startingLocation, Activity
            startingActivity)
    {
        Intent intent = new Intent(startingActivity, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);
    }

    private void setupUserProfileGrid()
    {
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL);
        rvUserProfile.setLayoutManager(layoutManager);
        rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState)
            {
                userPhotosAdapter.setLockedAnimations(true);
            }
        });
    }

    private void setupRevealBackground(Bundle savedInstanceState)
    {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null)
        {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()

            {
                @Override
                public boolean onPreDraw()
                {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return false;
                }
            });
        }else
        {
            userPhotosAdapter.setLockedAnimations(true);
            vRevealBackground.setToFinishedFrame ();
        }
    }

    @Override
    public void onStateChange(int state)
    {
        if (RevealBackgroundView.STATE_FINISH == state)
        {
            rvUserProfile.setVisibility(View.VISIBLE);
            userPhotosAdapter = new UserProfileAdapter(this);
            rvUserProfile.setAdapter(userPhotosAdapter);
        }else
        {
            rvUserProfile.setVisibility(View.INVISIBLE);
        }
    }
}

package com.test.app.instagram.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.test.app.instagram.Adapter.CommentsAdapter;
import com.test.app.instagram.R;
import com.test.app.instagram.Utils;

import butterknife.InjectView;

/**
 * Author：DJ
 * Time：2016/3/26 0026 16:30
 * Name：Instagram
 * Description：
 */
public class CommentsActivity extends ActionBarActivity
{
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.contentRoot)
    LinearLayout contentRoot;
    @InjectView(R.id.rvComments)
    RecyclerView rvComments;
    @InjectView(R.id.llAddComment)
    LinearLayout llAddComment;

    @InjectView(R.id.btnSendComment)
    Button btnSendComment;
    @InjectView(R.id.etComment)
    EditText etComment;

    private CommentsAdapter commentsAdapter;
    private int drawStarLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        setupComments();
        setupSendCommentButton();

        drawStarLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        if (savedInstanceState == null)
        {
            contentRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()

            {
                @Override
                public boolean onPreDraw()
                {
                    contentRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                    startIntroAnimation();
                    return true;
                }
            });

        }

    }

    private void setupComments() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setHasFixedSize(true);

        commentsAdapter = new CommentsAdapter(this);
        rvComments.setAdapter(commentsAdapter);
        rvComments.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvComments.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    commentsAdapter.setAnimationsLocked(true);
                }
            }
        });
    }
    private void setupSendCommentButton() {
//        btnSendComment.setOnSendClickListener(this);
    }

    private void startIntroAnimation()
    {
        contentRoot.setScaleY(0.1f);
        contentRoot.setPivotY(drawStarLocation);
        llAddComment.setTranslationY(100);

        contentRoot.animate().scaleY(1).setDuration(100).setInterpolator(new
                AccelerateInterpolator()).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                animateCount();
            }
        }).start();
    }

    private void animateCount()
    {
        commentsAdapter.updateItems();
        llAddComment.animate().translationY(0).setInterpolator(new DecelerateInterpolator())
                .setDuration(200).start();

    }
    @Override
    public void onBackPressed() {
        contentRoot.animate()
                .translationY(Utils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        CommentsActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }

}


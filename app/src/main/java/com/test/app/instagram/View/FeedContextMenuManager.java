package com.test.app.instagram.View;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.test.app.instagram.Utils;

/**
 * Author：DJ
 * Time：2016/3/31 0031 20:09
 * Name：Instagram
 * Description：
 */
public class FeedContextMenuManager extends RecyclerView.OnScrollListener implements View
        .OnAttachStateChangeListener {

    private static FeedContextMenuManager instance;

    private FeedContextMenu contextMenuView;

    private boolean isContextMenuShowing;
    private boolean isContextMenuDismissing;

    public static FeedContextMenuManager getInstance() {
        if (instance == null) {
            instance = new FeedContextMenuManager();
        }
        return instance;
    }


    public void toggleContextMenuFromView(View openingView, int feedItem, FeedContextMenu
            .OnFeedContextMenuItemClickListener listener)
    {
        if (contextMenuView == null)
        {
            showContextMenuFromView(openingView, feedItem, listener);
        }else
        {
            hideContextView();
        }
    }

    private void showContextMenuFromView(final View openingView, int feedItem, FeedContextMenu
            .OnFeedContextMenuItemClickListener listener)
    {
        if (!isContextMenuShowing)
        {
            isContextMenuShowing = true;
            contextMenuView = new FeedContextMenu(openingView.getContext());
            contextMenuView.bindToItem(feedItem);
            contextMenuView.addOnAttachStateChangeListener(this);
            contextMenuView.setOnFeedMenuItemClickListener(listener);

            ((ViewGroup) openingView.getRootView().findViewById(android.R.id.content)).addView
                    (contextMenuView);

            contextMenuView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()

            {
                @Override
                public boolean onPreDraw()
                {
                    contextMenuView.getViewTreeObserver().removeOnPreDrawListener(this);
                    setContextMenuInitialPosition(openingView);
                    performShowAnimation();
                    return false;
                }
            });
        }

    }

    private void setContextMenuInitialPosition(View openingView)
    {
        final int[] openingViewLocation = new int[2];
        openingView.getLocationOnScreen(openingViewLocation);
        int additionalBottomMargin = Utils.dpToPx(16);
        contextMenuView.setTranslationX(openingViewLocation[0] - contextMenuView.getWidth() / 3);
        contextMenuView.setTranslationY(openingViewLocation[1] - contextMenuView.getHeight() -
                additionalBottomMargin);
    }

    private void performShowAnimation()
    {
        contextMenuView.setPivotX(contextMenuView.getWidth() / 2);
        contextMenuView.setPivotY(contextMenuView.getHeight());
        contextMenuView.setScaleX(0.1f);
        contextMenuView.setScaleY(0.1f);
        contextMenuView.animate().scaleX(1f).scaleY(1f).setDuration(150).setInterpolator(new
                OvershootInterpolator()).setListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                isContextMenuShowing = false;
            }
        });
    }

    public void hideContextView()
    {
        if (!isContextMenuDismissing)
        {
            isContextMenuDismissing = true;
            performDismissingAnimation();
        }

    }

    private void performDismissingAnimation()
    {
        contextMenuView.setPivotX(contextMenuView.getWidth() / 2);
        contextMenuView.setPivotY(contextMenuView.getHeight());
        contextMenuView.animate().scaleX(0.1f).scaleY(0.1f).setDuration(150).setInterpolator(new
                AccelerateInterpolator()).setStartDelay(100).setListener(new AnimatorListenerAdapter()

        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (contextMenuView != null)
                {
                    contextMenuView.dismiss();
                }
                isContextMenuDismissing = false;
            }
        });

    }

    public void onScrolled(RecyclerView recyclerView, int dx, int dy)
    {
        if (contextMenuView != null)
        {
            hideContextView();
            contextMenuView.setTranslationY(contextMenuView.getTranslationY() - dy);
        }
    }

    @Override
    public void onViewAttachedToWindow(View v) {

    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        contextMenuView = null;
    }

}
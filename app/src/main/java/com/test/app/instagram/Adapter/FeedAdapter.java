package com.test.app.instagram.Adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;

import com.test.app.instagram.R;
import com.test.app.instagram.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Author：DJ
 * Time：2016/3/2 0002 19:47
 * Name：Instagram
 * Description：
 */
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View
        .OnClickListener
{
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new
            AccelerateInterpolator();
    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new
            DecelerateInterpolator();
    private static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator
            (4);

    private static final int ANIMATED_ITEMS_COUNT = 2;

    private Context context;
    private int lastAnimatedPosition = -1;
    private int itemsCount = 0;

    private boolean animateItems = false;

    private final Map<Integer, Integer> likesCount = new HashMap<>();
    private final Map<RecyclerView.ViewHolder, AnimatorSet> likeAnimations = new HashMap<>();
    private final ArrayList<Integer> likedPositions = new ArrayList<>();


    private OnFeedItemClickListener onFeedItemClickListener;

    public FeedAdapter(Context context)
    {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);

        final CellFeedViewHolder cellFeedViewHolder = new CellFeedViewHolder(view);
        cellFeedViewHolder.btnComment.setOnClickListener(this);
        cellFeedViewHolder.btnMore.setOnClickListener(this);
        cellFeedViewHolder.ivFeedCenter.setOnClickListener(this);
        cellFeedViewHolder.btnLike.setOnClickListener(this);
        cellFeedViewHolder.ivUserProfile.setOnClickListener(this);

        return cellFeedViewHolder;
    }

    private void runEnterAnimation(View view, int position)
    {
        if (!animateItems||position >= ANIMATED_ITEMS_COUNT - 1)
        {
            return;
        }

        if (position > lastAnimatedPosition)
        {
            lastAnimatedPosition = position;
            view.setTranslationY(Utils.getScreenHeight(context));
            view.animate()
                    .translationY(0)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
    {
        runEnterAnimation(viewHolder.itemView, position);
        CellFeedViewHolder holder = (CellFeedViewHolder) viewHolder;
        if (position % 2 == 0)
        {
            holder.ivFeedCenter.setImageResource(R.drawable.img_feed_center_1);
            holder.ivFeedBottom.setImageResource(R.drawable.img_feed_bottom_1);
        } else
        {
            holder.ivFeedCenter.setImageResource(R.drawable.img_feed_center_2);
            holder.ivFeedBottom.setImageResource(R.drawable.img_feed_bottom_2);
        }
        updateLikeCounter(holder, false);
        updateHeartButton(holder, false);


        holder.btnComment.setTag(position);
        holder.btnMore.setTag(position);
        holder.btnLike.setTag(holder);
        holder.ivFeedCenter.setTag(holder);

        if (likeAnimations.containsKey(holder))
        {
            likeAnimations.get(holder).cancel();
        }
        resetLikeAnimationState(holder);
    }

    @Override
    public int getItemCount()
    {
        return itemsCount;
    }


    private void updateLikeCounter(CellFeedViewHolder holder, boolean animated)
    {
        int currentLikesCount = likesCount.get(holder.getPosition()) + 1;
        String likeCountText = context.getResources().getQuantityString(R.plurals.likes_count,
                currentLikesCount, currentLikesCount);
        if (animated)
        {
            holder.tsLikesCounter.setText(likeCountText);
        }else
        {
            holder.tsLikesCounter.setCurrentText(likeCountText);
        }

        likesCount.put(holder.getPosition(), currentLikesCount);

    }

    private void updateHeartButton(final CellFeedViewHolder holder, boolean animated)
    {
        if (animated)
        {
            if (!likeAnimations.containsKey(holder))
            {
                AnimatorSet animatorSet = new AnimatorSet();
                likeAnimations.put(holder, animatorSet);


                ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(holder.btnLike, "rotation", 0f, 360f);
                rotationAnim.setDuration(300);
                rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

                ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.btnLike, "scaleX", 0.2f, 1f);
                bounceAnimX.setDuration(300);
                bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

                ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.btnLike, "scaleY", 0.2f, 1f);
                bounceAnimY.setDuration(300);
                bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
                bounceAnimY.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationStart(Animator animation)
                    {
                        holder.btnLike.setImageResource(R.drawable.ic_heart_red);
                    }
                });

                animatorSet.play(rotationAnim);
                animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);

                animatorSet.addListener(new AnimatorListenerAdapter()
                {
                    @Override
                    public void onAnimationEnd(Animator animation)
                    {
                        resetLikeAnimationState(holder);
                    }
                });

                animatorSet.start();

            }

        }else
        {
            if (likedPositions.contains(holder.getPosition()))
            {
                holder.btnLike.setImageResource(R.drawable.ic_heart_red);
            }else
            {
                holder.btnLike.setImageResource(R.drawable.ic_heart_outline_grey);
            }
        }

    }

    @Override
    public void onClick(View view)
    {
        final int viewId = view.getId();
        if (viewId == R.id.btnComment)
        {
            if (onFeedItemClickListener != null)
            {
                onFeedItemClickListener.onCommentsClick(view, (Integer) view.getTag());
            }
        } else if (viewId == R.id.btnMore)
        {
            if (onFeedItemClickListener != null)
            {
                onFeedItemClickListener.onMoreClick(view, (Integer) view.getTag());
            }
        } else if (viewId == R.id.btnLike)
        {
            CellFeedViewHolder holder = (CellFeedViewHolder) view.getTag();
            if (!likedPositions.contains(holder.getPosition()))
            {
                likedPositions.add(holder.getPosition());
                updateLikeCounter(holder, true);
                updateHeartButton(holder, true);
            }
        } else if (viewId == R.id.ivFeedCenter)
        {
            CellFeedViewHolder holder = (CellFeedViewHolder) view.getTag();
            if (!likedPositions.contains(holder.getPosition()))
            {
                likedPositions.add(holder.getPosition());
                updateLikeCounter(holder, true);
                animatePhotoLike(holder);
                updateHeartButton(holder, false);
            }

        } else if (viewId == R.id.ivUserProfile)
        {
            if (onFeedItemClickListener != null)
            {
                onFeedItemClickListener.onProfileClick(view);
            }
        }
    }

    private void animatePhotoLike(final CellFeedViewHolder holder)
    {
        if (!likeAnimations.containsKey(holder))
        {
            holder.vBglike.setVisibility(View.VISIBLE);
            holder.ivLike.setVisibility(View.VISIBLE);

            holder.vBglike.setScaleY(0.1f);
            holder.vBglike.setScaleX(0.1f);
            holder.vBglike.setAlpha(1f);
            holder.ivLike.setScaleY(0.1f);
            holder.ivLike.setScaleX(0.1f);

            AnimatorSet animatorSet = new AnimatorSet();
            likeAnimations.put(holder, animatorSet);

            ObjectAnimator bgScaleYAnim = ObjectAnimator.ofFloat(holder.vBglike, "scaleY", 0.1f,
                    1f);
            bgScaleYAnim.setDuration(200);
            bgScaleYAnim.setInterpolator(DECELERATE_INTERPOLATOR);
            ObjectAnimator bgScaleXAnim = ObjectAnimator.ofFloat(holder.vBglike, "scaleX", 0.1f,
                    1f);
            bgScaleXAnim.setDuration(200);
            bgScaleXAnim.setInterpolator(DECELERATE_INTERPOLATOR);
            ObjectAnimator bgAlphaAnim = ObjectAnimator.ofFloat(holder.vBglike, "alpha", 1f, 0f);
            bgAlphaAnim.setDuration(200);
            bgAlphaAnim.setStartDelay(150);
            bgAlphaAnim.setInterpolator(DECELERATE_INTERPOLATOR);

            ObjectAnimator imgScaleUpYAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleY", 0.1f, 1f);
            imgScaleUpYAnim.setDuration(300);
            imgScaleUpYAnim.setInterpolator(DECELERATE_INTERPOLATOR);
            ObjectAnimator imgScaleUpXAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleX", 0.1f, 1f);
            imgScaleUpXAnim.setDuration(300);
            imgScaleUpXAnim.setInterpolator(DECELERATE_INTERPOLATOR);

            ObjectAnimator imgScaleDownYAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleY", 1f,
                    0f);
            imgScaleDownYAnim.setDuration(300);
            imgScaleDownYAnim.setInterpolator(ACCELERATE_INTERPOLATOR);
            ObjectAnimator imgScaleDownXAnim = ObjectAnimator.ofFloat(holder.ivLike, "scaleX", 1f,
                    0f);
            imgScaleDownXAnim.setDuration(300);
            imgScaleDownXAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

            animatorSet.playTogether(bgScaleYAnim, bgScaleXAnim, bgAlphaAnim, imgScaleUpYAnim,
                    imgScaleUpXAnim);
            animatorSet.play(imgScaleDownXAnim).with(imgScaleDownYAnim).after(imgScaleUpYAnim);

            animatorSet.addListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    resetLikeAnimationState(holder);
                }
            });

            animatorSet.start();

        }
    }

    private void resetLikeAnimationState(CellFeedViewHolder holder)
    {
        likeAnimations.remove(holder);
        holder.vBglike.setVisibility(View.GONE);
        holder.ivLike.setVisibility(View.GONE);
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder
    {
        @InjectView(R.id.ivFeedCenter)
        ImageView ivFeedCenter;
        @InjectView(R.id.ivFeedBottom)
        ImageView ivFeedBottom;

        @InjectView(R.id.btnLike)
        ImageButton btnLike;
        @InjectView(R.id.btnComment)
        ImageButton btnComment;
        @InjectView(R.id.btnMore)
        ImageButton btnMore;

        @InjectView(R.id.vBglike)
        View vBglike;
        @InjectView(R.id.ivLike)
        ImageView ivLike;

        @InjectView(R.id.tsLikesCounter)
        TextSwitcher tsLikesCounter;

        @InjectView(R.id.ivUserProfile)
        ImageView ivUserProfile;


        public CellFeedViewHolder(View view)
        {
            super(view);
            ButterKnife.inject(this, view);
        }
    }




    public void updateItems(boolean animated)
    {
        itemsCount = 10;
        animateItems = animated;
        fillLikeWithRandomValues();
        notifyDataSetChanged();
    }

    private void fillLikeWithRandomValues()
    {
        for (int i=0;i<getItemCount();i++)
        {
            likesCount.put(i, new Random().nextInt(100));
        }

    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener)
    {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    public interface OnFeedItemClickListener
    {
        public void onCommentsClick(View v, int itemPosition);

        public void onMoreClick(View v, int position);

        public void onProfileClick(View v);
    }
}
package com.test.app.instagram.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ViewAnimator;

import com.test.app.instagram.R;

/**
 * Author：DJ
 * Time：2016/3/29 0029 22:28
 * Name：Instagram
 * Description：
 */
public class SendCommentButton extends ViewAnimator implements View.OnClickListener
{
    public static final int STATE_SEND = 0;
    public static final int STATE_DONE = 1;

    private static final long RESET_STATE_DELAY_MILLIS = 2000;

    private int currentState;

    private OnSendClickListener onSendClickListener;

    private Runnable revertStateRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            setCurrentState(STATE_SEND);
        }
    };

    public SendCommentButton(Context context)
    {
        super(context);
        init();
    }

    public SendCommentButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.view_send_comnent_button, this, true);
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        currentState = STATE_SEND;
        super.setOnClickListener(this);
    }

    @Override
    protected void onDetachedFromWindow()
    {
        removeCallbacks(revertStateRunnable);
        super.onDetachedFromWindow();
    }

    public void setCurrentState(int state)
    {
        if (state == currentState)
        {
            return;
        }
        currentState = state;
        if (state == STATE_DONE)
        {
            setEnabled(false);
            postDelayed(revertStateRunnable, RESET_STATE_DELAY_MILLIS);
            setInAnimation(getContext(), R.anim.slide_in_send);
            setOutAnimation(getContext(), R.anim.slide_in_done);
        } else if (state == STATE_SEND)
        {
            setEnabled(true);
            setInAnimation(getContext(), R.anim.slide_out_send);
            setOutAnimation(getContext(), R.anim.slide_out_done);
        }
        showNext();
    }

    @Override
    public void onClick(View v)
    {
        if (onSendClickListener != null)
        {
            onSendClickListener.OnSendClickListener(this);
        }
    }

    public void setOnSendClickListener(OnSendClickListener onSendClickListener)
    {
        this.onSendClickListener = onSendClickListener;
    }

    @Override
    public void setOnClickListener(OnClickListener l)
    {

    }

    public interface OnSendClickListener
    {
        public void OnSendClickListener(View v);
    }
}

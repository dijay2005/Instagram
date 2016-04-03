package com.test.app.instagram.Activity;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.test.app.instagram.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Author：DJ
 * Time：2016/4/2 0002 11:11
 * Name：Instagram
 * Description：
 */
public class BaseActivity extends ActionBarActivity
{
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.ivLogo)
    ImageView ivLogo;

    private MenuItem inboxMenuItem;


    @Override
    public void setContentView(int layoutResID)
    {
        super.setContentView(layoutResID);
        ButterKnife.inject(this);
        setupToolbar();
  }



    protected void setupToolbar()
    {
        if (toolbar != null)
        {
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_menu_white);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        inboxMenuItem = menu.findItem(R.id.action_inbox);
        inboxMenuItem.setActionView(R.layout.menu_item_view);
        return true;
    }

    public Toolbar getToolbar()
    {
        return toolbar;
    }

    public MenuItem getInboxMenuItem()
    {
        return inboxMenuItem;
    }

    public ImageView getIvLogo()
    {
        return ivLogo;
    }


}

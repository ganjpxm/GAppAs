/**
 * ObsActivity.java
 * OBSC Project
 *
 * Created by Gan Jianping on 13/06/13.
 * Copyright (c) 2013 DBS. All rights reserved.
 */
package org.ganjp.gdemo.base;

import android.os.Build;
import android.os.Bundle;

import org.ganjp.gdemo.R;
import org.ganjp.glib.core.base.BaseActivity;

/**
 * <p>The activity will be extended by all the Activity</p>
 * 
 * @author Gan Jianping
 * @since 1.0.0
 */
public abstract class DemoActivity extends BaseActivity {
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.blue_dark_primary));
        }
    }
}

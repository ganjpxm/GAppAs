/**
 * CornerListView.java
 *
 * Created by Gan Jianping on 09/09/13.
 * Copyright (c) 2013 GANJP. All rights reserved.
 */
package org.ganjp.glib.core.view;

import org.ganjp.glib.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * <p>Corner List View</p>
 * 
 * @author GanJianping
 * @since v1.0.0
 */
public class CornerListView extends ListView {
	public CornerListView(Context context) {
		super(context);
	}

	public CornerListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int x = (int) ev.getX();
			int y = (int) ev.getY();
			int itemnum = pointToPosition(x, y);
			if (itemnum == AdapterView.INVALID_POSITION)
				break;
			else {
				if (itemnum == 0) {
					if (itemnum == (getAdapter().getCount() - 1)) {
						setSelector(R.drawable.style_list_round);
					} else {
						setSelector(R.drawable.style_list_top_round);
					}
				} else if (itemnum == (getAdapter().getCount() - 1))
					setSelector(R.drawable.style_list_bottom_round);
				else {
					setSelector(R.drawable.style_list_center_round);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return super.onTouchEvent(ev);
	}
}
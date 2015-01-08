/**
 * PageScrollBar.java
 *
 * Created by Gan Jianping on 09/09/13.
 * Copyright (c) 2013 GANJP. All rights reserved.
 */
package org.ganjp.glib.core.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * <p>Page Scroll Bar</p>
 * 
 * @author GanJianping
 * @since v1.0.0
 */
public class PageScrollBar extends View {
	private int width = 0;
	private int height = 0;
	private int mPageNum = 1;
	private RectF mRect; 
	private double barWidth = 0.0;

	public PageScrollBar(Context context, int pageNum) {
		this(context);
		this.init(pageNum);
	}

	public PageScrollBar(Context context) {
		super(context);
	}

	public PageScrollBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void init(int pageNum) {
		this.mPageNum = pageNum;
	}

	public void updateScrollBlock(float progress) {
		float l = progress * (float) (width); 
		barWidth = (double) (width / mPageNum);
		if (mRect == null) {
			height = getMeasuredHeight();
			width = getMeasuredWidth();
			barWidth = (double) (width / mPageNum);
			mRect = new RectF(0f, 0f, (float) barWidth, (float) height);
		} else {
			mRect = new RectF(l, 0f, (float) (barWidth + l), (float) height);
		}
		postInvalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mRect == null) {
			height = getMeasuredHeight();
			width = getMeasuredWidth();
			barWidth = (double) (width / mPageNum);
			mRect = new RectF(0f, 0f, (float) barWidth, (float) height);
		}
		Paint paint = new Paint();
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawColor(Color.GRAY);
		canvas.drawRoundRect(mRect, 5, 5, paint);
	}
}

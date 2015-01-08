/**
 * ScrollViewGroup.java
 *
 * Created by Gan Jianping on 09/09/13.
 * Copyright (c) 2013 GANJP. All rights reserved.
 */
package org.ganjp.glib.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateInterpolator;
import android.widget.Scroller;

/**
 * <p>Scroll View Group</p>
 * 
 * @author GanJianping
 * @since v1.0.0
 */
public class ScrollViewGroup extends ViewGroup {
	private Context mContext;

	public ScrollViewGroup(Context context) {
		super(context);
		this.mContext = context;
	}

	public ScrollViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	private int mWidth;
	private int mHeight;
	private int mHalfWidth;
	private Scroller mScroller;

	public void init() {
		mScroller = new Scroller(mContext, new AccelerateInterpolator());
		ViewTreeObserver observer = this.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				ScrollViewGroup.this.mWidth = getWidth();
				ScrollViewGroup.this.mHeight = getHeight();
				ScrollViewGroup.this.mHalfWidth = mWidth / 2;
			}
		});

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int left = 0;
		int top = 0;
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			child.layout(left, top, left + mWidth, top + mHeight);
			left += mWidth;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		setMeasuredDimension(width, height);
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = getChildAt(i);
			child.measure(0, 0);
		}
	}

	private float pressDownX;
	private float pressUpX;
	private float oldX;
	private float nowOffset;
	private int curPage = 0;
	private boolean isChangedPage = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			this.pressDownX = event.getX();
			this.oldX = pressDownX;
			break;
		case MotionEvent.ACTION_UP:
			this.pressUpX = event.getX();
			this.actionUp();
			break;
		case MotionEvent.ACTION_MOVE:
			this.userMove(event);
			break;
		}
		return true;
	}

	public void userMove(MotionEvent event) {
		float newX = event.getX();
		float move = oldX - newX;
		float tempOffset = nowOffset + move;
		if (tempOffset > -100 && tempOffset < (mWidth * (getChildCount() - 1)) + 100) {
			this.oldX = newX;
			this.nowOffset += move;
			scrollBy((int) move, 0);
			if (mListener != null) {
				float scrWidth = (float) mWidth * getChildCount();
				float location = (float) (nowOffset / scrWidth);
				mListener.pageScroll(location);
			}

		}
	}

	private void actionUp() {
		boolean isToLeftMove = pressDownX > pressUpX ? true : false;
		float moveWidth = pressDownX > pressUpX ? pressDownX - pressUpX
				: pressUpX - pressDownX;
		System.out.println(" pressDownX " + pressDownX + ",pressUpX "
				+ pressUpX);
		if (moveWidth >= mHalfWidth) {
			curPage = (int) (nowOffset / mWidth);
			if (isToLeftMove) {
				curPage++;
			}
			isChangedPage = true;
		}
		this.changePage(); 
		oldX = 0;
		pressDownX = 0;
		pressUpX = 0;
	}

	private void changePage() {
		if (curPage < 0) { 
			curPage = 0;
		} else if (curPage > getChildCount() - 1) {
			curPage = (getChildCount() - 1);
		}
		if (isChangedPage && mListener != null) {
			mListener.pageChanged(curPage);
		}
		System.out.println("curPage " + curPage);
		float moveoffset = curPage * mWidth;
		int dx = (int) (moveoffset - nowOffset);
		mScroller.startScroll((int) nowOffset, 0, dx, 0, 500);
		postInvalidate();
	}

	@Override
	public void computeScroll() {
		if (mScroller!=null && mScroller.computeScrollOffset()) {
			int currX = mScroller.getCurrX();
			scrollTo(currX, 0);
			postInvalidate();
			nowOffset = currX;
			if (mListener != null) {
				float scrWidth = (float) mWidth * getChildCount();
				float location = (float) (nowOffset / scrWidth);
				mListener.pageScroll(location);
			}
		}
	}

	public void moveToPage(int page) {
		if (!mScroller.isFinished()) {
			mScroller.forceFinished(true);
		}
		this.curPage = page;
		changePage();
	}

	public int getmHeight() {
		return mHeight;
	}

	public int getCurPage() {
		return curPage;
	}

	private PageChangedListener mListener;

	public void setPageChangedListener(PageChangedListener listener) {
		this.mListener = listener;
	}

	public interface PageChangedListener {
		public void pageChanged(int page);

		public void pageScroll(float location);
	}
}

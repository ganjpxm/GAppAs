/**
 * SignatureActivity.java
 * UL OCR Project
 * 
 * Created by Gan Jianping on 02/10/13.
 * Copyright (c) 2013 DBS. All rights reserved.
 */
package sg.lt.obs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.ganjp.glib.core.entity.Response;
import org.ganjp.glib.core.util.DateUtil;
import org.ganjp.glib.core.util.DialogUtil;
import org.ganjp.glib.core.util.FileUtil;
import org.ganjp.glib.core.util.HttpConnection;
import org.ganjp.glib.core.util.ImageUtil;
import org.ganjp.glib.core.util.StringUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import sg.lt.obs.common.ObsConst;
import sg.lt.obs.common.other.ObsUtil;
import sg.lt.obs.common.other.PreferenceUtil;
import sg.lt.obs.common.view.CustomeFontButton;

public class SignatureActivity extends Activity {

	private String tag = "Signature";
	RelativeLayout mContent;
	LinearLayout mResult;
	signature mSignature;
	CustomeFontButton mClear, mGetSign, mCancel;
	public static String tempDir;
	public int count = 1;
	public String current = null;
	private Bitmap mBitmap;
	View mView;
	File mypath;
	private String uniqueId;
	private Context context;
    private String mBookingVehicleItemId;
    private String mBookingNumber;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.signature_activity);


		context = this;
		tempDir = Environment.getExternalStorageDirectory() + "/tmp/";
		prepareDirectory();
		uniqueId = getTodaysDate() + "_" + getCurrentTime() + "_" + Math.random();
		current = uniqueId + ".jpg";

		Intent intent = getIntent();
        mBookingVehicleItemId = intent.getStringExtra(ObsConst.KEY_BOOKING_VEHICLE_ITEM_ID);
        mBookingNumber = intent.getStringExtra(ObsConst.KEY_BOOKING_NUMBER);
		String signatureFullPath = intent.getStringExtra(ObsConst.KEY_FILE_FULL_PATH);
		mypath = FileUtil.createFile(signatureFullPath);
		
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();  
		int height = display.getHeight();  
		int option = (int ) Math.floor((width - 100) / 208);

		mContent = (RelativeLayout) findViewById(R.id.linearLayout);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(208*option, 95*option); // or wrap_content
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
		mSignature = new signature(this, null);
		mSignature.setBackgroundColor(getResources().getColor(R.color.white));
		mSignature.setLayoutParams(layoutParams);
		mContent.addView(mSignature);
		mClear = (CustomeFontButton) findViewById(R.id.clear);
		mGetSign = (CustomeFontButton) findViewById(R.id.getsign);
		mGetSign.setEnabled(false);
		mCancel = (CustomeFontButton) findViewById(R.id.cancel);
		mView = mSignature;

		mClear.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mSignature.clear();
				mGetSign.setEnabled(false);
			}
		});

		mGetSign.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				boolean error = captureSignature();
				if (!error) {
					mView.setDrawingCacheEnabled(true);
					mSignature.save(mView);
                    //ImageUtil.addWaterMark(mypath, "valid for limousineTransport");
                    String signaturePathBookingVehicleItemIds = PreferenceUtil.getString(ObsConst.KEY_SIGNATURE_PATH_BOOKING_VEHICLE_ITEM_IDS);
                    String value = mypath.getAbsolutePath() + "," + mBookingVehicleItemId;
                    if (StringUtil.hasText(signaturePathBookingVehicleItemIds)) {
                        if (signaturePathBookingVehicleItemIds.indexOf(value)==-1) {
                            signaturePathBookingVehicleItemIds += ";" + value;
                        }
                    } else {
                        signaturePathBookingVehicleItemIds = value;
                    }
                    PreferenceUtil.saveString(ObsConst.KEY_SIGNATURE_PATH_BOOKING_VEHICLE_ITEM_IDS, signaturePathBookingVehicleItemIds);

                    String[] signaturePathBookingVehicleItemIdArr = signaturePathBookingVehicleItemIds.split(";");
                    for (String signaturePathBookingVehicleItemId : signaturePathBookingVehicleItemIdArr) {
                        final String[] arr = signaturePathBookingVehicleItemId.split(",");
                        new ObsUtil.UploadSignatureTask(context).execute(ObsConst.URL_UPLOAD_SIGNATURE, arr[0], arr[1]);
                    }
                    PreferenceUtil.saveString(ObsConst.KEY_FILE_FULL_PATH, mypath.getAbsolutePath());
					finish();
				}
			}
		});

		mCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Bundle b = new Bundle();
				b.putString("status", "cancel");
				Intent intent = new Intent();
				intent.putExtras(b);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();		
	}

	@Override
	protected void onPause() {
		super.onPause();		
	}

	private boolean captureSignature() {
		boolean error = false;
		String errorMessage = "";
		if (error) {
			Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP, 105, 50);
			toast.show();
		}
		return error;
	}

	private String getTodaysDate() {
		final Calendar c = Calendar.getInstance();
		int todaysDate = (c.get(Calendar.YEAR) * 10000)
				+ ((c.get(Calendar.MONTH) + 1) * 100)
				+ (c.get(Calendar.DAY_OF_MONTH));
		return (String.valueOf(todaysDate));
	}

	private String getCurrentTime() {
		final Calendar c = Calendar.getInstance();
		int currentTime = (c.get(Calendar.HOUR_OF_DAY) * 10000)
				+ (c.get(Calendar.MINUTE) * 100) + (c.get(Calendar.SECOND));
		return (String.valueOf(currentTime));
	}

	private boolean prepareDirectory() {
		try {
			if (makedirs()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	private boolean makedirs() {
		File tempdir = new File(tempDir);
		if (!tempdir.exists())
			tempdir.mkdirs();
		if (tempdir.isDirectory()) {
			File[] files = tempdir.listFiles();
			for (File file : files) {
				if (!file.delete()) {
				}
			}
		}
		return (tempdir.isDirectory());
	}

	public class signature extends View {
		private static final float STROKE_WIDTH = 5f;
		private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
		private Paint paint = new Paint();
		private Path path = new Path();
		private float lastTouchX;
		private float lastTouchY;
		private final RectF dirtyRect = new RectF();

		public signature(Context context, AttributeSet attrs) {
			super(context, attrs);
			paint.setAntiAlias(true);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeWidth(STROKE_WIDTH);
		}

		public void save(View v) {
			if (mBitmap == null) {
				mBitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
			}
            Canvas canvas = new Canvas(mBitmap);
			try {
				FileOutputStream mFileOutStream = new FileOutputStream(mypath);

                v.draw(canvas);
				mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, mFileOutStream);
				mFileOutStream.flush();
				mFileOutStream.close();
			} catch (Exception e) {
			}
		}

		public void clear() {
			path.reset();
			invalidate();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawPath(path, paint);

            Paint paint1 = new Paint();
            paint1.setTextSize(40);
            paint1.setColor(Color.GRAY);
            int height = canvas.getHeight();
            int width = canvas.getWidth();
            String text = "Only valid for " + mBookingNumber + " at " + DateUtil.getNowDdEmMmYYYYHhMmAmPmFormate();
            canvas.drawText(text, (width-text.length()*20)/2, height-30, paint1);
        }

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float eventX = event.getX();
			float eventY = event.getY();
			mGetSign.setEnabled(true);

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				path.moveTo(eventX, eventY);
				lastTouchX = eventX;
				lastTouchY = eventY;
				return true;

			case MotionEvent.ACTION_MOVE:

			case MotionEvent.ACTION_UP:

				resetDirtyRect(eventX, eventY);
				int historySize = event.getHistorySize();
				for (int i = 0; i < historySize; i++) {
					float historicalX = event.getHistoricalX(i);
					float historicalY = event.getHistoricalY(i);
					expandDirtyRect(historicalX, historicalY);
					path.lineTo(historicalX, historicalY);
				}
				path.lineTo(eventX, eventY);
				break;

			default:
				return false;
			}

			invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
					(int) (dirtyRect.top - HALF_STROKE_WIDTH),
					(int) (dirtyRect.right + HALF_STROKE_WIDTH),
					(int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

			lastTouchX = eventX;
			lastTouchY = eventY;

			return true;
		}

		private void expandDirtyRect(float historicalX, float historicalY) {
			if (historicalX < dirtyRect.left) {
				dirtyRect.left = historicalX;
			} else if (historicalX > dirtyRect.right) {
				dirtyRect.right = historicalX;
			}

			if (historicalY < dirtyRect.top) {
				dirtyRect.top = historicalY;
			} else if (historicalY > dirtyRect.bottom) {
				dirtyRect.bottom = historicalY;
			}
		}

		private void resetDirtyRect(float eventX, float eventY) {
			dirtyRect.left = Math.min(lastTouchX, eventX);
			dirtyRect.right = Math.max(lastTouchX, eventX);
			dirtyRect.top = Math.min(lastTouchY, eventY);
			dirtyRect.bottom = Math.max(lastTouchY, eventY);
		}
	}

}
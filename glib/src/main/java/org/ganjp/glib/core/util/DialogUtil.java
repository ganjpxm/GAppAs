/**
 * DialogUtil.java
 *
 * Created by Gan Jianping on 07/01/15.
 * Copyright (c) 2015 GANJP. All rights reserved.
 *
 */
package org.ganjp.glib.core.util;

import org.ganjp.glib.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * <p>Dialog Utility</p>
 *
 * @author GanJianping
 * @since 1.0
 */
public class DialogUtil {
	
	public static ProgressDialog sProgressDialog = null;
    /**
     * <p>Show Alert Dialog</p>
     * <pre>
     *   DialogUtil.messageAlert(UiDialogActivity.this, getString(R.string.title), getString(R.string.message));
     * </pre>
     * 
     * @param ctx
     * @param message
     */
    public static void showAlertDialog(Context ctx, String message) {
        showAlertDialog(ctx, android.R.drawable.ic_dialog_alert, ctx.getString(R.string.alert), message, null, 
        		ctx.getString(R.string.ok));
    }
 
	/**
	 * <p>Show Confirm Dialog</p>
	 * <pre>
	   DialogUtil.showConfirmDialog(UiDialogActivity.this, getString(R.string.title), getString(R.string.message), 
	 	   new DialogInterface.OnClickListener() {
		       @Override
		       public void onClick(DialogInterface dialog, int which) {
		         if (which==-1) mMessageTv.setText("confirm");
		         else if (which==-2) mMessageTv.setText("cancel");
		       }
       });
	 * </pre>
	 * 
	 * @param ctx
	 * @param title
	 * @param message
	 * @param callBack
	 */
    public static void showConfirmDialog(Context ctx, String title, String message, DialogInterface.OnClickListener callBack) {
        showAlertDialog(ctx, android.R.drawable.ic_dialog_alert, title, message, callBack, 
        		new String[]{ctx.getString(R.string.confirm), ctx.getString(R.string.cancel)});
    }
    
    /**
     * <p>Show Alert Dialog</p>
	 * <pre>
	   DialogUtil.showAlertDialog(UiDialogActivity.this, android.R.drawable.ic_dialog_alert, getString(R.string.title), getString(R.string.message), 
			new DialogInterface.OnClickListener() {
	        	@Override
	            public void onClick(DialogInterface dialog, int which) {
	        		if (which==-1) mMessageTv.setText("confirm");
	        		else if (which==-2) mMessageTv.setText("cancel");
	        		else if (which==-3) mMessageTv.setText("more");
	            }
       }, new String[]{getString(R.string.confirm), getString(R.string.more), getString(R.string.cancel)});
	 * </pre>
     * 
     * @param ctx
     * @param icon
     * @param title
     * @param message
     * @param posCallback
     * @param buttonNames
     */
    public static void showAlertDialog(Context ctx, int icon, String title, String message, DialogInterface.OnClickListener posCallback, String... buttonNames) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        if (title!=null) {
        	builder.setTitle(title);
        }
        if (message!=null) {
        	builder.setMessage(message);
        }
        if (icon!=0) {
        	builder.setIcon(icon);
        }
        builder.setCancelable(false);
        if (buttonNames!=null && buttonNames.length==1) {
        	builder.setPositiveButton(buttonNames[0], posCallback);
        } else if (buttonNames!=null && buttonNames.length==2) {
        	builder.setPositiveButton(buttonNames[0], posCallback);
        	builder.setNegativeButton(buttonNames[1], posCallback);
        } else if (buttonNames!=null && buttonNames.length==3) {
        	builder.setPositiveButton(buttonNames[0], posCallback);
        	builder.setNeutralButton(buttonNames[1], posCallback);
        	builder.setNegativeButton(buttonNames[2], posCallback);
        }
        builder.create().show();
    }
    
    /**
     * <p>Show Input Dialog</p>
	 * <pre>
        final EditText et = new EditText(UiDialogActivity.this);
		DialogUtil.showInputDialog(UiDialogActivity.this, android.R.drawable.ic_dialog_info, getString(R.string.title), 
			getString(R.string.message), et, new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int which) {
		        	if (which==-1) mMessageTv.setText("confirm : " + et.getText()); 
			        else if (which==-2) mMessageTv.setText("cancel");
		    	}
		});
	 * </pre>
	 * 
     * @param ctx
     * @param icon
     * @param title
     * @param message
     * @param et
     * @param posCallback
     */
    public static void showInputDialog(Context ctx, int icon, String title, String message, EditText et, DialogInterface.OnClickListener posCallback) {
    	final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
    	if (title!=null) {
        	builder.setTitle(title);
        }
        if (message!=null) {
        	builder.setMessage(message);
        }
        if (icon!=0) {
        	builder.setIcon(icon);
        }
        builder.setView(et);
        builder.setPositiveButton(R.string.confirm, posCallback).setNegativeButton(R.string.cancel, posCallback);
        builder.create().show();
    }
	
    /**
     * <p>Show Radios Dialog</p>
	 * <pre>
	    private static String selectItem = "";
        final CharSequence[] items = {"Red", "Green", "Blue"};
		DialogUtil.showRadiosDialog(UiDialogActivity.this, android.R.drawable.ic_dialog_info, getString(R.string.title), items, 
			new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int which) {
		    		if (which>=0) {
		    			selectItem = String.valueOf(items[which]);
		    			mMessageTv.setText(selectItem);
		    		} else if (which==-1) {
		    			mMessageTv.setText("confirm : " + selectItem);
		    		} else if (which==-2) {
		    			mMessageTv.setText("cancel");
		    		}
		    	}
		});
	 * </pre>
	 * 
     * @param ctx
     * @param icon
     * @param title
     * @param items
     * @param posCallback
     */
    public static void showRadiosDialog(Context ctx, int icon, String title, CharSequence[] items, int selectedIndex, DialogInterface.OnClickListener posCallback) {
    	final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
    	if (title!=null) {
        	builder.setTitle(title);
        }
        if (icon!=0) {
        	builder.setIcon(icon);
        }
        builder.setSingleChoiceItems(items, selectedIndex, posCallback);
        builder.setPositiveButton(R.string.confirm, posCallback).setNegativeButton(R.string.cancel, posCallback);
        builder.create().show();
    }
    
    /**
     * <p>Show Checkbox Dialog</p>
	 * <pre>
       DialogUtil.showCheckboxsDialog(UiDialogActivity.this, android.R.drawable.ic_dialog_info, getString(R.string.title), items,
			itemsSelected, new DialogInterface.OnMultiChoiceClickListener() {
			    public void onClick(DialogInterface dialogInterface, int item, boolean b) {
			        mMessageTv.setText(String.format("%s: %s", items[item], b));
			    }
			}, new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int which) {
		    		if (which==-1) mMessageTv.setText("confirm : "); 
	        		else if (which==-2) mMessageTv.setText("cancel");
		    	}
			}
	   );
	 * </pre>	
	 * 
     * @param ctx
     * @param icon
     * @param title
     * @param items
     * @param itemsSelected
     * @param multiChoiceCallback
     * @param posCallback
     */
    public static void showCheckboxsDialog(Context ctx, int icon, String title, CharSequence[] items, boolean[] itemsSelected, 
    		DialogInterface.OnMultiChoiceClickListener multiChoiceCallback, DialogInterface.OnClickListener posCallback) {
    	final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
    	if (title!=null) {
        	builder.setTitle(title);
        }
        if (icon!=0) {
        	builder.setIcon(icon);
        }
        builder.setMultiChoiceItems(items, itemsSelected, multiChoiceCallback);
        builder.setPositiveButton(R.string.confirm, posCallback).setNegativeButton(R.string.cancel, posCallback);
        builder.create().show();
    }
    
    /**
     * <p>Show List Dialog</p>
	 * <pre>
     * DialogUtil.showListDialog(UiDialogActivity.this, android.R.drawable.ic_dialog_info, getString(R.string.title), items, 
			new DialogInterface.OnClickListener() {
		    	public void onClick(DialogInterface dialog, int which) {
		    		if (which>=0) {
		    			selectItem = String.valueOf(items[which]); //selectItem is static
		    			mMessageTv.setText(selectItem);
		    		} else if (which==-1) {
		    			mMessageTv.setText("cancel");
		    		}
		    	}
	   });
	 * </pre>	
	 * 		
     * @param ctx
     * @param icon
     * @param title
     * @param items
     * @param posCallback
     */
    public static void showListDialog(Context ctx, int icon, String title, CharSequence[] items, DialogInterface.OnClickListener posCallback) {
    	final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
    	if (title!=null) {
        	builder.setTitle(title);
        }
        if (icon!=0) {
        	builder.setIcon(icon);
        }
        builder.setItems(items, posCallback);
        builder.setPositiveButton(R.string.cancel, posCallback);
        builder.create().show();
    }
    
    /**
     * <p>Show Custome Dialog</p>
	 * <pre>
        final View view = LayoutInflater.from(UiDialogActivity.this).inflate(R.layout.common_template_dialog, null);
		DialogUtil.showCustomeDialog(UiDialogActivity.this, android.R.drawable.ic_dialog_info, getString(R.string.title), view, 
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					EditText userNameEt = (EditText)view.findViewById(R.id.user_name_et);
			    	if (which==-1) mMessageTv.setText("confirm user name : " + userNameEt.getText());
		        	else if (which==-2) mMessageTv.setText("cancel");
			    }
		});
     * </pre>
     * 
     * @param ctx
     * @param icon
     * @param title
     * @param view
     * @param posCallback
     */
    public static void showCustomeDialog(Context ctx, int icon, String title, View view, DialogInterface.OnClickListener posCallback) {
		AlertDialog.Builder builder=new AlertDialog.Builder(ctx);
		if (title!=null) {
        	builder.setTitle(title);
        }
        if (icon!=0) {
        	builder.setIcon(icon);
        }
		builder.setView(view);
		builder.setPositiveButton(R.string.confirm, posCallback);
		builder.setNegativeButton(R.string.cancel, posCallback);
		builder.create().show();
    }
    
    /**
     * <p>Show Progress Dialog</p>
	 * <pre>
        ProgressDialog dialog = DialogUtil.getProgressDialog(UiDialogActivity.this, android.R.drawable.ic_dialog_info, 
				getString(R.string.title), getString(R.string.message));
		dialog.show();
     * </pre>
     * 
     * @param ctx
     * @param icon
     * @param title
     * @param title
     * @param message
     */
    public static ProgressDialog getProgressDialog(Context ctx, int icon, String title, String message) {
    	ProgressDialog dialog = new ProgressDialog(ctx);
    	if (title!=null) {
    		dialog.setTitle(title);
        }
    	if (message!=null) {
    		dialog.setMessage(message);
        }
        if (icon!=0) {
        	dialog.setIcon(icon);
        }
        return dialog;
    }
    
    /**
     * <p>Show call dialog</p>
     * <pre>
     * DialogUtil.showCallDialog(getContext(), getString(R.string.dbs), getString(R.string.contact_number_display), getString(R.string.contact_number));
     * </pre>
     * 
     * @param ctx
     * @param title
     * @param call_number_display
     * @param call_number
     */
	public static void showCallDialog(final Context ctx, final String title, final String call_number_display, final String call_number) {
		final Dialog dialog = new Dialog(ctx);
		dialog.setContentView(R.layout.common_dialog_call);
		if (title!=null) {
			dialog.setTitle(title);
		}
		
		TextView textView = (TextView) dialog.findViewById(R.id.message);
		textView.setGravity(Gravity.CENTER); 
		textView.setText(call_number_display);

		Button cancelButton = (Button) dialog.findViewById(R.id.left_btn);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		Button callBtn = (Button) dialog.findViewById(R.id.right_btn);
		callBtn.setText(ctx.getString(R.string.call));
		callBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_CALL);
				callIntent.setData(Uri.parse("tel:" + call_number));
				ctx.startActivity(callIntent);
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	
	/**
	 * <p>Show Loading Dialog</p>
	 * 
	 * @param ctx
	 */
	public static void showLoadingDialog(Context ctx) {
		sProgressDialog = DialogUtil.getProgressDialog(ctx, 0, null, ctx.getString(R.string.loading));
		sProgressDialog.show();
    }
	
	/**
	 * <p>Show Loading Dialog</p>
	 * 
	 * @param ctx
	 */
	public static void showProcessingDialog(Context ctx) {
		sProgressDialog = DialogUtil.getProgressDialog(ctx, 0, null, ctx.getString(R.string.processing));
		sProgressDialog.show();
    }
	
	/**
	 * <p>Show Syncing Dialog</p>
	 * 
	 * @param ctx
	 * @param message
	 */
	public static void showMessageDialog(Context ctx, String message) {
		sProgressDialog = DialogUtil.getProgressDialog(ctx, 0, null, message);
		sProgressDialog.show();
    }
	
	/**
	 * <p>Show Progress Dialog</p>
	 *
	 */
	public static void dismissProgressDialog() {
		if (sProgressDialog!=null) {
			sProgressDialog.dismiss();
			sProgressDialog = null;
		}
    }
	
}

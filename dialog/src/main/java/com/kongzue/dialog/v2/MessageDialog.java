package com.kongzue.dialog.v2;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.kongzue.dialog.R;
import com.kongzue.dialog.util.BaseDialog;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static com.kongzue.dialog.v2.DialogSettings.*;

public class MessageDialog extends BaseDialog {

    private AlertDialog alertDialog;
    private static MessageDialog messageDialog;
    private boolean isCanCancel = true;

    private Context context;
    private String title;
    private String message;
    private String buttonCaption = "确定";
    private DialogInterface.OnClickListener onOkButtonClickListener;

    private MessageDialog() {
    }

    //Fast Function
    public static MessageDialog show(Context context, String title, String message) {
        return show(context, title, message, "确定", null);
    }

    public static MessageDialog show(Context context, String title, String message, String buttonCaption, DialogInterface.OnClickListener onOkButtonClickListener) {
        synchronized (MessageDialog.class) {
            if (messageDialog == null) messageDialog = new MessageDialog();
            messageDialog.context = context;
            messageDialog.title = title;
            messageDialog.buttonCaption = buttonCaption;
            messageDialog.message = message;
            messageDialog.onOkButtonClickListener = onOkButtonClickListener;
            messageDialog.showDialog();
            messageDialog.log("显示消息对话框 -> " + message);
            return messageDialog;
        }
    }

    private TextView txtDialogTitle;
    private TextView txtDialogTip;
    private TextView btnSelectNegative;
    private TextView btnSelectPositive;

    private void showDialog() {
        AlertDialog.Builder builder;
        switch (type) {
            case TYPE_IOS:
                builder = new AlertDialog.Builder(context, R.style.processDialog);
                break;
            default:
                builder = new AlertDialog.Builder(context);
                break;
        }
        builder.setCancelable(isCanCancel);

        alertDialog = builder.create();
        if (dialogLifeCycleListener != null) dialogLifeCycleListener.onCreate(alertDialog);
        if (isCanCancel) alertDialog.setCanceledOnTouchOutside(true);

        Window window = alertDialog.getWindow();
        switch (type) {
            case TYPE_KONGZUE:
                alertDialog.show();
                window.setContentView(R.layout.dialog_select);

                txtDialogTitle = (TextView) window.findViewById(R.id.txt_dialog_title);
                txtDialogTip = (TextView) window.findViewById(R.id.txt_dialog_tip);
                btnSelectNegative = (TextView) window.findViewById(R.id.btn_selectNegative);
                btnSelectPositive = (TextView) window.findViewById(R.id.btn_selectPositive);

                txtDialogTitle.setText(title);
                txtDialogTip.setText(message);
                if (message.contains("\n")){
                    txtDialogTip.setGravity(Gravity.LEFT);
                }else{
                    txtDialogTip.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                btnSelectNegative.setVisibility(View.GONE);
                btnSelectPositive.setText(buttonCaption);
                btnSelectPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        if (onOkButtonClickListener != null)
                            onOkButtonClickListener.onClick(alertDialog, BUTTON_POSITIVE);
                    }
                });

                break;
            case TYPE_MATERIAL:
                alertDialog.setTitle(title);
                alertDialog.setMessage(message);
                alertDialog.setButton(BUTTON_POSITIVE, buttonCaption, onOkButtonClickListener);
                alertDialog.show();
                break;
            case TYPE_IOS:
                alertDialog.show();
                window.setContentView(R.layout.dialog_select_ios);

                txtDialogTitle = (TextView) window.findViewById(R.id.txt_dialog_title);
                txtDialogTip = (TextView) window.findViewById(R.id.txt_dialog_tip);
                btnSelectNegative = (TextView) window.findViewById(R.id.btn_selectNegative);
                btnSelectPositive = (TextView) window.findViewById(R.id.btn_selectPositive);
                ImageView splitVertical = (ImageView) window.findViewById(R.id.split_vertical);

                txtDialogTitle.setText(title);
                txtDialogTip.setText(message);
                btnSelectNegative.setVisibility(View.GONE);
                splitVertical.setVisibility(View.GONE);
                btnSelectPositive.setBackgroundResource(R.drawable.button_dialog_one);
                btnSelectPositive.setText(buttonCaption);
                btnSelectPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        if (onOkButtonClickListener!=null)onOkButtonClickListener.onClick(alertDialog, BUTTON_POSITIVE);
                    }
                });

                break;
        }

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (dialogLifeCycleListener != null) dialogLifeCycleListener.onDismiss();
            }
        });
        alertDialog.show();
        if (dialogLifeCycleListener != null) dialogLifeCycleListener.onShow(alertDialog);
    }

    public MessageDialog setCanCancel(boolean canCancel) {
        isCanCancel = canCancel;
        if (alertDialog != null) alertDialog.setCancelable(canCancel);
        return this;
    }
}

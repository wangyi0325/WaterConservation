package cn.piesat.waterconservation.view.progress;

import android.content.Context;
import android.content.DialogInterface;


/**
 *
 */
public class ProgressDialogTool {
    private static LoadingDialog lodingDialog = null;

    private ProgressDialogTool() {
    }

    public static void showDialog(Context context) {
        if (lodingDialog == null) {
            lodingDialog = new LoadingDialog(context);
        }
        lodingDialog.show();
        lodingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                dismissDialog();
            }
        });
        lodingDialog.setCanceledOnTouchOutside(false);
    }

    public static void showDialogAndMsg(Context context) {
        if (lodingDialog == null) {
            lodingDialog = new LoadingDialog(context);
        }
        lodingDialog.setMessage("Loading").show();
        lodingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                dismissDialog();
            }
        });
        lodingDialog.setCanceledOnTouchOutside(false);
    }

    public static void setMessage(String text) {
        if (lodingDialog != null) {
            lodingDialog.setMessage(text);
        }
    }

    public static void cancelable(boolean isCancleable) {
        if (lodingDialog != null) {
            lodingDialog.setCancelable(isCancleable);
        }
    }

    public static void dismissDialog() {
        if (lodingDialog != null) {
            lodingDialog.dismiss();
            lodingDialog = null;
        }

    }


}

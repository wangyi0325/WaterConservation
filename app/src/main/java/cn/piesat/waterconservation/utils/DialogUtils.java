package cn.piesat.waterconservation.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.piesat.waterconservation.MainActivity;
import cn.piesat.waterconservation.R;

/**
 * Created by sen.luo on 2018/6/25.
 */

public class DialogUtils {

    /**
     * 带编辑框的DiaLog
     * @param context
     * @param title
     * @param getEditContent
     */
    public static void   showEditDialog(Context context, String title, final GetEditContent getEditContent){

        final EditText editText =new EditText(context);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setView(editText);
        builder.setNegativeButton("确定",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getEditContent.callBackContent(editText.getText().toString());
            }
        });
        builder.setPositiveButton("取消" ,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();

    }


    public interface GetEditContent{
        void callBackContent(String content);
    }


    /**
     * 只有确认按钮的Dialog
     * @param context
     * @param message
     * @param negativeClickListener
     */
    public static void onlyConfirmDialog(Context context, String message, DialogInterface.OnClickListener negativeClickListener){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setNegativeButton("确定", negativeClickListener);
        builder.create().show();
    }
}

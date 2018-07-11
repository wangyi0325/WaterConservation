package cn.piesat.waterconservation.view.progress;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import cn.piesat.waterconservation.R;


public class LoadingDialog extends Dialog {

	private TextView tvMessage;
	private ProgressLoadView imageView;

	public LoadingDialog(Context context) {
		this(context, R.style.LoadingDialog);
	}

	public LoadingDialog(Context context, int theme) {
		super(context, theme);
		init();
	}

	private void init() {
		this.setContentView(R.layout.loding_dialog);
		Window dialogWindow = getWindow();
		dialogWindow.setGravity(Gravity.CENTER);
		tvMessage = (TextView) findViewById(R.id.id_tv_loadingmsg);
		imageView = (ProgressLoadView) findViewById(R.id.loadingImageView);
	}

	public LoadingDialog setMessage(String message) {

		if (TextUtils.isEmpty(message)) {
			tvMessage.setVisibility(View.GONE);
			LayoutParams layoutParams = (LayoutParams) imageView.getLayoutParams();
			layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			imageView.setLayoutParams(layoutParams);
		} else {
			tvMessage.setVisibility(View.VISIBLE);
			tvMessage.setText(message);
		}

		return this;
	}

}

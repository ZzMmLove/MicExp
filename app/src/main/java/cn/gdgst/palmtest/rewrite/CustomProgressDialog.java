package cn.gdgst.palmtest.rewrite;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CustomProgressDialog extends ProgressDialog {

	private final String TAG = this.getClass().getSimpleName();
	public CustomProgressDialog(Context context) {
		super(context);
	}

	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		hideNumerAndPercent();
	}

	private void hideNumerAndPercent() {
		setNumerPercentState(View.INVISIBLE);
	}

	public void showNumerAndPercent() {
		setNumerPercentState(View.VISIBLE);
	}

	private void setNumerPercentState(int visibility) {
		try {
			Method method = TextView.class.getMethod("setVisibility",
					Integer.TYPE);

			Field numField = this.getClass().getSuperclass()
					.getDeclaredField("mProgressNumber");
			numField.setAccessible(true);
			TextView numTextView = (TextView) numField.get(this);
			method.invoke(numTextView, visibility);

			Field percentField = this.getClass().getSuperclass()
					.getDeclaredField("mProgressPercent");
			percentField.setAccessible(true);
			TextView percentTextView = (TextView) percentField.get(this);
			method.invoke(percentTextView, visibility);

		} catch (Exception e) {
			Log.e(TAG,
					"Failed to invoke the progressDialog method 'setVisibility' and set 'mProgressNumber' to GONE.",
					e);
		}
	}
}
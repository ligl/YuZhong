package cn.amose.yuzhong;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
	}

	public void onLoginClick(View v) {
		// TODO invoke login service
		startActivity(new Intent(this, BulletinActivity.class));
		finish();
	}

	public void onRegisterClick(View v) {
		startActivity(new Intent(this, ProcessUserActivity.class));
	}
}
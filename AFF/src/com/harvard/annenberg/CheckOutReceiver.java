package com.harvard.annenberg;

import java.util.Hashtable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class CheckOutReceiver extends BroadcastReceiver {

	private SharedPreferences prefs;
	private Hashtable<String, String> parameters;
	public static final String UPDATE_URL = "http://mgm.funformobile.com/aff/updateIsEating.php";

	@Override
	public void onReceive(Context context, Intent intent) {
		prefs = context.getSharedPreferences("AFF", Context.MODE_PRIVATE);
		prefs.edit().putBoolean("checkedin", false).commit();
		updateStatus(intent.getStringExtra("huid"));
	}

	public void updateStatus(String HUID) {
		parameters = new Hashtable<String, String>();

		parameters.put("huid", HUID);
		parameters.put("eatStatus", "N");
		parameters.put("state", "1");

		UpdateStatusTask upl = new UpdateStatusTask(parameters);
		upl.execute(UPDATE_URL);

	}
}

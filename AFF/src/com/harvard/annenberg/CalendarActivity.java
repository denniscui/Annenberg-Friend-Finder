package com.harvard.annenberg;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/*
 * Displays Annenberg hours.
 */
public class CalendarActivity extends Activity {

	public void onCreate(Bundle bunny) {
		super.onCreate(bunny);
		WebView w = new WebView(this);
		setContentView(w);
		w.loadUrl("http://www.dining.harvard.edu/residential_dining/halls_hours.html");

	}

}

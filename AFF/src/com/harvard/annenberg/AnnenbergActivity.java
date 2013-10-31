package com.harvard.annenberg;

import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AnnenbergActivity extends Activity {

	private static final String CHECKIN_URL = "http://mgm.funformobile.com/aff/checkIn.php";
	public static final String UPDATE_URL = "http://mgm.funformobile.com/aff/updateIsEating.php";
	private ProgressDialog mProgressDialog;
	private Hashtable<String, String> parameters;
	private SharedPreferences prefs;
	private static CountDownTimer curTimer;
	private ImageView annenbergImg;
	float mx;
	float my;
	float topLeftX;
	float topLeftY;
	int imgWidth;
	int imgHeight;
	int screenWidth;
	int screenHeight;
	private int tableId = 0;

	private boolean shouldFinish;

	float beginTapX;
	float beginTapY;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		shouldFinish = this.getIntent().getBooleanExtra("STARTCODE", false);
		prefs = getSharedPreferences("AFF", MODE_PRIVATE);
		mx = 0.0f;
		my = 0.0f;
		topLeftX = 0.0f;
		topLeftY = 0.0f;
		setContentView(R.layout.annenberg_layout);
		DisplayMetrics m = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(m);
		annenbergImg = (ImageView) this.findViewById(R.id.annenberg__img);
		imgWidth = this.getResources().getDrawable(R.drawable.annenberglayout)
				.getIntrinsicWidth() + 50;
		imgHeight = this.getResources().getDrawable(R.drawable.annenberglayout)
				.getIntrinsicHeight() + 50;
		screenWidth = m.widthPixels;
		screenHeight = m.heightPixels;
		annenbergImg.scrollBy(-imgWidth / 2 + screenWidth / 2, -imgHeight / 2
				+ screenHeight / 2);
		annenbergImg.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View arg0, MotionEvent event) {

				float curX, curY;

				switch (event.getAction()) {

				case MotionEvent.ACTION_DOWN:
					mx = event.getX();
					my = event.getY();
					beginTapX = mx;
					beginTapY = my;
					break;
				case MotionEvent.ACTION_MOVE:
					curX = event.getX();
					curY = event.getY();
					annenbergImg.scrollBy((int) (mx - curX), (int) (my - curY));
					topLeftX += (mx - curX);
					topLeftY += (my - curY);
					mx = curX;
					my = curY;
					break;
				case MotionEvent.ACTION_UP:

					curX = event.getX();
					curY = event.getY();
					annenbergImg.scrollBy((int) (mx - curX), (int) (my - curY));
					topLeftX += (mx - curX);
					topLeftY += (my - curY);
					if (topLeftX < 0) {
						annenbergImg.scrollBy((int) -topLeftX, 0);
						topLeftX = 0;
					}
					if (topLeftY < 0) {
						annenbergImg.scrollBy(0, (int) -topLeftY);
						topLeftY = 0;
					}
					if (topLeftY + screenHeight > imgHeight) {
						annenbergImg.scrollBy(0, (int) (imgHeight
								- screenHeight - topLeftY));
						topLeftY = imgHeight - screenHeight;
					}
					if (topLeftX + screenWidth > imgWidth) {
						annenbergImg.scrollBy(
								(int) (imgWidth - screenWidth - topLeftX), 0);
						topLeftX = imgWidth - screenWidth;
					}
					if (Math.abs(beginTapX - curX) < 10
							&& Math.abs(beginTapY - curY) < 10) {

						// They tapped a table!
						int absoluteX = (int) (topLeftX + curX);
						int absoluteY = (int) (topLeftY + curY);
						if (absoluteX > imgWidth || absoluteY > imgHeight) {
							break;
						}
						if (absoluteX < 250 && absoluteX > 80) {
							// A column
						} else if (absoluteX < 450 && absoluteX > 280) {
							// B column
							tableId += 17;
						} else if (absoluteX < 650 && absoluteX > 480) {
							// C column
							tableId += 34;
						} else {
							break;
						}

						if (absoluteY < 85 && absoluteY > 35) {
							tableId += 1;
						} else if (absoluteY < 165 && absoluteY > 115) {
							tableId += 2;
						} else if (absoluteY < 235 && absoluteY > 185) {
							tableId += 3;
						} else if (absoluteY < 335 && absoluteY > 285) {
							tableId += 4;
						} else if (absoluteY < 410 && absoluteY > 360) {
							tableId += 5;
						} else if (absoluteY < 510 && absoluteY > 460) {
							tableId += 6;
						} else if (absoluteY < 585 && absoluteY > 535) {
							tableId += 7;
						} else if (absoluteY < 660 && absoluteY > 610) {
							tableId += 8;
						} else if (absoluteY < 735 && absoluteY > 685) {
							tableId += 9;
						} else if (absoluteY < 845 && absoluteY > 795) {
							tableId += 10;
						} else if (absoluteY < 925 && absoluteY > 875) {
							tableId += 11;
						} else if (absoluteY < 1005 && absoluteY > 955) {
							tableId += 12;
						} else if (absoluteY < 1075 && absoluteY > 1025) {
							tableId += 13;
						} else if (absoluteY < 1175 && absoluteY > 1125) {
							tableId += 14;
						} else if (absoluteY < 1250 && absoluteY > 1200) {
							tableId += 15;
						} else if (absoluteY < 1330 && absoluteY > 1280) {
							tableId += 16;
						} else if (absoluteY < 1390 && absoluteY > 1340) {
							tableId += 17;
						} else {
							break;
						}

						// DO SOMETING WITH TABLE CLICK
						final SharedPreferences prefs = getSharedPreferences(
								"AFF", MODE_PRIVATE);
						String HUID = prefs.getString("huid", "");
						if (HUID.equals("")) {
							// HOLY SHIT
							showFinalAlert("Could not determine HUID - Please log in again");
						} else {
							if (curTimer != null) {
								curTimer.cancel();
							}
							checkIn(HUID, String.valueOf(tableId));
							curTimer = new CountDownTimer(1000 * 60 * 120,
									1000 * 60 * 30) {

								@Override
								public void onTick(long millisUntilFinished) {
									if (millisUntilFinished < 1000 * 60 * 45) {
										showFinalAlert("Please check in again soon or you will be checked out");
									}
								}

								@Override
								public void onFinish() {
									// Check them out.
									SharedPreferences prefs = getSharedPreferences(
											"AFF", 0);
									Editor e = prefs.edit();
									updateStatus();

								}
							};
						}
					}
					break;
				}

				return true;
			}
		});

	}

	public void checkIn(String huid, String tableNum) {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Checking In");
		mProgressDialog.setMessage("Checking in, Please Wait");
		mProgressDialog.setIndeterminate(true);

		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
		parameters = new Hashtable<String, String>();
		parameters.put("huid", huid);
		parameters.put("table", tableNum);
		CheckInTask upl = new CheckInTask();
		upl.execute(CHECKIN_URL);
	}

	private class CheckInTask extends AsyncTask<String, Integer, String> {

		protected String doInBackground(String... searchKey) {

			String url = searchKey[0];

			try {
				return ServerDbAdapter.connectToServer(this, url, parameters);
			} catch (Exception e) {
				return null;

			}
		}

		protected void onPostExecute(String result) {
			try {
				mProgressDialog.dismiss();
				showUploadSuccess(result);
				if (shouldFinish) {
					Intent intent = AnnenbergActivity.this.getIntent();
					intent.putExtra("tableNum", tableId);
					AnnenbergActivity.this.setResult(RESULT_OK, intent);
					AnnenbergActivity.this.finish();
				}

			} catch (Exception e) {

			}

		}

		public void showUploadSuccess(String json) {
			Log.v("JSON", json);
			if (json == null) {
				String message = "An network error has occured. Please try again later";
				showFinalAlert(message);
				return;
			}
			try {
				JSONObject object = new JSONObject(json);
				String status = object.getString("status");
				status = status.trim();
				Log.v("STATUS", "Status is: " + status);
				if (status.equals("OK")) {
					String message = "You have checked in!";
					showFinalAlert(message);
				} else {
					Log.v("STATUS", status);
					String message = status;
					showFinalAlert(message);
				}
			} catch (Exception e) {

			}

		}
	};

	private void showFinalAlert(CharSequence message) {
		new AlertDialog.Builder(AnnenbergActivity.this)
				.setTitle("Notice")
				.setMessage(message)
				.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// finish();
							}
						}).setCancelable(false).show();
	}

	// Fix status
	public void updateStatus() {
		parameters = new Hashtable<String, String>();

		parameters.put("huid", prefs.getString("huid", ""));
		parameters.put("eatStatus", "N");
		parameters.put("state", "1");

		UpdateStatusTask upl = new UpdateStatusTask();
		upl.execute(UPDATE_URL);

	}

	private class UpdateStatusTask extends AsyncTask<String, Integer, String> {

		protected String doInBackground(String... searchKey) {

			String url = searchKey[0];

			try {
				// Log.v("gsearch","gsearch result with AsyncTask");
				return ServerDbAdapter.connectToServer(this, url, parameters);
				// return "SUCCESS";
				// return downloadImage(url);
			} catch (Exception e) {
				// Log.v("Exception google search","Exception:"+e.getMessage());
				return null;

			}
		}

		protected void onPostExecute(String result) {
			try {
				showUploadSuccess(result);
			} catch (Exception e) {

			}

		}

		public void showUploadSuccess(String json) {
			Log.v("JSON", json);
			if (json == null) {
				String message = "An network error has occured. Please try again later";
				showFinalAlert(message);
				return;
			}
			try {
				JSONObject object = new JSONObject(json);
				String status = object.getString("status");
				status = status.trim();
				Log.v("STATUS", "Status is: " + status);
				if (status.equals("OK")) {
					String message = "Status updated!";
					showFinalAlert(message);
					return;

				} else {
					Log.v("STATUS", status);
					String message = status;
					showFinalAlert(message);
				}
			} catch (Exception e) {

			}

		}
	};
}

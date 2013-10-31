package com.harvard.annenberg;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Adapter for a list of people at the table.
 */
public class TableAdapter extends ArrayAdapter<Person> {

	Context c;
	ArrayList<Person> people;
	static ArrayList<Bitmap> images;

	public TableAdapter(Activity activity, ArrayList<Person> people) {
		super(activity, R.layout.person_row, R.id.person_name, people);
		this.people = people;
		c = activity.getApplicationContext();
		images = new ArrayList<Bitmap>();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);

		Person p = people.get(position);

		((TextView) view.findViewById(R.id.person_name)).setText(p.getName());

		char tableLetter = (char) ((Integer.valueOf(p.getTable()) - 1) / 17 + 'A');
		String tableId = (Integer.valueOf(p.getTable()) - 1) % 17 + 1 + ""
				+ tableLetter;
		if (p.getImg().equals("Y")) {
			ImageDownloader imgDownloader = new ImageDownloader();
			imgDownloader.execute(p.getHUID(), view);
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date dateOne = null;
		java.util.Date dateTwo = null;
		try {
			dateOne = df.parse(p.getTime());
			dateTwo = Calendar.getInstance().getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long timeDiff = 0;
		if (dateOne != null && dateTwo != null)
			timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());

		int timeElapsed = (int) (timeDiff / 60000);
		((TextView) view.findViewById(R.id.person_status))
				.setText("Eating for " + timeElapsed + " mins.");

		return view;
	}

	private class ImageDownloader extends AsyncTask<Object, Integer, byte[]> {
		private String HUID;
		private View view;

		@Override
		protected byte[] doInBackground(Object... searchKey) {

			HUID = (String) searchKey[0];
			HUID = HUID.substring(HUID.lastIndexOf("/") + 1);
			view = (View) searchKey[1];

			String urlString = "http://mgm.funformobile.com/aff/img/"
					+ HUID.trim() + ".jpg";
			URL aURL;
			try {
				aURL = new URL(urlString);

				URLConnection conn = aURL.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				ByteArrayBuffer baf = new ByteArrayBuffer(50);

				int current = 0;

				while ((current = bis.read()) != -1) {

					baf.append((byte) current);

				}
				is.close();
				bis.close();

				byte[] bytes = baf.toByteArray();
				baf.clear();

				return bytes;
				// adp.notifyDataSetChanged();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}

		protected void onPostExecute(byte[] result) {

			if (result != null) {
				Log.v("We got the image", "We got the image");
				Bitmap bmp = BitmapFactory.decodeByteArray(result, 0,
						result.length);
				((ImageView) view.findViewById(R.id.person_image))
						.setImageBitmap(bmp);

			} else {
				((ImageView) view.findViewById(R.id.person_image))
						.setBackgroundResource(R.drawable.defaulticon);
			}
		}
	};
}

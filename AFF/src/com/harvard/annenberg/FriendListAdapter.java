package com.harvard.annenberg;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

/*
 * Adapter for friends list.
 */
public class FriendListAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private ExpandableListView mExpandableListView;
	private ArrayList<HashMap<String, String>> groups;
	private ArrayList<ArrayList<HashMap<String, String>>> children;
	private int[] groupStatus;
	private ArrayList<ArrayList<byte[]>> images;

	public FriendListAdapter(Context context,
			ArrayList<HashMap<String, String>> groups,
			ArrayList<ArrayList<HashMap<String, String>>> child,
			ExpandableListView expListView) {
		mContext = context;
		mExpandableListView = expListView;

		this.groups = groups;
		this.children = child;
		images = new ArrayList<ArrayList<byte[]>>();
		for (int i = 0; i < groups.size(); i++) {
			images.add(new ArrayList<byte[]>());
			for (int j = 0; j < children.get(i).size(); j++) {
				images.get(i).add(null);
			}
		}

		groupStatus = new int[groups.size()];

		for (int i = 0; i < groupStatus.length; i++)
			groupStatus[i] = 0;

		setListEvent();
	}

	private void setListEvent() {

		mExpandableListView
				.setOnGroupExpandListener(new OnGroupExpandListener() {

					public void onGroupExpand(int arg0) {
						// TODO Auto-generated method stub
						groupStatus[arg0] = 1;
					}
				});

		mExpandableListView
				.setOnGroupCollapseListener(new OnGroupCollapseListener() {

					public void onGroupCollapse(int arg0) {
						// TODO Auto-generated method stub
						groupStatus[arg0] = 0;
					}
				});
	}

	public long getChildId(int arg0, int arg1) {
		return 0;
	}

	public View getChildView(int arg0, int arg1, boolean arg2, View arg3,
			ViewGroup arg4) {
		ImageDownloader imgDownloader = new ImageDownloader();

		if (arg0 == 0) {
			RequestChildHolder childHolder;
			// if (arg3 == null) {
			// Friend Request
			arg3 = LayoutInflater.from(mContext).inflate(
					R.layout.friend_req_row, null);

			childHolder = new RequestChildHolder();
			childHolder.HUID = (TextView) arg3.findViewById(R.id.request_huid);
			childHolder.name = (TextView) arg3.findViewById(R.id.request_name);
			childHolder.img = (ImageView) arg3.findViewById(R.id.request_image);
			arg3.setTag(childHolder);
			// } else {
			// childHolder = (RequestChildHolder) arg3.getTag();
			// }

			childHolder.name.setText(children.get(arg0).get(arg1).get("name"));
			// childHolder.HUID.setText("HUID: "
			// + children.get(arg0).get(arg1).get("HUID"));

			imgDownloader.execute(children.get(arg0).get(arg1).get("HUID"),
					childHolder.img, (Integer) arg0, (Integer) arg1);

			return arg3;
		}
		PersonChildHolder childHolder;
		// if (arg3 == null) {
		arg3 = LayoutInflater.from(mContext).inflate(R.layout.person_row, null);

		childHolder = new PersonChildHolder();
		childHolder.name = (TextView) arg3.findViewById(R.id.person_name);
		childHolder.img = (ImageView) arg3.findViewById(R.id.person_image);
		childHolder.status = (TextView) arg3.findViewById(R.id.person_status);
		// childHolder.table = (TextView) arg3.findViewById(R.id.person_table);
		// childHolder.time = (TextView) arg3.findViewById(R.id.person_time);
		arg3.setTag(childHolder);
		// } else {
		// childHolder = (PersonChildHolder) arg3.getTag();
		// }
		childHolder.name.setText(children.get(arg0).get(arg1).get("name"));
		int statusID = Integer.parseInt(children.get(arg0).get(arg1)
				.get("status"));
		String status = "";

		if (statusID <= 1) {
			status = "Not at Annenberg";
		} else if (statusID == 2) {
			status = "In line";
		} else {
			status = "Eating";
		}

		imgDownloader.execute(children.get(arg0).get(arg1).get("HUID"),
				childHolder.img, (Integer) arg0, (Integer) arg1);

		int tableID = Integer.parseInt(children.get(arg0).get(arg1)
				.get("table"));
		String table = "" + ((tableID - 1) % 17 + 1);
		if (tableID > 17 && tableID <= 34)
			table += "B";
		else if (tableID > 34)
			table += "C";
		else if (tableID == 0)
			table = "N/A";
		else
			table += "A";
		// childHolder.table.setText("Table: " + table + " ");
		String time = children.get(arg0).get(arg1).get("time");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date dateOne = null;
		java.util.Date dateTwo = null;
		try {
			dateOne = df.parse(time);
			dateTwo = Calendar.getInstance().getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long timeDiff = 0;
		if (dateOne != null && dateTwo != null)
			timeDiff = Math.abs(dateOne.getTime() - dateTwo.getTime());
		int timeElapsed = (int) (timeDiff / 60000);
		if (status.equals("Eating")) {
			status = "Table " + table + " for " + timeElapsed + " mins.";
		}

		// childHolder.time.setText("Last check-in: " + time);

		childHolder.status.setText(status);
		return arg3;

	}

	public int getChildrenCount(int arg0) {
		// TODO Auto-generated method stub
		return children.get(arg0).size();
	}

	public Object getGroup(int arg0) {
		// TODO Auto-generated method stub
		return groups.get(arg0);
	}

	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groups.size();
	}

	public long getGroupId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public View getGroupView(int arg0, boolean arg1, View arg2, ViewGroup arg3) {
		// TODO Auto-generated method stub
		GroupHolder groupHolder;
		if (arg2 == null) {
			arg2 = LayoutInflater.from(mContext).inflate(R.layout.category_row,
					null);
			groupHolder = new GroupHolder();
			groupHolder.img = (ImageView) arg2.findViewById(R.id.indicator);
			groupHolder.title = (TextView) arg2.findViewById(R.id.category);
			arg2.setTag(groupHolder);
		} else {
			groupHolder = (GroupHolder) arg2.getTag();
		}
		if (groupStatus[arg0] == 0) {
			groupHolder.img.setImageResource(R.drawable.expand);
		} else {
			groupHolder.img.setImageResource(R.drawable.collapse);
		}
		groupHolder.title.setText(groups.get(arg0).get("category"));

		return arg2;
	}

	class GroupHolder {
		ImageView img;
		TextView title;
	}

	class RequestChildHolder {
		ImageView img;
		TextView name;
		TextView HUID;
	}

	class PersonChildHolder {
		ImageView img;
		TextView name;
		TextView status;
		TextView table;
		TextView time;
	}

	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

	public Object getChild(int groupPosition, int childPosition) {
		Log.v("getChild", "If this is ever called, we're in deep shit.");
		return null;
	}

	private class ImageDownloader extends AsyncTask<Object, Integer, byte[]> {
		private String HUID;
		private ImageView view;

		private Integer groupPos;
		private Integer childPos;

		@Override
		protected byte[] doInBackground(Object... searchKey) {

			HUID = (String) searchKey[0];

			HUID = HUID.substring(HUID.lastIndexOf("/") + 1);
			view = (ImageView) searchKey[1];
			groupPos = (Integer) searchKey[2];
			childPos = (Integer) searchKey[3];

			if (images.get(groupPos).get(childPos) != null)
				return images.get(groupPos).get(childPos);

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
			}

		}

		protected void onPostExecute(byte[] result) {

			if (result != null) {
				images.get(groupPos).set(childPos, result);
				Bitmap bmp = BitmapFactory.decodeByteArray(result, 0,
						result.length);
				view.setImageBitmap(bmp);
			} else {
				view.setBackgroundResource(R.drawable.defaulticon);
			}
		}
	};
}

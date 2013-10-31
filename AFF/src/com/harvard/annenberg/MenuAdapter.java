package com.harvard.annenberg;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
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
 * Adapter for the menu.
 */
public class MenuAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private ExpandableListView mExpandableListView;
	private ArrayList<HashMap<String, String>> groups;
	private String[] groupKeys;
	private ArrayList<ArrayList<HashMap<String, String>>> children;
	private String[] childKeys;
	private int[] groupStatus;

	public MenuAdapter(Context context,
			ArrayList<HashMap<String, String>> groups, String[] groupKeys,
			ArrayList<ArrayList<HashMap<String, String>>> child,
			String[] childKeys, ExpandableListView expListView) {
		mContext = context;
		mExpandableListView = expListView;

		this.groups = groups;
		this.groupKeys = groupKeys;
		this.children = child;
		this.childKeys = childKeys;

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

	public String getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return children.get(arg0).get(arg1).get(childKeys[0]);
	}

	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getChildView(int arg0, int arg1, boolean arg2, View arg3,
			ViewGroup arg4) {
		// TODO Auto-generated method stub

		ChildHolder childHolder;
		if (arg3 == null) {
			arg3 = LayoutInflater.from(mContext).inflate(R.layout.row_menu,
					null);

			childHolder = new ChildHolder();

			childHolder.name = (TextView) arg3.findViewById(R.id.name);
			arg3.setTag(childHolder);
		} else {
			childHolder = (ChildHolder) arg3.getTag();
		}

		childHolder.name
				.setText(children.get(arg0).get(arg1).get(childKeys[0]));
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
		groupHolder.title.setText(groups.get(arg0).get(groupKeys[0]));

		return arg2;
	}

	class GroupHolder {
		ImageView img;
		TextView title;
	}

	class ChildHolder {
		TextView name;
	}

	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

}

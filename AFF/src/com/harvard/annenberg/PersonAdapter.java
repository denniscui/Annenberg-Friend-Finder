package com.harvard.annenberg;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * Adapter for the people in Annenberg.
 */
public class PersonAdapter extends ArrayAdapter {
	ArrayList<Person> people;

	public PersonAdapter(Context context, ArrayList<Person> objects) {
		super(context, R.layout.person_row, R.id.person_name, objects);
		people = objects;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		((TextView) view.findViewById(R.id.person_name)).setText(people.get(
				position).getName());
		int statusID = Integer.parseInt(people.get(position).getStatus());
		String status = "";
		if (statusID == 1) {
			status = "N/A";
		}
		if (statusID == 2) {
			status = "In line";
		}
		if (statusID == 3) {
			status = "Eating";
		}
		((TextView) view.findViewById(R.id.person_status)).setText("Status: "
				+ status + " ");
		int tableID = Integer.parseInt(people.get(position).getTable());
		String table = "" + ((tableID - 1) % 17 + 1);
		if (tableID > 17 && tableID <= 34)
			table += "B";
		else if (tableID > 34)
			table += "C";
		else if (tableID == 0)
			table = "N/A";
		else
			table += "A";
		// ((TextView) view.findViewById(R.id.person_table)).setText("Table: "
		// + table + " ");
		String time = people.get(position).getTime();
		if (time.equals("null")) {
			time = "None";
		} else {
			StringTokenizer st = new StringTokenizer(time);
			st.nextToken();
			time = st.nextToken();
		}
		// ((TextView) view.findViewById(R.id.person_time))
		// .setText("Last check-in: " + time);
		ImageView i = ((ImageView) view.findViewById(R.id.person_image));
		String image = people.get(position).getImg();
		if (image.equals("") == false) {
			Uri u = Uri.parse(people.get(position).getImg());
			i.setImageURI(u);
		}

		return view;
	}
}

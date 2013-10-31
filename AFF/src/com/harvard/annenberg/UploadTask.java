package com.harvard.annenberg;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

/*
 * Uploads an image.
 */
public class UploadTask extends AsyncTask<String, Object, Object> {

	private Hashtable<String, String> parameters;
	Context context;
	public static final String UPLOAD_URL = "http://mgm.funformobile.com/aff/upload.php";
	// private String requireCookies;

	private long serverId;

	ImgUtil util;

	public UploadTask(Context context1) {
		context = context1;
		util = new ImgUtil(context);
	}

	public String upload(String serverUrl, Hashtable<String, String> params,
			Uri uri) {
		try {
			String boundary = "*****************************************";
			String newLine = "\r\n";
			int bytesAvailable;
			// int totalBytes;
			int bufferSize;
			int maxBufferSize = 4096;
			// int bytesRead;

			String filename = String.valueOf(serverId);
			URL url = new URL(serverUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestMethod("POST");
			con.setRequestProperty("Connection", "Keep-Alive");
			// con.setRequestProperty("Cookie", requireCookies);

			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			// FFMHttpUtil resizeUtil = new FFMHttpUtil(this);
			byte[] filebuf = util.getResizedImageData(uri, 320, 320);
			// byte[] filebuf = dataFromUri(uri);
			int strlen = filebuf.length;

			// InputStream fis =
			// context.getContentResolver().openInputStream(uri);
			// //
			// int strlen = fis.available();
			// if ( strlen < MmsConfig.getMaxMessageSize())
			// return null;
			// int strlen=filebuf.length;
			String ststr = "--" + boundary + newLine;
			strlen += ststr.length();
			ststr = "Content-Disposition: form-data; " + "name=\"file"
					+ "\";filename=\"" + filename + "\"" + newLine + newLine;
			strlen += ststr.length();
			ststr = newLine + "--" + boundary + "--" + newLine;
			strlen += ststr.length();
			Enumeration<String> keysforlength = params.keys();
			String keyl, vall;
			while (keysforlength.hasMoreElements()) {
				keyl = keysforlength.nextElement().toString();
				vall = params.get(keyl);
				ststr = "--" + boundary + newLine;
				strlen += ststr.length();
				ststr = "Content-Disposition: form-data;name=\"" + keyl + "\""
						+ newLine + newLine + vall + newLine + "--" + boundary
						+ "--" + newLine;
				strlen += ststr.length();
			}
			con.setFixedLengthStreamingMode(strlen);
			DataOutputStream dos = new DataOutputStream(con.getOutputStream());
			dos.writeBytes("--" + boundary + newLine);
			dos.writeBytes("Content-Disposition: form-data; " + "name=\"file"
					+ "\";filename=\"" + filename + "\"" + newLine + newLine);

			bytesAvailable = filebuf.length;
			int pos = 0;
			// totalBytes = bytesAvailable;
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			int len = bufferSize;
			while (pos < filebuf.length) {
				if (pos + bufferSize > filebuf.length)
					len = filebuf.length - pos;
				dos.write(filebuf, pos, len);
				pos += len;
			}
			// Log.v("writing byte buffer="+len, "OK"); pos += len; }

			// bytesAvailable = fis.available();
			// bufferSize = Math.min(bytesAvailable, maxBufferSize);
			// byte[] buffer = new byte[bufferSize];
			// bytesRead = fis.read(buffer, 0, bufferSize);
			// while (bytesRead > 0) {
			// dos.write(buffer, 0, bufferSize);
			// bytesAvailable = fis.available();
			// bufferSize = Math.min(bytesAvailable, maxBufferSize);
			// bytesRead = fis.read(buffer, 0, bufferSize);
			// }
			dos.writeBytes(newLine);
			dos.writeBytes("--" + boundary + "--" + newLine);
			// fis.close();
			// Now write the data

			Enumeration<String> keys = params.keys();
			String key, val;
			while (keys.hasMoreElements()) {
				key = keys.nextElement().toString();
				val = params.get(key);
				dos.writeBytes("--" + boundary + newLine);
				dos.writeBytes("Content-Disposition: form-data;name=\"" + key
						+ "\"" + newLine + newLine + val);
				dos.writeBytes(newLine);
				dos.writeBytes("--" + boundary + "--" + newLine);

			}
			dos.flush();
			dos.close();

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			StringBuffer response = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}

			String json = response.toString();
			Log.v("JSON", json);

			rd.close();
			return json;

		} catch (MalformedURLException me) {
			Log.v("MalformedURLException", me.toString());
			return null;
		} catch (IOException ie) {
			Log.v("IOException", ie.toString());
			return null;
		} catch (Exception e) {
			Log.v("Exception", "Exception caught in upload");
			Log.v("Exception", e.toString());
			return null;
			// Log.e("HREQ", "Exception: "+e.toString());
		}

	}

	@Override
	protected Object doInBackground(String... params) {
		parameters = new Hashtable<String, String>();
		parameters.put("huid", params[0] + ".jpg");

		upload(UPLOAD_URL, parameters, Uri.parse(params[1]));

		// TODO Auto-generated method stub
		return null;
	}
}

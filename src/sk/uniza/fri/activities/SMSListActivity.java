package sk.uniza.fri.activities;

import sk.uniza.fri.R;
import sk.uniza.fri.comp.SMSBase;
import sk.uniza.fri.comp.SMSBroadcastReceiver;
import sk.uniza.fri.comp.SMSSQLiteOpenHelper;
import android.app.ListActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class SMSListActivity extends ListActivity {
	private static final String TAG = SMSListActivity.class.getName();
	private static boolean isAlreadyEnabled = false;

	private SMSBase mDb;
	private Cursor mCursor;
	private SimpleCursorAdapter mAdapter;

	final Handler mHandler = new Handler();
	final Runnable mUpdater = new Runnable() {
		public void run() {
			mCursor.requery();
			mAdapter.notifyDataSetChanged();
			Log.e(TAG, "Updated");

			((TextView) findViewById(R.id.activity_smslist_tv_received_count))
					.setText("" + mAdapter.getCount());

			mHandler.postDelayed(this, mUpdateInterval);
		}
	};

	private SharedPreferences mPrefs;
	private int mUpdateInterval = 10000;
	private static final SMSBroadcastReceiver sbr = new SMSBroadcastReceiver();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smslist);

		mDb = new SMSBase(this);
		mDb.open();

		mCursor = mDb.getAllSMS();
		startManagingCursor(mCursor);

		String[] columns = new String[] {
				SMSSQLiteOpenHelper.K_SMS_RECEIVED_TEL,
				SMSSQLiteOpenHelper.K_SMS_RECEIVED_TEXT,
				SMSSQLiteOpenHelper.K_SMS_RECEIVED_TIME };

		int[] to = new int[] { R.id.view_smslist_item_tel,
				R.id.view_smslist_item_text, R.id.view_smslist_item_time, };

		mAdapter = new SimpleCursorAdapter(this, R.layout.view_smslist_item,
				mCursor, columns, to);
		setListAdapter(mAdapter);

		((TextView) findViewById(R.id.activity_smslist_tv_received_count))
				.setText("" + mAdapter.getCount());

		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			startActivity(new Intent(this, SettingsPreferenceActivity.class));
		}

		return true;
	}

	@Override
	protected void onResume() {
		mDb.open();
		startManagingCursor(mCursor);

		boolean isEnabled = mPrefs.getBoolean(
				SettingsPreferenceActivity.PREFERENCE_RECEIVER, true);
		String updateInterval = mPrefs.getString(
				SettingsPreferenceActivity.PREFERENCE_UPDATE, "10000");
		mUpdateInterval = Integer.valueOf(updateInterval);

		mHandler.postDelayed(mUpdater, mUpdateInterval);

		Log.e(TAG, "update interval: " + updateInterval);

		if (isEnabled && !isAlreadyEnabled) {
			IntentFilter filter = new IntentFilter();
			filter.addAction("android.provider.Telephony.SMS_RECEIVED");

			isAlreadyEnabled = true;
			registerReceiver(sbr, filter);
			Log.e(TAG, "Registered: " + isEnabled);
		} else if (!isEnabled && isAlreadyEnabled) {

			isAlreadyEnabled = false;
			unregisterReceiver(sbr);
			Log.e(TAG, "Unregistered: " + isEnabled);
		}

		super.onResume();
	}

	@Override
	protected void onPause() {

		// mHandler.removeCallbacks(mUpdater);

		stopManagingCursor(mCursor);
		mDb.close();
		super.onPause();
	}
}
package sk.uniza.fri.activities;

import java.util.List;
import java.util.prefs.Preferences;

import sk.uniza.fri.R;
import sk.uniza.fri.classes.SMS;
import sk.uniza.fri.comp.SMSBase;
import sk.uniza.fri.comp.SMSBroadcastReceiver;
import android.app.ListActivity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SMSListActivity extends ListActivity {
	private static final String TAG = SMSListActivity.class.getName();
	private static boolean isAlreadyEnabled = false;
	
	private SMSBase mDb;
	private SharedPreferences mPrefs;
	private static final SMSBroadcastReceiver sbr = new SMSBroadcastReceiver();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smslist);

		mDb = new SMSBase(this);
		mDb.open();
		
		List<SMS> sms = mDb.getAllSMS();
		ArrayAdapter<SMS> adapter = new ArrayAdapter<SMS>(this, android.R.layout.simple_list_item_1, sms);
		setListAdapter(adapter);
		
		((TextView) findViewById(R.id.activity_smslist_tv_received_count)).setText("" + sms.size());
		
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
		boolean isEnabled = mPrefs.getBoolean(SettingsPreferenceActivity.PREFERENCE_RECEIVER, true);
		
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
		mDb.close();
		super.onPause();
	}
}
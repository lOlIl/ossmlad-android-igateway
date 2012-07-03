package sk.uniza.fri.activities;

import java.util.List;

import sk.uniza.fri.R;
import sk.uniza.fri.classes.SMS;
import sk.uniza.fri.comp.SMSBase;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SMSListActivity extends ListActivity {

	private SMSBase db;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smslist);

		db = new SMSBase(this);
		db.open();
		
		List<SMS> sms = db.getAllSMS();
		ArrayAdapter<SMS> adapter = new ArrayAdapter<SMS>(this, android.R.layout.simple_list_item_1, sms);
		setListAdapter(adapter);
		
		((TextView) findViewById(R.id.activity_smslist_tv_received_count)).setText("" + sms.size());
	}

	@Override
	protected void onResume() {
		db.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		db.close();
		super.onPause();
	}
}
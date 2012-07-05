package sk.uniza.fri.comp;

import sk.uniza.fri.activities.SettingsPreferenceActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSBroadcastReceiver extends BroadcastReceiver {
	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	private static final String TAG = SMSBroadcastReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "Intent recieved: " + intent.getAction());

		if (intent.getAction().equals(SMS_RECEIVED)) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");
				final SmsMessage[] messages = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++) {
					messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}
				if (messages.length > -1) {
					String smsText = messages[0].getMessageBody();
					
					Log.i(TAG,
							"Message recieved: " + smsText);
					
					SMSBase db = new SMSBase(context);
					db.open();
					int receivedId = db.createSMSReceived(messages[0].getOriginatingAddress(), smsText);
					db.close();
					
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
					String URL = sp.getString(SettingsPreferenceActivity.PREFERENCE_SERVER_URL, SettingsPreferenceActivity.SERVER_URL);
					if (!URL.startsWith("http://"))
						URL = SettingsPreferenceActivity.SERVER_URL;
					
					Log.e("X", URL);
					
					SMSGatewayAsyncTask at = new SMSGatewayAsyncTask(context);
					at.execute(URL, "1", ""+ receivedId, smsText);
					
				}
			}
		}
	}
}

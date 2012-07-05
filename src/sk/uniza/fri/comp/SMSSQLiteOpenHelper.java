package sk.uniza.fri.comp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SMSSQLiteOpenHelper extends SQLiteOpenHelper {
	private final String TAG = SMSSQLiteOpenHelper.class.getName();
	private static final String DATABASE_NAME = "sms.db";
	private static final int DATABASE_VERSION = 3;
	
	public static final String T_SMS_RECEIVED = "sms_received";
	public static final String K_SMS_RECEIVED_ID = "sms_received_id";
	public static final String K_SMS_RECEIVED_TEL = "sms_received_tel";
	public static final String K_SMS_RECEIVED_TIME = "sms_received_time";
	public static final String K_SMS_RECEIVED_TEXT = "sms_received_text";

	public static final String T_SMS_SENT = "sms_sent";
	public static final String K_SMS_SENT_ID = "sms_sent_id";
	public static final String K_SMS_SENT_RECEIVED_ID = "sms_received_id";
	public static final String K_SMS_SENT_TIME = "sms_sent_time";
	public static final String K_SMS_SENT_TEXT = "sms_sent_text";

	private static final String[] DATABASE_CREATE = new String[] {
			"create table "
			+ T_SMS_RECEIVED + "(" + K_SMS_RECEIVED_ID
			+ " integer primary key, " + K_SMS_RECEIVED_TEL
			+ " varchar(16) not null, " + K_SMS_RECEIVED_TIME
			+ " datetime not null, " + K_SMS_RECEIVED_TEXT + " text not null);",
			"create table "
			+ T_SMS_SENT + "(" + K_SMS_SENT_ID
			+ " integer primary key autoincrement, " + K_SMS_SENT_RECEIVED_ID
			+ " integer not null, " + K_SMS_SENT_TIME
			+ " datetime not null, " + K_SMS_SENT_TEXT + " text not null,"
			+ " foreign key ("+ K_SMS_SENT_RECEIVED_ID +") references "+ T_SMS_RECEIVED + "("+ K_SMS_RECEIVED_ID +"));",
	};
	
	private static final String[] DATABASE_DROP = new String[] {
		"DROP TABLE IF EXISTS " + T_SMS_SENT,
		"DROP TABLE IF EXISTS " + T_SMS_RECEIVED
	};

	public SMSSQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		for (String sql : DATABASE_CREATE) {
			database.execSQL(sql);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		
		for (String sql : DATABASE_DROP) {
			db.execSQL(sql);
		}
		
		onCreate(db);
	}
}
package sk.uniza.fri.comp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SMSSQLiteOpenHelper extends SQLiteOpenHelper {
	private final String TAG = SMSSQLiteOpenHelper.class.getName();
	public static final String T_SMS_RECEIVED = "sms_received";
	public static final String K_SMS_RECEIVED_ID = "sms_received_id";
	public static final String K_SMS_RECEIVED_TEL = "sms_received_tel";
	public static final String K_SMS_RECEIVED_TIME = "sms_received_time";
	public static final String K_SMS_RECEIVED_TEXT = "sms_received_text";

	private static final String DATABASE_NAME = "commments.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ T_SMS_RECEIVED + "(" + K_SMS_RECEIVED_ID
			+ " integer primary key autoincrement, " + K_SMS_RECEIVED_TEL
			+ " varchar(16) not null, " + K_SMS_RECEIVED_TIME
			+ " datetime not null, " + K_SMS_RECEIVED_TEXT + " text not null);";

	public SMSSQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + T_SMS_RECEIVED);
		onCreate(db);
	}
}
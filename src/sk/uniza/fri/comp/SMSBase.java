package sk.uniza.fri.comp;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SMSBase {
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private SQLiteDatabase db;
	private SMSSQLiteOpenHelper dbHelper;

	public SMSBase(Context context) {
		dbHelper = new SMSSQLiteOpenHelper(context);
	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public long createReceivedSMS(String tel, String text) {
		ContentValues values = new ContentValues();
		values.put(SMSSQLiteOpenHelper.K_SMS_RECEIVED_TEL, tel);
		values.put(SMSSQLiteOpenHelper.K_SMS_RECEIVED_TEXT, text);
		values.put(SMSSQLiteOpenHelper.K_SMS_RECEIVED_TIME,
				dateFormat.format(new Date()));
		return db.insert(SMSSQLiteOpenHelper.T_SMS_RECEIVED, null, values);
	}

	public Cursor getAllSMSReceived() {
		return db.rawQuery("SELECT "+ 
			SMSSQLiteOpenHelper.K_SMS_RECEIVED_ID +" as _id, " +
			SMSSQLiteOpenHelper.K_SMS_RECEIVED_TEL + ", " +
			SMSSQLiteOpenHelper.K_SMS_RECEIVED_TEXT + ", " +
			SMSSQLiteOpenHelper.K_SMS_RECEIVED_TIME + " FROM " + SMSSQLiteOpenHelper.T_SMS_RECEIVED + " ORDER BY " + SMSSQLiteOpenHelper.K_SMS_RECEIVED_ID + " DESC" , null);
	}
}

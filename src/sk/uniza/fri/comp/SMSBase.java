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

	public int createSMSReceived(String tel, String text) {
		ContentValues values = new ContentValues();
		int receivedID = getMaxSMSReceived() + 1;
		values.put(SMSSQLiteOpenHelper.K_SMS_RECEIVED_ID, receivedID);
		values.put(SMSSQLiteOpenHelper.K_SMS_RECEIVED_TEL, tel);
		values.put(SMSSQLiteOpenHelper.K_SMS_RECEIVED_TEXT, text);
		values.put(SMSSQLiteOpenHelper.K_SMS_RECEIVED_TIME,
				dateFormat.format(new Date()));
		long count = db
				.insert(SMSSQLiteOpenHelper.T_SMS_RECEIVED, null, values);
		return (count == -1) ? -1 : receivedID;
	}

	public int createSMSSent(int receivedID, String text) {
		ContentValues values = new ContentValues();
		int sentID = getMaxSMSSent() + 1;
		values.put(SMSSQLiteOpenHelper.K_SMS_SENT_ID, sentID);
		values.put(SMSSQLiteOpenHelper.K_SMS_RECEIVED_ID, receivedID);
		values.put(SMSSQLiteOpenHelper.K_SMS_SENT_TEXT, text);
		values.put(SMSSQLiteOpenHelper.K_SMS_SENT_TIME,
				dateFormat.format(new Date()));
		long count = db.insert(SMSSQLiteOpenHelper.T_SMS_SENT, null, values);
		return (count == -1) ? -1 : sentID;
	}

	public int updateSMSSent(int sentID) {
		ContentValues values = new ContentValues();
		values.put(SMSSQLiteOpenHelper.K_SMS_SENT_WAS_SENT, 1);
		return db.update(SMSSQLiteOpenHelper.T_SMS_SENT, values,
				SMSSQLiteOpenHelper.K_SMS_SENT_ID + " = ?",
				new String[] { String.valueOf(sentID) });
	}

	public Cursor getAllSMSReceived() {
		return db.rawQuery("SELECT " + SMSSQLiteOpenHelper.K_SMS_RECEIVED_ID
				+ " as _id, " + SMSSQLiteOpenHelper.K_SMS_RECEIVED_TEL + ", "
				+ SMSSQLiteOpenHelper.K_SMS_RECEIVED_TEXT + ", "
				+ SMSSQLiteOpenHelper.K_SMS_RECEIVED_TIME + " FROM "
				+ SMSSQLiteOpenHelper.T_SMS_RECEIVED + " ORDER BY "
				+ SMSSQLiteOpenHelper.K_SMS_RECEIVED_ID + " DESC", null);
	}

	public Cursor getAllSMSSent() {
		return db.rawQuery("SELECT " + SMSSQLiteOpenHelper.K_SMS_SENT_ID
				+ " as _id, " + SMSSQLiteOpenHelper.K_SMS_RECEIVED_TEL + ", "
				+ SMSSQLiteOpenHelper.K_SMS_SENT_TEXT + " as "
				+ SMSSQLiteOpenHelper.K_SMS_RECEIVED_TEXT + ", "
				+ SMSSQLiteOpenHelper.K_SMS_SENT_TIME + " as "
				+ SMSSQLiteOpenHelper.K_SMS_RECEIVED_TIME + " FROM "
				+ SMSSQLiteOpenHelper.T_SMS_SENT + " NATURAL JOIN "
				+ SMSSQLiteOpenHelper.T_SMS_RECEIVED + " ORDER BY "
				+ SMSSQLiteOpenHelper.K_SMS_SENT_ID + " DESC", null);
	}

	private int getMaxSMSReceived() {
		Cursor cursor = db.rawQuery("SELECT MAX("
				+ SMSSQLiteOpenHelper.K_SMS_RECEIVED_ID + ") FROM "
				+ SMSSQLiteOpenHelper.T_SMS_RECEIVED, null);

		int id = 0;
		if (cursor.moveToFirst())
			id = cursor.getInt(0);

		cursor.close();
		return id;
	}

	private int getMaxSMSSent() {
		Cursor cursor = db.rawQuery("SELECT MAX("
				+ SMSSQLiteOpenHelper.K_SMS_SENT_ID + ") FROM "
				+ SMSSQLiteOpenHelper.T_SMS_SENT, null);

		int id = 0;
		if (cursor.moveToFirst())
			id = cursor.getInt(0);

		cursor.close();
		return id;
	}

	public String getSMSSender(int receivedID) {
		Cursor cursor = db.rawQuery("SELECT "
				+ SMSSQLiteOpenHelper.K_SMS_RECEIVED_TEL + " FROM "
				+ SMSSQLiteOpenHelper.T_SMS_RECEIVED + " WHERE "
				+ SMSSQLiteOpenHelper.K_SMS_RECEIVED_ID + " = ?",
				new String[] { String.valueOf(receivedID) });

		String tel = null;
		if (cursor.moveToFirst())
			tel = cursor.getString(0);

		cursor.close();
		return tel;
	}
}

package sk.uniza.fri.comp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sk.uniza.fri.classes.SMS;

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

	public List<SMS> getAllSMS() {
		List<SMS> smss = new ArrayList<SMS>();

		Cursor cursor = db.rawQuery("SELECT * FROM " + SMSSQLiteOpenHelper.T_SMS_RECEIVED, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			SMS sms = getSMSFromCursor(cursor);
			smss.add(sms);
			cursor.moveToNext();
		}
		cursor.close();
		return smss;
	}

	private SMS getSMSFromCursor(Cursor c) {
		SMS s = new SMS();
		s.setId(Integer.valueOf(c.getString(c.getColumnIndex(SMSSQLiteOpenHelper.K_SMS_RECEIVED_ID))));
		s.setTel(c.getString(c.getColumnIndex(SMSSQLiteOpenHelper.K_SMS_RECEIVED_TEL)));
		s.setText(c.getString(c.getColumnIndex(SMSSQLiteOpenHelper.K_SMS_RECEIVED_TEXT)));
		try {
			s.setTime(dateFormat.parse(c.getString(c.getColumnIndex(SMSSQLiteOpenHelper.K_SMS_RECEIVED_TIME))));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return s;
	}
}

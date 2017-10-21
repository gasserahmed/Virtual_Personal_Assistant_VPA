package com.example.vpa;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NotesDbAdapter {

	public static final String KEY_TITLE = "title";
	public static final String KEY_BODY = "body";
	public static final String KEY_ROWID = "_id";
	public static final String KEY_LOCK = "lockflag";
	public static final String COL_DATE = "date";
	public static final String KEY_DATEFORQUERY = "dateforquery";

	private static final String TAG = "NotesDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	public int there_is_locked_memo;
	private static final String DATABASE_CREATE = "create table notes (_id integer primary key autoincrement, "
			+ "title text not null, body text not null, date text not null, lockflag text not null, dateforquery text not null);";
	private static final String DATABASE_NAME = "data";
	private static final String DATABASE_TABLE = "notes";
	private static final int DATABASE_VERSION = 7;

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}

	public NotesDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public NotesDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public long createNote(String title, String body, String lockFlag) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_TITLE, title);
		initialValues.put(KEY_BODY, body);
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"E dd/MM/yyyy, hh:mm a");
		initialValues.put(COL_DATE, dateFormat.format(new Date()));
		initialValues.put(KEY_LOCK, lockFlag);
		dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		initialValues.put(KEY_DATEFORQUERY, dateFormat.format(new Date()));

		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	public boolean deleteNote(long rowId) {

		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean deleteAllNotes() {

		return mDb.delete(DATABASE_TABLE, null, null) > 0;
	}

	public Cursor fetchAllNotes() {
		// this part is for searching if there's memo locked or not
		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TITLE,
				KEY_BODY, COL_DATE, KEY_LOCK, KEY_DATEFORQUERY }, KEY_LOCK
				+ "=" + "1", null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		if (!mCursor.moveToFirst()) {
			there_is_locked_memo = 0;
		} else
			there_is_locked_memo = 1;
		//
		return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TITLE,
				KEY_BODY, COL_DATE, KEY_LOCK, KEY_DATEFORQUERY }, null, null,
				null, null, KEY_DATEFORQUERY + " DESC");
	}

	public Cursor fetchNote(long rowId) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE, new String[] { KEY_ROWID, KEY_TITLE,
				KEY_BODY, COL_DATE, KEY_LOCK, KEY_DATEFORQUERY }, KEY_ROWID
				+ "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public boolean updateNote(long rowId, String title, String body,
			String lockFlag) {
		ContentValues args = new ContentValues();
		Cursor note = fetchNote(rowId);
		String old_body = note.getString(note
				.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));
		args.put(KEY_TITLE, title);
		args.put(KEY_BODY, body);
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"E dd/MM/yyyy, hh:mm a");
		if (!body.equals(old_body)) {
			args.put(COL_DATE, dateFormat.format(new Date()));
			dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			args.put(KEY_DATEFORQUERY, dateFormat.format(new Date()));
		}
		args.put(KEY_LOCK, lockFlag);

		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}
}

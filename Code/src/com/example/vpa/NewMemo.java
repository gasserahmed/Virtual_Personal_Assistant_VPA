package com.example.vpa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class NewMemo extends Activity {
	private Long mRowId;
	private NotesDbAdapter mDbHelper;
	private Menu menu;
	Cursor note;
	EditText et;
	boolean empty_cancel_flag = false;
	private String lockFlag, memo_old_body, old_body = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new NotesDbAdapter(this);
		mDbHelper.open();
		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		// to hide the keyboard
		setContentView(R.layout.activity_new_memo);
		et = (EditText) findViewById(R.id.editText);
		mRowId = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(NotesDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
					: null;
			lockFlag = "0";
		}
		if (mRowId != null) {
			note = mDbHelper.fetchNote(mRowId);
			memo_old_body = note.getString(note
					.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));
		}
		populateFields();
		if (et.getText().toString().isEmpty()) {
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			et.requestFocus();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.menu = menu;
		getMenuInflater().inflate(R.menu.new_memo, menu);
		MenuItem m = menu.findItem(R.id.memo_lock);
		if (lockFlag.equals("1"))
			m.setIcon(R.drawable.ic_unlock);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.memo_save:
			save_cancel();
			return true;
		case R.id.memo_cancel:
			if (mRowId != null) {
				if (!memo_old_body.equals(""))
					old_body = memo_old_body;
			}
			empty_cancel_flag = true;
			finish();
			return true;
		case R.id.memo_edit:
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			et.requestFocus();
			imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
			return true;
		case R.id.memo_clear:
			// et.setTextColor(getResources().getColor(R.color.white));
			if (mRowId == null) {
				empty_cancel_flag = true;
				finish();
			} else {
				new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_DARK)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.memo)
						.setMessage("Are you sure to clear this memo?")
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										mDbHelper.deleteNote(mRowId);
										empty_cancel_flag = true;
										finish();
									}

								}).setNegativeButton("No", null).show();
			}
			return true;
		case R.id.memo_lock:
			if (lockFlag.startsWith("0") && !et.getText().toString().isEmpty()) {
				lockFlag = "1";
				Toast.makeText(getApplicationContext(), "Memo is locked",
						Toast.LENGTH_LONG).show();
				item.setIcon(R.drawable.ic_unlock);

			} else if (lockFlag.startsWith("1")) {
				lockFlag = "0";
				Toast.makeText(getApplicationContext(), "Memo is unlocked",
						Toast.LENGTH_LONG).show();
				item.setIcon(R.drawable.ic_lock);
			} else
				Toast.makeText(getApplicationContext(), "No memo to lock",
						Toast.LENGTH_LONG).show();
			return true;

		}
		return super.onOptionsItemSelected(item);
	}

	private void populateFields() {
		if (mRowId != null) {
			note = mDbHelper.fetchNote(mRowId);
			et.setText(note.getString(note
					.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
			lockFlag = note.getString(note
					.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCK));
			et.setSelection(et.getText().length());
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	private void saveState() {
		String title = et.getText().toString();
		String body = et.getText().toString();
		if (old_body != null) {
			title = old_body;
			body = old_body;
		}
		if (lockFlag.equals("1")) {
			title = "***Locked***";
		}

		if (empty_cancel_flag == false) {
			if (mRowId == null) {
				long id = mDbHelper.createNote(title, body, lockFlag);
				if (id > 0) {
					mRowId = id;
				}
			} else {
				mDbHelper.updateNote(mRowId, title, body, lockFlag);
			}
		}
	}

	@Override
	public void onBackPressed() {
		save_cancel();
	}

	public void save_cancel() {
		if (et.getText().toString().isEmpty()) {
			if (mRowId != null)
				mDbHelper.deleteNote(mRowId);
			Toast.makeText(getApplicationContext(), "No memo to save",
					Toast.LENGTH_LONG).show();
			empty_cancel_flag = true;
		} else
			setResult(RESULT_OK);
		finish();
	}
}

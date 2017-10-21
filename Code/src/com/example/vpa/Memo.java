package com.example.vpa;

import java.util.ArrayList;

import com.example.vpa.R.string;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Memo extends ListActivity {
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int LOCK_ID = Menu.FIRST + 2;
	TextView tv;
	private NotesDbAdapter mDbHelper;
	private UserCustomAdapter userAdapter;
	private SharedPreferences prefs;
	private Cursor note;
	private String pin = "";
	private int pinFlag, pinFlag2;
	private long id_listItem, context_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notes_list);
		mDbHelper = new NotesDbAdapter(this);
		mDbHelper.open();
		note = mDbHelper.fetchAllNotes();
		fillData();
		registerForContextMenu(getListView());
		prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		pin = prefs.getString("PIN", "");
		if (pin.equals("")) {
			pinFlag = 1;
			alertDialog();
		} else
			pinFlag = 2;
	}

	protected void onPause() {
		super.onPause();
		Editor editPrefs = prefs.edit();
		editPrefs.putString("PIN", pin);
		// editPrefs.remove("PIN");
		editPrefs.commit();
	}

	private void fillData() {
		note = mDbHelper.fetchAllNotes();
		startManagingCursor(note);
		String[] from = new String[] { NotesDbAdapter.KEY_TITLE,
				NotesDbAdapter.COL_DATE };
		int[] to = new int[] { R.id.title_text, R.id.date_text };
		SimpleCursorAdapter notes = new SimpleCursorAdapter(this,
				R.layout.notes_row, note, from, to);
		this.userAdapter = new UserCustomAdapter(this, R.layout.notes_row,
				note, from, to);
		setListAdapter(this.userAdapter);
		// setListAdapter(notes);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		id_listItem = id;
		pinFlag2 = 1;
		openNote();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.memo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
		case R.id.action_add:
			openNewMemo();
			return true;
		case R.id.clear_all:
			if (note.getCount() != 0) {
				if (mDbHelper.there_is_locked_memo != 0) {
					Toast.makeText(getApplicationContext(),
							"Can't clear memos.\nLocked memo exists",
							Toast.LENGTH_LONG).show();
					break;
				}
				new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle(R.string.memo)
						.setMessage("Are you sure to clear all memos?")
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										mDbHelper.deleteAllNotes();
										fillData();
									}

								}).setNegativeButton("No", null).show();
			} else
				Toast.makeText(getApplicationContext(), "No memo to clear",
						Toast.LENGTH_LONG).show();

			return true;
		case R.id.pin_code:
			alertDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View v,
	// ContextMenuInfo menuInfo) {
	// super.onCreateContextMenu(menu, v, menuInfo);
	// AdapterView.AdapterContextMenuInfo info =
	// (AdapterView.AdapterContextMenuInfo) menuInfo;
	// note = mDbHelper.fetchNote(info.id);
	// menu.add(0, DELETE_ID, 0, R.string.menu_clear);
	// menu.setHeaderTitle(note.getString(note
	// .getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
	// if (note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCK))
	// .startsWith("1"))
	// menu.add(0, LOCK_ID, 0, "Unlock");
	// else
	// menu.add(0, LOCK_ID, 0, R.string.memo_lock);
	// }

	// @Override
	// public boolean onContextItemSelected(MenuItem item) {
	// final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
	// .getMenuInfo();
	// context_id = info.id;
	// note = mDbHelper.fetchNote(info.id);
	// startManagingCursor(note);
	// switch (item.getItemId()) {
	// case DELETE_ID:
	//
	// if (note.getString(
	// note.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCK))
	// .startsWith("0")) {
	// new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
	// .setIcon(android.R.drawable.ic_dialog_alert)
	// .setTitle(
	// note.getString(note
	// .getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)))
	// .setMessage("Are you sure to clear this memo?")
	// .setPositiveButton("Yes",
	// new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog,
	// int which) {
	// mDbHelper.deleteNote(info.id);
	// fillData();
	// }
	//
	// }).setNegativeButton("No", null).show();
	// } else
	// Toast.makeText(getApplicationContext(), "Memo is locked",
	// Toast.LENGTH_LONG).show();
	//
	// return true;
	// case LOCK_ID:
	// pinFlag2 = 2;
	// openNote();
	// return true;
	// }
	// return super.onContextItemSelected(item);
	// }

	public void openNewMemo() {
		Intent intent = new Intent(this, NewMemo.class);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}

	public void alertDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this,
				AlertDialog.THEME_HOLO_DARK);
		alert.setTitle(R.string.pin_code);
		final EditText et = new EditText(this);
		final EditText et2 = new EditText(this);
		et.setHint(R.string.enter_current_pin_code);
		et.setTextColor(Color.parseColor("#FFFFFF"));
		et.setGravity(Gravity.CENTER);
		et.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		et.setSingleLine(true);
		et.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		et2.setSingleLine(true);
		et2.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		et2.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		et2.setGravity(Gravity.CENTER);
		et2.setTextColor(Color.parseColor("#FFFFFF"));
		et2.setHint(R.string.enter_new_pin_code);
		if (pinFlag == 1) {
			et.setHint(R.string.first_time_pincode);
			alert.setView(et);
		} else if (pinFlag == 2) {
			LinearLayout ll = new LinearLayout(this);
			ll.setOrientation(LinearLayout.VERTICAL);
			ll.addView(et);
			ll.addView(et2);
			alert.setView(ll);
		}
		alert.setPositiveButton(R.string.set,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (!et.getText().toString().isEmpty()) {
							if (pinFlag == 1) {
								pin = et.getText().toString();
								Toast.makeText(getApplicationContext(),
										"PIN set!", Toast.LENGTH_LONG).show();
								pinFlag = 2;
							} else {
								if (!et2.getText().toString().isEmpty()) {
									if (et.getText().toString().equals(pin)) {
										pin = et2.getText().toString();
										Toast.makeText(getApplicationContext(),
												"PIN changed!",
												Toast.LENGTH_LONG).show();
									} else
										Toast.makeText(getApplicationContext(),
												"Wrong current PIN",
												Toast.LENGTH_LONG).show();
								} else
									Toast.makeText(getApplicationContext(),
											"Empty \"new PIN\" slot!",
											Toast.LENGTH_LONG).show();
							}
						} else
							Toast.makeText(getApplicationContext(),
									"Empty \"current PIN\" slot!",
									Toast.LENGTH_LONG).show();
					}
				});
		AlertDialog dialog = alert.create();
		dialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		dialog.show();

	}

	public void openNote() {
		final Intent i = new Intent(this, NewMemo.class);
		i.putExtra(NotesDbAdapter.KEY_ROWID, id_listItem);
		AlertDialog.Builder alert = new AlertDialog.Builder(this,
				AlertDialog.THEME_HOLO_DARK);
		alert.setTitle(R.string.pin_code);
		final EditText et = new EditText(this);
		et.setSingleLine(true);
		et.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		et.setGravity(Gravity.CENTER);
		et.setHint(R.string.enter_pin);
		et.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		et.setTextColor(Color.parseColor("#FFFFFF"));
		alert.setView(et);
		String s;
		if (pinFlag2 == 1) {
			if (note.getString(
					note.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCK))
					.equals("0")) {
				startActivityForResult(i, ACTIVITY_EDIT);
				return;
			} else {
				s = "Open";
				note = mDbHelper.fetchNote(id_listItem);
			}
		} else
			s = "Unlock";
		if (pinFlag2 == 2) {
			note = mDbHelper.fetchNote(context_id);
			if (note.getString(
					note.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCK))
					.equals("0")) {
				mDbHelper
						.updateNote(
								context_id,
								"***Locked***",
								note.getString(note
										.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)),
								"1");
				Toast.makeText(getApplicationContext(), "Memo Locked",
						Toast.LENGTH_LONG).show();
				fillData();
				return;
			}
		}
		alert.setPositiveButton(s, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				String pin2 = et.getText().toString();
				if (pin2.equals(pin)) {
					if (pinFlag2 == 1)
						startActivityForResult(i, ACTIVITY_EDIT);
					else {
						if (note.getString(
								note.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCK))
								.equals("1")) {
							mDbHelper.updateNote(
									context_id,
									note.getString(note
											.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)),
									note.getString(note
											.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)),
									"0");
							Toast.makeText(getApplicationContext(),
									"Memo Unlocked", Toast.LENGTH_LONG).show();
							fillData();
						}

					}
				} else
					Toast.makeText(getApplicationContext(), "Wrong PIN code",
							Toast.LENGTH_LONG).show();
			}
		});
		alert.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				fillData();
				dialog.dismiss();
			}
		});
		AlertDialog dialog = alert.create();

		dialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		dialog.show();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)

	{

		setContentView(R.layout.notes_list);

		super.onConfigurationChanged(newConfig);

	}

	// -------------------------------UserCustomAdapterClass-------------------------------

	public class UserCustomAdapter extends SimpleCursorAdapter implements
			OnClickListener {

		private final Context mContext;
		private final int mLayout;
		private final Cursor mCursor;
		private NotesDbAdapter mDbHelper;
		private Cursor note2;
		private final int mIdIndex;
		private final LayoutInflater mLayoutInflater;

		private final class ViewHolder {

			public ImageButton lock_button;
			public ImageButton clear_button;
			public TextView title;
			public TextView date;
		}

		public UserCustomAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to) {
			super(context, layout, c, from, to);
			this.mContext = context;
			this.mLayout = layout;
			this.mCursor = c;
			this.mIdIndex = mCursor.getColumnIndex(NotesDbAdapter.KEY_ROWID);
			this.mLayoutInflater = LayoutInflater.from(mContext);
		}

		//

		@Override
		public void bindView(View v, Context context, final Cursor c) {

			final int i = c.getPosition();
			mDbHelper = new NotesDbAdapter(v.getContext());
			mDbHelper.open();
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.title = (TextView) v.findViewById(R.id.title_text);
			viewHolder.date = (TextView) v.findViewById(R.id.date_text);
			if (c.moveToPosition(i)) {
				note2 = mDbHelper.fetchNote(Long.parseLong(c
						.getString(mIdIndex)));

				viewHolder.title.setText(note2.getString(note2
						.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
				viewHolder.date.setText(note2.getString(note2
						.getColumnIndexOrThrow(NotesDbAdapter.COL_DATE)));
				viewHolder.lock_button = (ImageButton) v
						.findViewById(R.id.button_lock);
				viewHolder.lock_button.setTag(Long.parseLong(c
						.getString(mIdIndex)));
				if (note2.getString(
						note2.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCK))
						.equals("1")) {
					viewHolder.lock_button
							.setImageResource(R.drawable.ic_unlock);
					viewHolder.title.setTextColor(Color.parseColor("#DA0D22"));
				} else {
					viewHolder.lock_button.setImageResource(R.drawable.ic_lock);
					viewHolder.title.setTextColor(Color.parseColor("#FFFFFF"));
				}
				viewHolder.clear_button = (ImageButton) v
						.findViewById(R.id.button_clear);
				viewHolder.clear_button.setTag(Long.parseLong(c
						.getString(mIdIndex)));
				viewHolder.clear_button
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								final long rowid = (Long) viewHolder.clear_button
										.getTag();
								note2 = mDbHelper.fetchNote(rowid);
								if (note2
										.getString(
												note2.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCK))
										.startsWith("0")) {
									new AlertDialog.Builder(v.getContext(),
											AlertDialog.THEME_HOLO_DARK)
											.setIcon(
													android.R.drawable.ic_dialog_alert)
											.setTitle(
													note2.getString(note2
															.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)))
											.setMessage(
													"Are you sure to clear this memo?")
											.setPositiveButton(
													"Yes",
													new DialogInterface.OnClickListener() {
														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															mDbHelper
																	.deleteNote(rowid);
															fillData();
														}

													})
											.setNegativeButton("No", null)
											.show();
								} else
									Toast.makeText(v.getContext(),
											"Memo is locked", Toast.LENGTH_LONG)
											.show();
							}

						});
				viewHolder.lock_button
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								final long rowid = (Long) viewHolder.clear_button
										.getTag();
								context_id = rowid;
								pinFlag2 = 2;
								openNote();
							}

						});
			}
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}
	}

	// final String row_id = c.getString(c
	// .getColumnIndex(NotesDbAdapter.KEY_ROWID));

	// ImageButton ib = (ImageButton) v.findViewById(R.id.button_lock);
	// ImageButton ib2 = (ImageButton)
	// v.findViewById(R.id.button_clear);
	// TextView title = (TextView) v.findViewById(R.id.title_text);

	// if (ib != null) {
	// ib.setTag(row_id);
	// }
	// if (ib2 != null) {
	// ib2.setTag(row_id);
	// }
	// if (title != null) {
	// title.setTag(row_id);
	// }
	// ib.setOnClickListener(this);
	// ib2.setOnClickListener(this);
}

//
// public View getView(int position, View convertView, ViewGroup parent)
// {
// mDbHelper = new NotesDbAdapter(parent.getContext());
// mDbHelper.open();
// Cursor cursor = (Cursor) getItem(position);
// if (mCursor.moveToPosition(position)) {
// ViewHolder viewHolder;
//
// if (convertView == null) {
// convertView = mLayoutInflater.inflate(mLayout, null);
// viewHolder = new ViewHolder();
// viewHolder.lock_button = (ImageButton) convertView
// .findViewById(R.id.button_lock);
// viewHolder.clear_button = (ImageButton) convertView
// .findViewById(R.id.button_clear);
// viewHolder.title = (TextView) convertView
// .findViewById(R.id.title_text);
// viewHolder.date = (TextView) convertView
// .findViewById(R.id.date_text);
// final String row_id = mCursor.getString(mIdIndex);
// viewHolder.lock_button.setTag(row_id);
// viewHolder.clear_button.setTag(row_id);
// viewHolder.title.setTag(row_id);
// // note = mDbHelper.fetchNote(Long.parseLong(row_id));
// // if (note.getString(
// // note.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCK))
// // .equals("1"))
// // viewHolder.lock_button
// // .setImageResource(R.drawable.ic_unlock);
// convertView.setTag(viewHolder);
// } else {
// viewHolder = (ViewHolder) convertView.getTag();
// }
// final String row_id = mCursor.getString(mIdIndex);
// note2 = mDbHelper.fetchNote(Long.parseLong(row_id));
// viewHolder.title.setText(note2.getString(note2
// .getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
// viewHolder.date.setText(note2.getString(note2
// .getColumnIndexOrThrow(NotesDbAdapter.COL_DATE)));
// viewHolder.lock_button.setTag(row_id);
// viewHolder.clear_button.setTag(row_id);
// if (note2.getString(
// note2.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCK))
// .equals("1"))
// viewHolder.lock_button
// .setImageResource(R.drawable.ic_unlock);
// viewHolder.lock_button
// .setOnClickListener(new OnClickListener() {
// @Override
// public void onClick(View v) {
// context_id = Long.parseLong(row_id);
// pinFlag2 = 2;
// openNote();
// }
//
// });
// viewHolder.clear_button
// .setOnClickListener(new OnClickListener() {
// @Override
// public void onClick(View v) {
//
// note2 = mDbHelper.fetchNote(Long
// .parseLong(row_id));
// if (note2
// .getString(
// note2.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCK))
// .startsWith("0")) {
// new AlertDialog.Builder(v.getContext(),
// AlertDialog.THEME_HOLO_DARK)
// .setIcon(
// android.R.drawable.ic_dialog_alert)
// .setTitle(
// note2.getString(note2
// .getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)))
// .setMessage(
// "Are you sure to clear this memo?")
// .setPositiveButton(
// "Yes",
// new DialogInterface.OnClickListener() {
// @Override
// public void onClick(
// DialogInterface dialog,
// int which) {
// mDbHelper
// .deleteNote(Long
// .parseLong(row_id));
// fillData();
// }
//
// })
// .setNegativeButton("No", null)
// .show();
// } else
// Toast.makeText(v.getContext(),
// "Memo is locked", Toast.LENGTH_LONG)
// .show();
// }
//
// });
// }
// return convertView;
//
// }


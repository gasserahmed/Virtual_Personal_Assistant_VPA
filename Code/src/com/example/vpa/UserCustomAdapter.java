//package com.example.vpa;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.database.Cursor;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.ListView;
//import android.widget.SimpleCursorAdapter;
//import android.widget.TextView;
//import android.widget.Toast;
//
//public class UserCustomAdapter extends SimpleCursorAdapter implements
//		OnClickListener {
//
//	private final Context mContext;
//	private final int mLayout;
//	private final Cursor mCursor;
//	private NotesDbAdapter mDbHelper;
//	private Cursor note;
//	private final int mIdIndex;
//	private final LayoutInflater mLayoutInflater;
//
//	private final class ViewHolder {
//
//		public ImageButton lock_button;
//		public ImageButton clear_button;
//		public TextView title;
//		public TextView date;
//	}
//
//	public UserCustomAdapter(Context context, int layout, Cursor c,
//			String[] from, int[] to) {
//		super(context, layout, c, from, to);
//		this.mContext = context;
//		this.mLayout = layout;
//		this.mCursor = c;
//		this.mIdIndex = mCursor.getColumnIndex(NotesDbAdapter.KEY_ROWID);
//		this.mLayoutInflater = LayoutInflater.from(mContext);
//	}
//
//	@Override
//	public View newView(Context context, Cursor cursor, ViewGroup parent) {
//
//		Cursor c = getCursor();
//
//		final LayoutInflater inflater = LayoutInflater.from(context);
//		View v = inflater.inflate(mLayout, parent, false);
//
//		String row_id = c.getString(c.getColumnIndex(NotesDbAdapter.KEY_ROWID));
//
//		ImageButton ib = (ImageButton) v.findViewById(R.id.button_lock);
//		ImageButton ib2 = (ImageButton) v.findViewById(R.id.button_clear);
//		TextView title = (TextView) v.findViewById(R.id.title_text);
//		if (ib != null) {
//			ib.setTag(row_id);
//		}
//		if (ib2 != null) {
//			ib.setTag(row_id);
//		}
//		if (title != null) {
//			title.setTag(row_id);
//		}
//		ib.setOnClickListener(this);
//		ib2.setOnClickListener(this);
//		return v;
//	}
//
//	@Override
//	public void bindView(View v, Context context, Cursor c) {
//
//		String row_id = c.getString(c.getColumnIndex(NotesDbAdapter.KEY_ROWID));
//
//		ImageButton ib = (ImageButton) v.findViewById(R.id.button_lock);
//		ImageButton ib2 = (ImageButton) v.findViewById(R.id.button_clear);
//		TextView title = (TextView) v.findViewById(R.id.title_text);
//
//		if (ib != null) {
//			ib.setTag(row_id);
//		}
//		if (ib2 != null) {
//			ib2.setTag(row_id);
//		}
//		if (title != null) {
//			title.setTag(row_id);
//		}
//		ib.setOnClickListener(this);
//		ib2.setOnClickListener(this);
//	}
//
//	@Override
//	public void onClick(View v) {
//
//	}
//
//	public View getView(int position, View convertView, ViewGroup parent) {
//		mDbHelper = new NotesDbAdapter(parent.getContext());
//		mDbHelper.open();
//		if (mCursor.moveToPosition(position)) {
//			ViewHolder viewHolder;
//
//			if (convertView == null) {
//				convertView = mLayoutInflater.inflate(mLayout, null);
//				viewHolder = new ViewHolder();
//				viewHolder.lock_button = (ImageButton) convertView
//						.findViewById(R.id.button_lock);
//				viewHolder.clear_button = (ImageButton) convertView
//						.findViewById(R.id.button_clear);
//				viewHolder.title = (TextView) convertView
//						.findViewById(R.id.title_text);
//				viewHolder.date = (TextView) convertView
//						.findViewById(R.id.date_text);
//				final String row_id = mCursor.getString(mIdIndex);
//				viewHolder.lock_button.setTag(row_id);
//				viewHolder.clear_button.setTag(row_id);
//				viewHolder.title.setTag(row_id);
//				convertView.setTag(viewHolder);
//			} else {
//				viewHolder = (ViewHolder) convertView.getTag();
//			}
//			final String row_id = mCursor.getString(mIdIndex);
//			note = mDbHelper.fetchNote(Long.parseLong(row_id));
//			viewHolder.title.setText(note.getString(note
//					.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
//			viewHolder.date.setText(note.getString(note
//					.getColumnIndexOrThrow(NotesDbAdapter.COL_DATE)));
//			viewHolder.lock_button.setTag(row_id);
//			viewHolder.clear_button.setTag(row_id);
//			viewHolder.lock_button.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//
//					note = mDbHelper.fetchNote(Long.parseLong(row_id));
//					Toast.makeText(
//							v.getContext(),
//							note.getString(note
//									.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCK)),
//							Toast.LENGTH_LONG).show();
//				}
//
//			});
//			viewHolder.clear_button.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//
//					note = mDbHelper.fetchNote(Long.parseLong(row_id));
//					if (note.getString(
//							note.getColumnIndexOrThrow(NotesDbAdapter.KEY_LOCK))
//							.startsWith("0")) {
//						new AlertDialog.Builder(v.getContext(),
//								AlertDialog.THEME_HOLO_DARK)
//								.setIcon(android.R.drawable.ic_dialog_alert)
//								.setTitle(
//										note.getString(note
//												.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)))
//								.setMessage("Are you sure to clear this memo?")
//								.setPositiveButton("Yes",
//										new DialogInterface.OnClickListener() {
//											@Override
//											public void onClick(
//													DialogInterface dialog,
//													int which) {
//												mDbHelper.deleteNote(Long
//														.parseLong(row_id));
////												fillData();
//											}
//
//										}).setNegativeButton("No", null).show();
//					} else
//						Toast.makeText(v.getContext(), "Memo is locked",
//								Toast.LENGTH_LONG).show();
//				}
//
//			});
//		}
//		return convertView;
//
//	}
//}
package com.android.minicounter;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PersonViewer extends Activity implements OnClickListener{
	RelativeLayout layout_add;
	Button button_add, button_add_cancel;
	TextView tv, title, net;
	LinearLayout layout_person_holder;
	Typeface quicksandLight, lato;
	EditText et_name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_person_viewer);
		ActionBar actionbar = getActionBar();
		actionbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom));
		actionbar.setTitle("miniCOUNTER");
		
		int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		TextView title = (TextView)findViewById(titleId);
		title.setTextColor(getResources().getColor(R.color.appBlue));
		lato = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
		title.setTypeface(lato);
		
		layout_add = (RelativeLayout)findViewById(R.id.layout_add);
		layout_add.setVisibility(View.GONE);
		
		button_add = (Button)findViewById(R.id.button_add);
		button_add.setOnClickListener(this);
		
		button_add_cancel = (Button)findViewById(R.id.button_add_cancel);
		button_add_cancel.setOnClickListener(this);
		
		quicksandLight = Typeface.createFromAsset(getAssets(), "Quicksand-Light.otf");
		
		refreshLayout();
		et_name = (EditText)findViewById(R.id.editText_add);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_person_viewer, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case R.id.action_add:
			layout_add = (RelativeLayout)findViewById(R.id.layout_add);
			int heightpx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96, getResources().getDisplayMetrics());
			layout_add.getLayoutParams().height = heightpx;
			layout_add.setVisibility(View.VISIBLE);
			et_name.requestFocus();
			InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
			imm.showSoftInput(et_name, InputMethodManager.SHOW_IMPLICIT);
			return true;
			
		case R.id.action_delete_all:
			AlertDialog.Builder alertDeleteAll = new AlertDialog.Builder(PersonViewer.this);
			alertDeleteAll.setTitle("Delete Everything");
			alertDeleteAll.setMessage("This'll delete everything. Are you sure?");
			alertDeleteAll.setCancelable(false);
			alertDeleteAll.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					DbCreate db = new DbCreate(getApplicationContext(), DbCreate.DB_NAME, null, DbCreate.VERSION);
					SQLiteDatabase sql = db.getWritableDatabase();
					sql.delete(DbCreate.TABLE_AMOUNT, null, null);
					sql.delete(DbCreate.TABLE_PERSON, null, null);
					refreshLayout();
				}
			});
			alertDeleteAll.setNegativeButton("No", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			});
			AlertDialog alertdialog = alertDeleteAll.create();
			alertdialog.show();
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		refreshLayout();
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		
		switch(v.getId()){
		case R.id.button_add:
			
			String name = et_name.getText().toString();
			if(!name.isEmpty())
				addPersonToDb(name);
			refreshLayout();
			
			layout_add = (RelativeLayout)findViewById(R.id.layout_add);
			layout_add.setVisibility(View.GONE);
			et_name.setText(null);
			break;
			
		case R.id.button_add_cancel:
			layout_add = (RelativeLayout)findViewById(R.id.layout_add);
			et_name = (EditText)findViewById(R.id.editText_add);
			et_name.setText(null);
			layout_add.setVisibility(View.GONE);
			InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(et_name.getWindowToken(), 0);
			break;
		}
	}
	
	private void addEntryToLayout(final String name, String amount){
		layout_person_holder = (LinearLayout)findViewById(R.id.linear_person_holder);
		final View view = getLayoutInflater().inflate(R.layout.layout_person_entry, null);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(PersonViewer.this, AmountViewer.class);
				i.putExtra("name", name);
				startActivity(i);
			}
		});
		TextView tv_name = (TextView)view.findViewById(R.id.textView_name);
		TextView tv_amount = (TextView)view.findViewById(R.id.textView_amount);
		final Button button_explore = (Button)view.findViewById(R.id.button_explore);
		button_explore.setBackgroundResource(R.drawable.explore_bg);
		tv_name.setText(name);
		tv_name.setTypeface(quicksandLight);
		tv_amount.setText(amount);
		tv_amount.setTypeface(quicksandLight);
		if((Integer.parseInt(amount)) > 0)
			tv_amount.setTextColor(getResources().getColor(R.color.emerald));
		else if((Integer.parseInt(amount)) < 0)
			tv_amount.setTextColor(getResources().getColor(R.color.alizarin));
		
		button_explore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PopupMenu popup = new PopupMenu(getApplicationContext(), button_explore);
				popup.getMenuInflater().inflate(R.menu.popup_explore, popup.getMenu());
				
				popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch(item.getItemId()){
						case R.id.explore_view:
							Intent i = new Intent(PersonViewer.this, AmountViewer.class);
							i.putExtra("name", name);
							startActivity(i);
							return true;
							
						case R.id.explore_delete:
							AlertDialog.Builder alertDeleteAll = new AlertDialog.Builder(PersonViewer.this);
							alertDeleteAll.setTitle("Delete");
							alertDeleteAll.setMessage("Do you want to delete this entry?");
							alertDeleteAll.setCancelable(false);
							alertDeleteAll.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									final String whereClause = DbCreate.AMOUNT_PARENT+" = ?";
									final String whereArgs[] = {name};
									DbCreate db = new DbCreate(getApplicationContext(), DbCreate.DB_NAME, null, DbCreate.VERSION);
									SQLiteDatabase sql = db.getWritableDatabase();
									sql.delete(DbCreate.TABLE_AMOUNT, whereClause, whereArgs);
									sql.delete(DbCreate.TABLE_PERSON, DbCreate.PERSON_NAME+" = ?", whereArgs);
									refreshLayout();
								}
							});
							alertDeleteAll.setNegativeButton("No", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.cancel();
								}
							});
							AlertDialog alertdialog = alertDeleteAll.create();
							alertdialog.show();
							
							break;
						}
						return true;
					}
				});
				popup.show();
			}
		});
		
		layout_person_holder.addView(view);
	}

	private void addPersonToDb(String name){
		DbCreate db = new DbCreate(getApplicationContext(), DbCreate.DB_NAME, null, DbCreate.VERSION);
		SQLiteDatabase sql = db.getWritableDatabase();
		ContentValues cv = new ContentValues();
		
		cv.put(DbCreate.PERSON_NAME, name);
		sql.insert(DbCreate.TABLE_PERSON, null, cv);
	}
	
	private void refreshLayout(){
		layout_person_holder = (LinearLayout)findViewById(R.id.linear_person_holder);
		if(((LinearLayout)layout_person_holder).getChildCount() > 0)
			layout_person_holder.removeAllViews();
		
		String selPerson = "SELECT * FROM "+DbCreate.TABLE_PERSON;
		DbCreate db = new DbCreate(getApplicationContext(), DbCreate.DB_NAME, null, DbCreate.VERSION);
		SQLiteDatabase sql = db.getReadableDatabase();
		
		Cursor cursor = sql.rawQuery(selPerson, null);
		
		if(cursor != null){
			if(cursor.moveToFirst()){
				do{
					String name = cursor.getString(1);
					int amount = 0;
					String selAmount = "SELECT * FROM "+DbCreate.TABLE_AMOUNT+" WHERE "+DbCreate.AMOUNT_PARENT+"='"+name+"'";
					try{
						Cursor cursor_amount = sql.rawQuery(selAmount, null);
						if(cursor_amount != null){
							if(cursor_amount.moveToFirst()){
								do{
									int temp = Integer.parseInt(cursor_amount.getString(1));
									amount+=temp;
								}while(cursor_amount.moveToNext());
							}
						}
					}catch(Exception e){
						Toast.makeText(getApplicationContext(), "Database error: "+e, Toast.LENGTH_LONG).show();
					}
					addEntryToLayout(name, Integer.toString(amount));
				}while(cursor.moveToNext());
			}
		}
		netSet();
	}
	
	private void netSet(){
		DbCreate db = new DbCreate(getApplicationContext(), DbCreate.DB_NAME, null, DbCreate.VERSION);
		SQLiteDatabase sql = db.getReadableDatabase();
		String selAmount = "SELECT * FROM "+DbCreate.TABLE_AMOUNT;
		try{
			int amount = 0;
			Cursor cursor_amount = sql.rawQuery(selAmount, null);
			if(cursor_amount != null){
				if(cursor_amount.moveToFirst()){
					do{
						int temp = Integer.parseInt(cursor_amount.getString(1));
						amount+=temp;
					}while(cursor_amount.moveToNext());
				}
			}
			net = (TextView)findViewById(R.id.textView_net_amount);
			net.setText(Integer.toString(amount));
			net.setTypeface(quicksandLight);
		}catch(Exception e){
			Toast.makeText(getApplicationContext(), "Database error: "+e, Toast.LENGTH_LONG).show();
		}
	}
}
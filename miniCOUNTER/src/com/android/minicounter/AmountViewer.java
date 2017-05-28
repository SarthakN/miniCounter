package com.android.minicounter;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AmountViewer extends Activity implements OnClickListener{
	RelativeLayout relative_add_amount;
	Button button_collapse, button_add, button_minus;
	EditText et_amount, et_details;
	String name;
	LinearLayout layout_amount_holder;
	Typeface quicksandLight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_amount_viewer);
		
		Bundle extras = getIntent().getExtras();
		name = extras.getString("name");
		Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
		int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
		TextView title = (TextView)findViewById(titleId);
		title.setTextColor(Color.BLACK);
		
		ActionBar actionbar = getActionBar();
		actionbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_bottom));
		actionbar.setTitle(name);
		actionbar.setDisplayShowTitleEnabled(true);
		actionbar.setDisplayHomeAsUpEnabled(true);
		
		relative_add_amount = (RelativeLayout)findViewById(R.id.relative_add_amount);
		relative_add_amount.getLayoutParams().height = 0;
		relative_add_amount.setVisibility(View.GONE);
		
		et_amount = (EditText)findViewById(R.id.editText_amount);
		et_details = (EditText)findViewById(R.id.editText_details);
		button_add = (Button)findViewById(R.id.amount_add);
		button_minus = (Button)findViewById(R.id.amount_minus);
		button_collapse = (Button)findViewById(R.id.button_collapse);
		
		button_add.setOnClickListener(this);
		button_minus.setOnClickListener(this);
		button_collapse.setOnClickListener(this);
		
		quicksandLight = Typeface.createFromAsset(getAssets(), "Quicksand-Light.otf");
		refreshLayout();
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
			relative_add_amount = (RelativeLayout)findViewById(R.id.relative_add_amount);
			int heightpx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
			relative_add_amount.getLayoutParams().height = heightpx;
			relative_add_amount.setVisibility(View.VISIBLE);
			break;
		
		case R.id.action_delete_all:
			AlertDialog.Builder alertDeleteAll = new AlertDialog.Builder(this);
			alertDeleteAll.setTitle("Delete All");
			alertDeleteAll.setMessage("Do you want to delete all entries?");
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
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		String amount, details;
		switch(v.getId()){
		case R.id.button_collapse:
			et_amount.setText(null);
			et_details.setText(null);
			relative_add_amount = (RelativeLayout)findViewById(R.id.relative_add_amount);
			relative_add_amount.getLayoutParams().height = 0;
			relative_add_amount.setVisibility(View.GONE);
			break;
			
		case R.id.amount_add:
			amount = et_amount.getText().toString();
			details = et_details.getText().toString();
			if(amount.isEmpty())
				Toast.makeText(getApplicationContext(), "Please enter some AMOUNT", Toast.LENGTH_LONG).show();
			else if(amount.length() < 10){
				addAmountToDb(amount, name, details);
				refreshLayout();
			}
			else{
				Toast.makeText(getApplicationContext(), "I think you are dreaming", Toast.LENGTH_LONG).show();
			}
			et_amount.setText(null);
			et_details.setText(null);
			break;
			
		case R.id.amount_minus:
			amount = et_amount.getText().toString();
			details = et_details.getText().toString();
			if(amount.isEmpty())
				Toast.makeText(getApplicationContext(), "Please enter some AMOUNT", Toast.LENGTH_LONG).show();
			else{
				addAmountToDb("-"+amount, name, details);
				refreshLayout();
			}
			et_amount.setText(null);
			et_details.setText(null);
			break;
		}
	}
	
	private void addEntryToLayout(String amount, String details){
		layout_amount_holder = (LinearLayout)findViewById(R.id.linear_amount_holder);
		final View view = getLayoutInflater().inflate(R.layout.layout_amount_entry, null);
		TextView tv_amount = (TextView)view.findViewById(R.id.textView_amount);
		TextView tv_details = (TextView)view.findViewById(R.id.textView_details);
		Button button_amount_delete = (Button)view.findViewById(R.id.button_amount_delete);
		
		tv_amount.setText(amount);
		tv_amount.setTypeface(quicksandLight);
		tv_details.setText(details);
		tv_details.setTypeface(quicksandLight);
		
		if((Integer.parseInt(amount)) > 0)
			tv_amount.setTextColor(getResources().getColor(R.color.emerald));
		else if((Integer.parseInt(amount)) < 0)
			tv_amount.setTextColor(getResources().getColor(R.color.alizarin));
		
		final String whereClause = DbCreate.AMOUNT_PARENT+" = ? AND "+DbCreate.AMOUNT+" =?";
		final String whereArgs[] = {name, amount};
		button_amount_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertDeleteAll = new AlertDialog.Builder(AmountViewer.this);
				alertDeleteAll.setTitle("Delete");
				alertDeleteAll.setMessage("Do you want to delete this entry?");
				alertDeleteAll.setCancelable(false);
				alertDeleteAll.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						DbCreate db = new DbCreate(getApplicationContext(), DbCreate.DB_NAME, null, DbCreate.VERSION);
						SQLiteDatabase sql = db.getWritableDatabase();
						sql.delete(DbCreate.TABLE_AMOUNT, whereClause, whereArgs);
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
			}
		});
		layout_amount_holder.addView(view);
	}
	
	private void addAmountToDb(String amount, String parent, String details){
		DbCreate db = new DbCreate(getApplicationContext(), DbCreate.DB_NAME, null, DbCreate.VERSION);
		SQLiteDatabase sql = db.getWritableDatabase();
		ContentValues cv = new ContentValues();
		
		cv.put(DbCreate.AMOUNT, amount);
		cv.put(DbCreate.AMOUNT_PARENT, parent);
		cv.put(DbCreate.DETAILS, details);
		
		sql.insert(DbCreate.TABLE_AMOUNT, null, cv);
	}
	
	private void refreshLayout(){
		layout_amount_holder = (LinearLayout)findViewById(R.id.linear_amount_holder);
		if(((LinearLayout)layout_amount_holder).getChildCount() > 0)
			layout_amount_holder.removeAllViews();
		
		String selAmount = "SELECT * FROM "+DbCreate.TABLE_AMOUNT+" WHERE "+DbCreate.AMOUNT_PARENT+"="+"'"+name+"'";
		DbCreate db = new DbCreate(getApplicationContext(), DbCreate.DB_NAME, null, DbCreate.VERSION);
		SQLiteDatabase sql = db.getReadableDatabase();
		try{
			Cursor cursor = sql.rawQuery(selAmount, null);
			if(cursor.moveToNext()){
				do{
					String amount = cursor.getString(1);
					String details = cursor.getString(3);
					
					addEntryToLayout(amount, details);
				}while(cursor.moveToNext());
			}
		}catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Database Error: "+e, Toast.LENGTH_SHORT).show();
		}
	}
}
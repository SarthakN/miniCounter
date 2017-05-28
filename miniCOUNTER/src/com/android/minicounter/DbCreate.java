package com.android.minicounter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbCreate extends SQLiteOpenHelper{

	public static final int VERSION = 1;
	public static final String DB_NAME = "db";
	
	public static final String TABLE_PERSON = "person";
	public static final String PERSON_ID = "userid";
	public static final String PERSON_NAME = "name";
	
	public static final String TABLE_AMOUNT = "amount";
	public static final String AMOUNT_ID = "amountid";
	public static final String AMOUNT = "amount";
	public static final String AMOUNT_PARENT = "parent";
	public static final String DETAILS = "details";
	
	public DbCreate(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String createUser = "CREATE TABLE "+TABLE_PERSON+"("+PERSON_ID+" INTEGER PRIMARY KEY,"+PERSON_NAME+" TEXT)";
		String createAmount = "CREATE TABLE "+TABLE_AMOUNT+"("+AMOUNT_ID+" INTEGER PRIMARY KEY,"+AMOUNT+" TEXT,"+AMOUNT_PARENT+" TEXT,"+DETAILS+" TEXT)";
		
		db.execSQL(createUser);
		db.execSQL(createAmount);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
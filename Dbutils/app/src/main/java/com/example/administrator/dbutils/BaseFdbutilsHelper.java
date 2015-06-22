package com.example.administrator.dbutils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseFdbutilsHelper extends SQLiteOpenHelper {

	public String[] fleidname;
	public String tablename;
	public String[] columnnames;
	
	
	
	
	private static int getversion() {
		// TODO Auto-generated method stub
		return 0;
	}
	public BaseFdbutilsHelper(Context context,  String[] fleid,
			String tablename) {
		super(context, "mydb.db", null, 1);
		this.fleidname = fleid;
		this.tablename = tablename;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		StringBuffer sb = new StringBuffer();
		sb.append("create table ");
		sb.append(tablename);
		sb.append(" (_id integer primary key autoincrement");
		for (int i = 0; i < fleidname.length; i++) {
			sb.append("," + fleidname[i] + " varchar(30)");
		}
		sb.append(")");
		System.out.println(sb.toString());
		db.execSQL(sb.toString());
	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		
	}
	
	
}

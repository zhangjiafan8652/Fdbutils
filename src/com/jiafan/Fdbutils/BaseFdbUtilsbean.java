package com.jiafan.Fdbutils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class BaseFdbUtilsbean {

	private Context context;

	private Field[] fields;
	private String[] fieldnames;
	private String tablename;

	private BaseFdbutilsHelper basebeanutils;

	public BaseFdbUtilsbean(Context context) {
		this.context = context;
		setField();
		setTable();
		basebeanutils = new BaseFdbutilsHelper(context, fieldnames, tablename);
		
		SQLiteDatabase db = basebeanutils.getWritableDatabase();
		
		ArrayList<String> arrayList = new ArrayList<String>();
		
		for (int i = 0; i < fieldnames.length; i++) {
			if(!checkColumnExist1(db, tablename, fieldnames[i])){
				arrayList.add(fieldnames[i]);
				System.out.println("这是不存在的字段:"+fieldnames[i]);
			}
		}
		
		
		if(arrayList.size()!=0){
			String[] columnnames=new String[arrayList.size()];
			for (int i = 0; i < arrayList.size(); i++) {
				columnnames[i]=arrayList.get(i);
				//alter table mybean add sex1 varchar(20) ;
				StringBuffer sb=new StringBuffer();
				sb.append("alter table "+tablename+" add "+columnnames[i]+" varchar(20)");
				String sql=sb.toString();
				db.execSQL(sql);
				
			}
			
		}
		
	}

	/**
	 * 存储数据库
	 * 
	 * @return
	 */
	public boolean save() {

		SQLiteDatabase db = basebeanutils.getWritableDatabase();
		ContentValues values = new ContentValues();
		try {
			for (int i = 0; i < fields.length; i++) {
				// System.out.println(field[i].getName()+field[i].get(this));
				values.put(fields[i].getName(), fields[i].get(this) + "");
			}

		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
		long id = db.insert(tablename, null, values);

		// db.close();
		if (id != -1) {

			return true;
		} else {
			return false;
		}

	}

	/**
	 * 
	 * @param beanvalues
	 *            需要查找的条件集合
	 * 
	 * @param context
	 *            上下环境
	 * @return
	 */
	public <E> List<E> query(FdbutilesValues beanvalues) {
		setTable();
		HashMap<String, String> hashMap = beanvalues.getWhereequalto();

		Iterator<String> iterator = hashMap.keySet().iterator();
		StringBuffer sb = new StringBuffer();
		ArrayList<String> valuesarrayList = new ArrayList<String>();
		ArrayList<String> keyarrayList = new ArrayList<String>();
		int k = 0;
		sb.append("select * from " + tablename + " where ");
		while (iterator.hasNext()) {
			String key = iterator.next();
			String values = hashMap.get(key);
			/*
			 * if (k == 0) { sb.append(key + " like ? "); } else {
			 * sb.append("and " + key + " like ? "); }
			 */
			if (k == 0) {
				sb.append(key + "=? ");
			} else {
				sb.append("and " + key + " =? ");
			}

			keyarrayList.add(key);
			valuesarrayList.add(values);
			k = k + 1;

		}

		String[] values2 = new String[valuesarrayList.size()];
		for (int i = 0; i < values2.length; i++) {
			values2[i] = valuesarrayList.get(i);

		}
		// String selection = sb.toString();

		SQLiteDatabase db = basebeanutils.getWritableDatabase();

		String sql = sb.toString();
		System.out.println(sql);
		/*
		 * System.out.println(tablename+selection+values2[0]); Cursor cursor =
		 * db.query(tablename, null, selection, values2, null, null, null,
		 * null);
		 */

		Cursor cursor = db.rawQuery(sql, values2);

		if (cursor.getCount() == 0) {
			cursor.close();
			// db.close();
			return null;
		}
		List<E> infos = new ArrayList<E>();
		while (cursor.moveToNext()) {
			try {
				System.out.println(this.getClass().getConstructors().length
						+ "你懂的构造方法");
				// 获取构造方法创建对象
				E newInstance = (E) this.getClass().getConstructors()[0]
						.newInstance(context);

				Method[] methods = this.getClass().getMethods();

				for (int i = 0; i < methods.length; i++) {
					for (int j = 0; j < fields.length; j++) {
						String methodname = methods[i].getName().toLowerCase();
						String keyname = "set" + fieldnames[j].toLowerCase();
						if (methodname.equals(keyname)) {
							// System.out.println(values2[j]);
							int columnIndex = cursor
									.getColumnIndex(fieldnames[j]);
							String values = cursor.getString(columnIndex);
							methods[i].invoke(newInstance, values);
						}
					}
				}
				infos.add(newInstance);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				// cursor.close();
				// db.close();
			}
		}
		cursor.close();
		// db.close();
		return infos;
		/*
		 * Cursor cursor = db.query("Orderbean_db", null, "driver_id like ?",
		 * new String[] { driver_id }, null, null, null, "0,10");
		 */
	}

	/**
	 * 传入参数 条件参数 删除对应的条目
	 * 
	 * @param beanvalues
	 * @return
	 */
	public boolean deleData(FdbutilesValues beanvalues) {
		setTable();
		// update foods set name='CHOCOLATE BOBKA' where name='Chocolate Bobka';
		HashMap<String, String> hashMap = beanvalues.getWhereequalto();
		Iterator<String> iterator = hashMap.keySet().iterator();
		StringBuffer sb = new StringBuffer();
		ArrayList<String> valuesarrayList = new ArrayList<String>();
		ArrayList<String> keyarrayList = new ArrayList<>();
		int k = 0;
		// select * from mybean where name='张三' and age='5';
		sb.append(" delete from " + tablename + " where ");
		while (iterator.hasNext()) {
			String key = iterator.next();
			String values = hashMap.get(key);

			if (k == 0) {
				sb.append(key + "='" + values + "' ");
			} else {
				sb.append("and " + key + " ='" + values + "' ");
			}
			keyarrayList.add(key);
			valuesarrayList.add(values);
			k = k + 1;
		}
		String sql = sb.toString();
		System.out.println(sql);
		SQLiteDatabase db = basebeanutils.getWritableDatabase();
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 传入参数 条件参数 删除对应的条目
	 * 
	 * @param beanvalues
	 * @return
	 */
	public boolean updateData(FdbutilesValues beanvalues) {
		setTable();
		StringBuffer sb = new StringBuffer();
		// update foods set name='CHOCOLATE BOBKA' where name='Chocolate Bobka';
		// HashMap<String, String> equalmap = beanvalues.getWhereequalto();
		HashMap<String, String> setmap = beanvalues.getSetting();
		Iterator<String> iterator = setmap.keySet().iterator();
		int k = 0;
		// select * from mybean where name='张三' and age='5';
		// update foods set name='CHOCOLATE BOBKA' where name='Chocolate Bobka';
		sb.append(" update " + tablename + " set ");
		while (iterator.hasNext()) {
			String key = iterator.next();
			String values = setmap.get(key);
			if (k == 0) {
				sb.append(key + "='" + values + "' ");
			} else {
				sb.append("and " + key + " ='" + values + "' ");
			}
			k = k + 1;
		}
		// HashMap<String, String> setmap = beanvalues.getSetting();
		HashMap<String, String> equalmap = beanvalues.getWhereequalto();
		Iterator<String> iterator1 = equalmap.keySet().iterator();
		int y = 0;
		// select * from mybean where name='张三' and age='5';
		// update foods set name='CHOCOLATE BOBKA' where name='Chocolate Bobka';
		sb.append(" where ");
		while (iterator1.hasNext()) {
			String key = iterator1.next();
			String values = equalmap.get(key);
			if (y == 0) {
				sb.append(key + "='" + values + "' ");
			} else {
				sb.append("and " + key + " ='" + values + "' ");
			}
			// keyarrayList.add(key);
			// valuesarrayList.add(values);
			y = y + 1;
		}

		String sql = sb.toString();
		System.out.println(sql);
		SQLiteDatabase db = basebeanutils.getWritableDatabase();
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void setTable() {
		tablename = this.getClass().getName();
		int lastIndexOf = tablename.lastIndexOf(".");
		tablename = tablename.substring(lastIndexOf + 1);
	}

	private void setField() {
		fields = this.getClass().getFields();
		fieldnames = new String[fields.length];
		for (int i = 0; i < fields.length; i++) {
			fieldnames[i] = fields[i].getName();
		}
	}

	/**
	 * 
	 * 
	 * 
	 * @param context
	 *            上下环境
	 * @return
	 */
	public <E> List<E> query() {
		setTable();
		StringBuffer sb = new StringBuffer();

		sb.append("select * from " + tablename);
		// String selection = sb.toString();

		SQLiteDatabase db = basebeanutils.getWritableDatabase();
		
		String sql = sb.toString();
		System.out.println(sql);
		/*
		 * System.out.println(tablename+selection+values2[0]); Cursor cursor =
		 * db.query(tablename, null, selection, values2, null, null, null,
		 * null);
		 */

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.getCount() == 0) {
			cursor.close();
			// db.close();
			return null;
		}
		List<E> infos = new ArrayList<E>();
		while (cursor.moveToNext()) {
			try {
				System.out.println(this.getClass().getConstructors().length
						+ "你懂的构造方法");
				// 获取构造方法创建对象
				E newInstance = (E) this.getClass().getConstructors()[0]
						.newInstance(context);

				Method[] methods = this.getClass().getMethods();

				for (int i = 0; i < methods.length; i++) {
					for (int j = 0; j < fields.length; j++) {
						String methodname = methods[i].getName().toLowerCase();
						String keyname = "set" + fieldnames[j].toLowerCase();
						if (methodname.equals(keyname)) {
							// System.out.println(values2[j]);
							int columnIndex = cursor
									.getColumnIndex(fieldnames[j]);
							String values = cursor.getString(columnIndex);
							methods[i].invoke(newInstance, values);
						}
					}
				}

				infos.add(newInstance);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				// cursor.close();
				// db.close();
			}
		}
		cursor.close();
		// db.close();
		return infos;
		/*
		 * Cursor cursor = db.query("Orderbean_db", null, "driver_id like ?",
		 * new String[] { driver_id }, null, null, null, "0,10");
		 */
	}
	
	
	/**
	* 方法1：检查某表列是否存在    
	* @param db
	* @param tableName 表名
	* @param columnName 列名
	* @return
	*/
	private boolean checkColumnExist1(SQLiteDatabase db, String tableName
	        , String columnName) {
	    boolean result = false ;
	    Cursor cursor = null ;
	    try{
	        //查询一行
	        cursor = db.rawQuery( "SELECT * FROM " + tableName + " LIMIT 0"
	            , null );
	        result = cursor != null && cursor.getColumnIndex(columnName) != -1 ;
	    }catch (Exception e){
	        // Log.e(TAG,"checkColumnExists1..." + e.getMessage()) ;
	    }finally{
	        if(null != cursor && !cursor.isClosed()){
	            cursor.close() ;
	        }
	    }
	  
	    return result ;
	}

}

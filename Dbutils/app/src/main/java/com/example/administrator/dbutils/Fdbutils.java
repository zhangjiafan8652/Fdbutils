package com.example.administrator.dbutils;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/6/22.
 */
public class Fdbutils {

    public static Fdbutils mFdbutils;

    private void Fdbutils(){}

    public static FdbUtilsHelper fdbUtilsHelper;
    public static SQLiteDatabase db;

    public static  Fdbutils getmFdbutilsInstance(Context context){


        if (mFdbutils!=null){
            return mFdbutils;
        }else {
            fdbUtilsHelper= new FdbUtilsHelper(context);
            mFdbutils= new Fdbutils();
        }
        return  mFdbutils;

    }

    /**
     * 将对象存入数据库
     * @param mBean
     * @return
     */
    public boolean saveBean(FbaseBean mBean){
        setTable(mBean);
        //判断此表是否存在
        if (isExist(mBean)){

            //判断列是否有增加
            addExistclumd(mBean);

            //存入数据库
            return save(mBean);


        }else {

            //创建此表
            createTable(mBean);
            //保存此表
            saveBeantable(mBean);

            return save(mBean);

        }


    }

    /**
     * 将创建的table存入基类table
     * @param mBean
     */
    private boolean saveBeantable(FbaseBean mBean) {

        db = fdbUtilsHelper.getWritableDatabase();


        ContentValues values = new ContentValues();

        values.put("tablename", tablename + "");
        long id = db.insert(FdbUtilsHelper.ALLTABLE, null, values);

        db.close();
        if (id != -1) {

            return true;
        } else {
            return false;
        }

    }


    /**
     * 将bean存入数据库
     * @param mBean
     */
    private boolean save(FbaseBean mBean){

        db = fdbUtilsHelper.getWritableDatabase();
        Field[] fields = mBean.getClass().getFields();

        ContentValues values = new ContentValues();
        try {
            for (int i = 0; i < fields.length; i++) {
                // System.out.println(field[i].getName()+field[i].get(this));
                int lastIndexOf = fields[i].getType().getName().lastIndexOf(".");
                String objname = fields[i].getType().getName().substring(lastIndexOf + 1);
                Log.d("objname", objname);
                if (objname.equals("String")){
                    Log.d(fields[i].getName(), fields[i].get(mBean)+"");

                    values.put(fields[i].getName(), fields[i].get(mBean) + "");
                }else {
                    //利用base64算法.将不是String的对象转成String存到数据库中
                    String s = ObjectToString(fields[i].get(mBean));
                    values.put(fields[i].getName(), s);

                }

            }

        } catch (Exception e) {
            // TODO: handle exception
            throw new RuntimeException(e);
        }
        long id = db.insert(tablename, null, values);

        db.close();
        if (id != -1) {

            return true;
        } else {
            return false;
        }

    }

    /**
     * 添加不存在的列
     */
    private void addExistclumd(FbaseBean mBean) {

        ArrayList<String> arrayList = new ArrayList<String>();
        String[] fieldnames = getBeanfield(mBean);
        db = fdbUtilsHelper.getWritableDatabase();
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
        db.close();

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


    /**
     * 创建一个数据表
     * @param mBean
     */
    private void createTable(FbaseBean mBean) {

        String[] beanfield = getBeanfield(mBean);
        StringBuffer sb = new StringBuffer();
        sb.append("create table ");
        sb.append(tablename);
        sb.append(" (_id integer primary key autoincrement");
        for (int i = 0; i < beanfield.length; i++) {
            sb.append("," + beanfield[i] + " varchar(30)");
        }
        sb.append(")");
        db = fdbUtilsHelper.getWritableDatabase();
        // System.out.println(sb.toString());
        db.execSQL(sb.toString());
        db.close();


    }

    /**
     * 获取bean的参数
     */
    private String[] getBeanfield(FbaseBean mbean){
        Field[] fields = mbean.getClass().getFields();
        String[] fieldnames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldnames[i] = fields[i].getName();
        }
        return  fieldnames;
    }



    /**
     * 判断某javabean是否存在
     * @param mBean
     * @return
     */
    private boolean isExist(FbaseBean mBean){
        StringBuffer sb = new StringBuffer();
        sb.append("select * from " + FdbUtilsHelper.ALLTABLE + " where ");
        sb.append("tablename" + "=? ");
        String[] values2 = new String[1];
        for (int i = 0; i < values2.length; i++) {
            values2[i] = tablename;
        }
        String sql = sb.toString();
        db = fdbUtilsHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, values2);

        if (cursor.getCount() == 0) {
            cursor.close();
            db.close();
            return false;
        }
        return true;
    }

    String tablename;
    private void setTable(FbaseBean mBean) {
        tablename = mBean.getClass().getName();
        int lastIndexOf = tablename.lastIndexOf(".");
        tablename = tablename.substring(lastIndexOf + 1);
    }

    /**
     * 将对象转换成字符串
     * @param object
     * @param <T>
     * @return
     */
    public <T> String ObjectToString(T object){

        ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        // 将Product对象放到OutputStream中
        // 将Product对象转换成byte数组，并将其进行base64编码
        String newWord = new String(myBase64.encode(baos.toByteArray()));
        return newWord;

    }

    /**
     * 将String 转成对象
     * @param k
     * @param <T>
     * @return
     */
    public <T> T StringToObject(String k){
        try {
            // 对Base64格式的字符串进行解码
            byte[] base64Bytes = myBase64.decode(k);
            ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            // 从ObjectInputStream中读取Product对象
            T addWord = (T) ois.readObject();
            return addWord;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

}
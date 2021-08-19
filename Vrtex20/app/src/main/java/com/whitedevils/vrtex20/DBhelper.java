package com.whitedevils.vrtex20;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBhelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "Bills";

    Context context;

    public DBhelper(@Nullable Context context) {
        super(context, "Data.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table login(id integer primary key autoincrement, name text, mail text, mobile integer,password text)");
        db.execSQL(" Create table Bills(id integer primary key autoincrement,Date date,invoiceno text , total integer) ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists login");
        db.execSQL("drop table if exists Bills");

        onCreate(db);

    }

    public Boolean insert(String name,String mail,String mobile,String password){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);
        contentValues.put("mail",mail);
        contentValues.put("mobile",mobile);
        contentValues.put("password",password);
        long ins = db.insert("login",null,contentValues);
        if (ins == -1 ) return false;
        else return true;
    }

    Cursor readAlldata(){
        String query = " Select * from " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query,null);
        }
        return cursor;
    }

    public Boolean listinsert(String Date,String invoiceno,String total){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Date",Date);
        contentValues.put("invoiceno",invoiceno);
        contentValues.put("total",total);
        long ins = db.insert("Bills",null,contentValues);
        if (ins == -1) return false;
        else return true;
    }

    public Boolean chkdetails(String invoiceno){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from Bills where invoiceno=?",new String[]{invoiceno});
        return cursor.getCount() <= 0;
    }

    public  Boolean chkmail(String mail){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from login where mail=?",new String[]{mail});
        if (cursor.getCount()>0)return false;
        else return true;
    }

    public Boolean mailpassword(String mail, String password){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from login where mail=? and password=?", new String[]{mail,password});
        if (cursor.getCount()>0) return false;
        else return true;
    }

    public void deleteTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    public void deleteSingleRow(String inv_no){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete( TABLE_NAME, "invoiceno=?", new String[]{inv_no});
        if (result != -1){
            Toast.makeText(context, "successfully deleted!...", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Failed to delete!...", Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor search(String Date){

        // Select All Query
        //String query = "SELECT  * FROM Bills WHERE Date=? ";
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor cursor = db.rawQuery("SELECT  * FROM Bills WHERE Date=? ", new String[]{Date});
        //String selectQuery = "SELECT  * FROM Bills WHERE Date BETWEEN fromDate AND toDate";
        Cursor cursor = null;
        // looping through all rows and adding to list
        if (db != null){
            cursor = db.rawQuery("SELECT  * FROM Bills WHERE Date=? ", new String[]{Date});
        }

        return cursor;
        // return contact list
        //return cursor;

//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = "SELECT * from Bills where Date " + "like'%" + Date + "%'";
//        Cursor cursor = db.rawQuery(query,null);
//
//        if (cursor.getCount() == 0){
//            Toast.makeText(context, "ON RECORD FOUND!...", Toast.LENGTH_SHORT).show();
//        }
//
//        StringBuffer buffer = new StringBuffer();
//        while(cursor.moveToNext()){
//            buffer.append("Date \t" + cursor.getString(1) + "\n");
//            //buffer.append("invoiceno \t" + cursor.getString(2) + "\n");
//            //buffer.append("total \t" + cursor.getString(3) + "\n");
//        }
//        Log.d("datas", "search: " + buffer.toString());
//
//
//        return cursor;

    }
}
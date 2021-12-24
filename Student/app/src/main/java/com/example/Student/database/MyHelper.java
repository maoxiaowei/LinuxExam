package com.example.Student.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.Student.bean.StudentBean;
import com.example.Student.utils.DBUtils;

import java.util.ArrayList;
import java.util.List;


public class MyHelper extends SQLiteOpenHelper {

    private SQLiteDatabase sqLiteDatabase;

    //创建数据库
    public MyHelper(@Nullable Context context) {
        super(context, DBUtils.DATABASE_NAME, null, DBUtils.DATABASE_VERION);
        sqLiteDatabase = this.getWritableDatabase();
    }

    //创建表
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DBUtils.DATABASE_TABLE + "("+DBUtils.BOOK_ID+" integer primary key autoincrement,"+ DBUtils.BOOK_NAME + " VARCHAR(200)," + DBUtils.BOOK_AUTHOR + " VARCHAR(200) )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //添加数据
    public boolean insertData(String name, String author,SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBUtils.BOOK_NAME, name);
        contentValues.put(DBUtils.BOOK_AUTHOR, author);
        return db.insert(DBUtils.DATABASE_TABLE, null, contentValues) > 0;
    }

    //删除数据
    public boolean deleteData(String id,SQLiteDatabase db) {
        String sql = DBUtils.BOOK_ID + "=?";
        String[] contentValuesArray = new String[]{String.valueOf(id)};
        return db.delete(DBUtils.DATABASE_TABLE, sql, contentValuesArray) > 0;
    }

    //修改数据
    public boolean updateData(String id, String name, String author,SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBUtils.BOOK_NAME, name);
        contentValues.put(DBUtils.BOOK_AUTHOR, author);
        String sql = DBUtils.BOOK_ID + "=?";
        String[] strings = new String[]{id};
        return db.update(DBUtils.DATABASE_TABLE, contentValues, sql, strings) > 0;
    }



    //查询数据
    public List<StudentBean> query(SQLiteDatabase db) {
        List<StudentBean> list = new ArrayList<StudentBean>();
        Cursor cursor = db.query(DBUtils.DATABASE_TABLE, null, null, null, null, null, DBUtils.BOOK_ID + " desc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                StudentBean bookInfo = new StudentBean();
                String id = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(DBUtils.BOOK_ID)));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(DBUtils.BOOK_NAME));
                String author = cursor.getString(cursor.getColumnIndexOrThrow(DBUtils.BOOK_AUTHOR));
                bookInfo.setId(id);
                bookInfo.setName(name);
                bookInfo.setAge(author);
                list.add(bookInfo);
//                getHttp();
            }
            cursor.close();
        }
        return list;
    }
}

package com.example.aderz.vladapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ToDoDataBase  extends SQLiteOpenHelper {

    public ToDoDataBase(Context context){
        super(context, "myDB", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("myLogs", "--- onCreate database ---");
        db.execSQL("create table List ("
                + "id integer primary key autoincrement,"
                + "ident integer,"
                + "upr0 text," + "upr1 text," + "upr2 text," + "upr3 text," + "upr4 text,"+
                "upr5 text," + "upr6 text," + "upr7 text," + "upr8 text," + "upr9 text,"+
                "upr10 text," + "upr11 text," + "upr12 text," + "upr13 text," + "upr14 text,"+
                "upr15 text," + "upr16 text," + "upr17 text," + "upr18 text," + "upr19 text,"+
                "upr20 text," + "upr21 text," + "upr22 text," + "upr23 text," + "upr24 text,"+
                "upr25 text," + "upr26 text," + "upr27 text," + "upr28 text," + "upr29 text"+");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d("myLogs", "--- onCreate database ---");

        db.execSQL("create table if not exists List ("
                + "ident integer,"
                + "upr0 text," + "upr1 text," + "upr2 text," + "upr3 text," + "upr4 text,"+
                "upr5 text," + "upr6 text," + "upr7 text," + "upr8 text," + "upr9 text,"+
                "upr10 text," + "upr11 text," + "upr12 text," + "upr13 text," + "upr14 text,"+
                "upr15 text," + "upr16 text," + "upr17 text," + "upr18 text," + "upr19 text,"+
                "upr20 text," + "upr21 text," + "upr22 text," + "upr23 text," + "upr24 text,"+
                "upr25 text," + "upr26 text," + "upr27 text," + "upr28 text," + "upr29 text"+");");
    }
}

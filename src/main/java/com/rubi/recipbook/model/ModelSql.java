package com.rubi.recipbook.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ModelSql extends SQLiteOpenHelper {

    ModelSql(Context context) {
        super(context, "database.db", null, 14);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        RecipeSql.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        RecipeSql.onUpgrade(db, oldVersion, newVersion);
    }

}

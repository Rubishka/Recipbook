package com.rubi.recipbook.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class ModelSql extends SQLiteOpenHelper {

    ModelSql(Context context) {
        super(context, "database.db", null, 10);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        RecipeSql.onCreate(db);
        //db.delete("recipe",null,null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        RecipeSql.onUpgrade(db, oldVersion, newVersion);
    }

}

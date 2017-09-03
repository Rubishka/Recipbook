package com.rubi.recipbook.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;


public class RecipeSql {
    static final String RECIPE_TABLE = "recipe";
    static final String RECIPE_ID = "rcpid";
    static final String RECIPE_NAME = "recipeName";
    static final String RECIPE_DESCRIPTION = "recipeDescription";
    static final String RECIPE_INGREDIENT = "ingredient";
    static final String RECIPE_BY = "recipeBy";
    static final String RECIPE_DIRECTION = "direction";
    static final String RECIPE_CHECK = "checked";
    static final String RECIPE_IMAGE_URL = "imageUrl";
    static final String RECIPE_LAST_UPDATE = "lastUpdateDate";

    static List<Recipe> getAllRecipes(SQLiteDatabase db) {
        Cursor cursor = db.query("recipe", null, null, null, null, null, null);
        List<Recipe> list = new LinkedList<Recipe>();
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(RECIPE_ID);
            int nameIndex = cursor.getColumnIndex(RECIPE_NAME);
            int descIndex = cursor.getColumnIndex(RECIPE_DESCRIPTION);
            int ingredientIndex = cursor.getColumnIndex(RECIPE_INGREDIENT);
            int recipeByIndex = cursor.getColumnIndex(RECIPE_BY);
            int directionIndex = cursor.getColumnIndex(RECIPE_DIRECTION);
            int checkIndex = cursor.getColumnIndex(RECIPE_CHECK);
            int imageUrlIndex = cursor.getColumnIndex(RECIPE_IMAGE_URL);
            int lastUpdateDateIndex = cursor.getColumnIndex(RECIPE_LAST_UPDATE);

            do {
                Recipe rcp = new Recipe();
                rcp.id = cursor.getString(idIndex);
                rcp.recipeName = cursor.getString(nameIndex);
                rcp.vegetarian = (cursor.getInt(checkIndex) == 1);
                rcp.imageUrl = cursor.getString(imageUrlIndex);
                rcp.lastUpdateDate = cursor.getDouble(lastUpdateDateIndex);
                rcp.shortDescription=cursor.getString(descIndex);
                rcp.recipeBy = cursor.getString(recipeByIndex);
                rcp.ingredient = cursor.getString(ingredientIndex);
                rcp.direction = cursor.getString(directionIndex);
                list.add(rcp);

            } while (cursor.moveToNext());
        }
        return list;
    }

    static void addRecipe(SQLiteDatabase db, Recipe rcp) {
        ContentValues values = new ContentValues();
        values.put(RECIPE_ID, rcp.id);
        values.put(RECIPE_NAME, rcp.recipeName);
        values.put(RECIPE_DESCRIPTION, rcp.shortDescription);
        values.put(RECIPE_BY, rcp.recipeBy);
        values.put(RECIPE_INGREDIENT, rcp.ingredient);
        values.put(RECIPE_DIRECTION, rcp.direction);
        if (rcp.vegetarian) {
            values.put(RECIPE_CHECK, 1);
        } else {
            values.put(RECIPE_CHECK, 0);
        }
        values.put(RECIPE_IMAGE_URL, rcp.imageUrl);
        values.put(RECIPE_LAST_UPDATE, rcp.lastUpdateDate);
        db.insert(RECIPE_TABLE, RECIPE_ID, values);
        //rdb.update(RECIPE_TABLE, values, "rcpid=" + rcp.id, null);

        }

        static void deleteRecipe(SQLiteDatabase db, String rcpID) {

            int i = db.delete(RECIPE_TABLE, "rcpid=?", new String[]{rcpID});
            if (i > 0) {
                Log.d("TAG", "success to delete " + rcpID);
            }
        }

    static Recipe getRecipe(SQLiteDatabase db, String rcpId) {

        Cursor cursor = db.rawQuery("SELECT * FROM " + RECIPE_TABLE
                + " WHERE  rcpid=?", new String[]{rcpId});
        Recipe rcp = new Recipe();
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(RECIPE_ID);
            int nameIndex = cursor.getColumnIndex(RECIPE_NAME);
            int descIndex = cursor.getColumnIndex(RECIPE_DESCRIPTION);
            int ingredientIndex = cursor.getColumnIndex(RECIPE_INGREDIENT);
            int recipeByIndex = cursor.getColumnIndex(RECIPE_BY);
            int directionIndex = cursor.getColumnIndex(RECIPE_DIRECTION);
            int checkIndex = cursor.getColumnIndex(RECIPE_CHECK);
            int imageUrlIndex = cursor.getColumnIndex(RECIPE_IMAGE_URL);
            int lastUpdateDateIndex = cursor.getColumnIndex(RECIPE_LAST_UPDATE);

            do {
                rcp.id = cursor.getString(idIndex);
                rcp.recipeName = cursor.getString(nameIndex);
                rcp.vegetarian = (cursor.getInt(checkIndex) == 1);
                rcp.imageUrl = cursor.getString(imageUrlIndex);
                rcp.lastUpdateDate = cursor.getDouble(lastUpdateDateIndex);
                rcp.shortDescription=cursor.getString(descIndex);
                rcp.recipeBy = cursor.getString(recipeByIndex);
                rcp.ingredient = cursor.getString(ingredientIndex);
                rcp.direction = cursor.getString(directionIndex);
            } while (cursor.moveToNext());
        }
        return rcp;
    }

    static public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + RECIPE_TABLE +
                " (" +
                RECIPE_ID + " TEXT PRIMARY KEY, " +
                RECIPE_NAME + " TEXT, " +
                RECIPE_DESCRIPTION + " TEXT, " +
                RECIPE_BY + " TEXT, " +
                RECIPE_INGREDIENT + " TEXT, " +
                RECIPE_DIRECTION + " TEXT, " +
                RECIPE_CHECK + " NUMBER, " +
                RECIPE_LAST_UPDATE + " NUMBER, " +
                RECIPE_IMAGE_URL + " TEXT);";
        Log.d("TAG",sql);
        db.execSQL(sql);
    }

    static public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table " + RECIPE_TABLE + ";");
        onCreate(db);
    }

}

package com.rubi.recipbook;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity
        implements RecipeDetailsFragment.OnHeadlineSelectedListener{

     String recipeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecipeListFragment listFragment = RecipeListFragment.newInstance();
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.replace(R.id.main_container,listFragment);
        tran.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.edit_main,menu);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.main_add:
                fragment = AddRecipeFragment.newInstance();
                break;
            case android.R.id.home:
                fragment=RecipeListFragment.newInstance();

                break;
           case R.id.main_edit:
                fragment= EditRecipeFragment.newInstance(recipeID);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        if (fragment != null){
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_container, fragment);
            if (itemId != android.R.id.home) {
                transaction.addToBackStack("");
            }
            transaction.commit();
        }
        else{
            Log.d("TAG", "Main activity - Error in creating fragment");
        }
        return true;
    }

/*    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }*/

    @Override
    public void onArticleSelected(String recipeID) {
        this.recipeID=recipeID;
        Log.d("TAG", "Recipe id is"+ recipeID);

    }
}

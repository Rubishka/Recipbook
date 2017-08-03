package com.rubi.recipbook;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.rubi.recipbook.model.Model;
import com.rubi.recipbook.model.Recipe;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.List;


public class RecipeListFragment extends Fragment implements View.OnClickListener{

    ListView list;
    RecipeListAdapter adapter;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Model.UpdateRecipeEvent event) {
        Toast.makeText(MyApplication.getMyContext(), "got new recipe event", Toast.LENGTH_SHORT).show();
        boolean exist = false;
        for (Recipe rcp: adapter.data){
            Log.d("TAG", "rcp in list fragment: "+rcp.recipeName);
            if (rcp.id.equals(event.recipe.id)){
                rcp = event.recipe;
                exist = true;
                break;
            }
        }
        if (!exist){
            adapter.data.add(event.recipe);
        }
        adapter.notifyDataSetChanged();
        list.setSelection(adapter.getCount() - 1);

    }



     public RecipeListFragment() {}

    public static RecipeListFragment newInstance() {

        RecipeListFragment fragment = new RecipeListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new RecipeListAdapter();
        setHasOptionsMenu(true);

        EventBus.getDefault().register(this);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear(); // clears all menu items..
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    EditText itemIdEditTest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("TAG", "onCreateView");

        View contentView = inflater.inflate(R.layout.fragment_list_recipe, container, false);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        list = (ListView) contentView.findViewById(R.id.stlist_list);

        adapter = new RecipeListAdapter();
         list.setAdapter(adapter);
        Model.instace.getAllRecipes(new Model.GetAllRecipesAndObserveCallback() {
            @Override
            public void onComplete(List<Recipe> list) {
                adapter.data=list;
                adapter.notifyDataSetChanged();
                Log.d("TAG", "getAllRecipes, the recipes: ");
                for (Recipe rcp :adapter.data){
                    Log.d("TAG", rcp.id);
                }
            }
            @Override
            public void onCancel() {
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("TAG", "row item was clicked at position: " + position);
                Log.d("TAG", "row item was clicked with ID : " + adapter.data.get(position).id);
                RecipeDetailsFragment recipedetails= RecipeDetailsFragment.newInstance(adapter.data.get(position).id);
                FragmentTransaction tran = getFragmentManager().beginTransaction();
                tran.replace(R.id.main_container, recipedetails);
                tran.addToBackStack(null);
                tran.commit();

            }
        });


        boolean hasPermission = (ContextCompat.checkSelfPermission(MyApplication.getMyContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions((Activity) MyApplication.getMyContext(),new String[]{
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        }
        Log.d("TAG", "return contentView");

        return contentView;
    }

    static final int REQUEST_ADD_ID = 1;
    static final int REQUEST_WRITE_STORAGE = 11;


    @Override
    public void onResume() {
        super.onResume();
        //adapter.notifyDataSetChanged();

    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);
        super.onDetach();
    }


    @Override
    public void onClick(View v) {

    }





}

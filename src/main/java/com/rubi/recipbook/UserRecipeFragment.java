package com.rubi.recipbook;

import android.app.FragmentTransaction;
import android.os.Bundle;

import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.rubi.recipbook.model.Model;
import com.rubi.recipbook.model.Recipe;
import com.rubi.recipbook.model.User;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class UserRecipeFragment extends Fragment {

    ListView list;
    RecipeListAdapter adapter;
    String userName;
    static final int USER_FRAGMENT = 1;

    public UserRecipeFragment() {}

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Model.UpdateRecipeEvent event) {
        Toast.makeText(MyApplication.getMyContext(), "New recipe added", Toast.LENGTH_SHORT).show();
        Log.d("TAG", "Subscribe: UpdateRecipeEvent");
        boolean exist = false;
        for (Recipe rcp: adapter.data){

            Log.d("TAG", "rcp in list fragment: "+rcp.recipeName);
            if (rcp.id.equals(event.recipe.id)){
                rcp = event.recipe;
                exist = true;
                break;
            }
        }
        if (!exist&& event.recipe.recipeBy.equals(userName)){
            adapter.data.add(event.recipe);
        }
        adapter.notifyDataSetChanged();
        list.setSelection(adapter.getCount() - 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Model.DeleteRecipeEvent event) {
        Log.d("TAG", "notify delete");
        if (event.recipe.recipeBy.equals(userName)) {
            adapter.data.remove(event.recipe);
            adapter.notifyDataSetChanged();
        }
    }

    public static UserRecipeFragment newInstance() {
        UserRecipeFragment fragment = new UserRecipeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new RecipeListAdapter();

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear(); // clears all menu items..
        getActivity().getMenuInflater().inflate(R.menu.user_main, menu);
        getActivity().setTitle(userName+" recipes");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contentView = inflater.inflate(R.layout.fragment_user_recipe, container, false);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        list = (ListView) contentView.findViewById(R.id.stlist_list);
        adapter = new RecipeListAdapter();

        userName=Model.instace.getUser().name;

        Log.d("TAG", "username is: "+userName);
        Model.instace.getAllRecipes(new Model.GetAllRecipesAndObserveCallback() {
            @Override
            public void onComplete(List<Recipe> list) {
                for (Recipe rcp: list){

                    if (userName.equals(rcp.recipeBy)) {
                        adapter.data.add(rcp);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancel() {
            }
        });
        list.post(new Runnable() {
            public void run() {
                list.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RecipeDetailsFragment recipedetails= RecipeDetailsFragment.newInstance(adapter.data.get(position).id,USER_FRAGMENT);
                FragmentTransaction tran = getFragmentManager().beginTransaction();
                tran.replace(R.id.main_container, recipedetails);
                tran.addToBackStack("");
                tran.commit();
            }
        });
        return contentView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
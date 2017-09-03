package com.rubi.recipbook;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rubi.recipbook.model.Model;
import com.rubi.recipbook.model.Recipe;


public class RecipeDetailsFragment extends Fragment {

    private static final String ARG_PARAM1 = "recipeID";
    private static final String ARG_PARAM2 = "user fragment";
    protected String recipeID;
    protected int userFragment;
    OnHeadlineSelectedListener mCallback;
    Recipe rcp;

    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(String recipeID);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public RecipeDetailsFragment() {}

    public static RecipeDetailsFragment newInstance(String recipeID,int userFragment) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM2, String.valueOf(userFragment));
        args.putString(ARG_PARAM1, recipeID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            recipeID = getArguments().getString(ARG_PARAM1);
            setHasOptionsMenu(true);
             userFragment= Integer.parseInt(getArguments().getString(ARG_PARAM2));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear(); // clears all menu items..
        getActivity().setTitle("Recipe details");
        if (userFragment==1) {
            getActivity().getMenuInflater().inflate(R.menu.edit_main, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
        else{
            getActivity().getMenuInflater().inflate(R.menu.empty_main  , menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View contentView= inflater.inflate(R.layout.fragment_details_recipe, container, false);

        Model.instace.getRecipe(recipeID, new Model.GetRecipeCallback() {
                    @Override
                    public void onComplete(Recipe recipe) {
                        rcp=recipe;

                        TextView nameTV = (TextView) contentView.findViewById(R.id.detailRecipe_name);
                        nameTV.setText(rcp.recipeName);

                        TextView idTV = (TextView) contentView.findViewById(R.id.detailRecipe_ID);
                        idTV.setText(rcp.id);

                        TextView descriptionTV = (TextView) contentView.findViewById(R.id.detailRecipe_description);
                        descriptionTV.setText(rcp.shortDescription);

                        TextView recipeByTV = (TextView) contentView.findViewById(R.id.detailRecipe_recipeBy);
                        recipeByTV.setText(rcp.recipeBy);

                        TextView directionTV = (TextView) contentView.findViewById(R.id.detailRecipe_direction);
                        directionTV.setText(rcp.direction);

                        TextView ingredientTV = (TextView) contentView.findViewById(R.id.detailRecipe_ingredient);
                        ingredientTV.setText(rcp.ingredient);

                        CheckBox cbTV = (CheckBox) contentView.findViewById(R.id.detailRecipe_CB);
                        cbTV.setChecked(rcp.vegetarian);

                        final ImageView imageView = (ImageView) contentView.findViewById(R.id.detailRecipe_Image);
                        imageView.setTag(rcp.imageUrl);
                        final ProgressBar progressBar = (ProgressBar) contentView.findViewById(R.id.detailRecipe_ProgressBar);
                        progressBar.setVisibility(View.GONE);
                        imageView.setImageDrawable(MyApplication.getMyContext().getDrawable(R.drawable.food));

                        if (rcp.imageUrl != null && !rcp.imageUrl.isEmpty() && !rcp.imageUrl.equals("")) {
                            progressBar.setVisibility(View.VISIBLE);
                            Model.instace.getImage(rcp.imageUrl, new Model.GetImageListener() {
                                @Override
                                public void onSuccess(Bitmap image) {
                                    String tagUrl = imageView.getTag().toString();
                                    if (tagUrl.equals(rcp.imageUrl)) {
                                        imageView.setImageBitmap(image);
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                                @Override
                                public void onFail() {
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                   @Override
                    public void onCancel() {
                        Log.d("TAG", "cant find recipe with id "+ recipeID  );
                    }
                });
        mCallback.onArticleSelected(recipeID);
        return contentView;
    }

}

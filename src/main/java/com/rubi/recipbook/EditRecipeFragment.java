package com.rubi.recipbook;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rubi.recipbook.model.Model;
import com.rubi.recipbook.model.Recipe;

import static android.view.View.GONE;
import static com.rubi.recipbook.AddRecipeFragment.REQUEST_IMAGE_CAPTURE;


public class EditRecipeFragment extends Fragment    {

    final static int RESAULT_SUCCESS = 1;
    final static int RESAULT_FAIL = 0;

    Recipe rcp;

    Bitmap imageBitmap;
    EditText nameEt;
    TextView idEt;
    EditText descriptionEt;
    EditText recipebyEt;
    EditText directionEt;
    EditText ingredientEt;
    CheckBox cb;
     //MyDatePicker date;
    ImageView imageView;
    ProgressBar progressBar;


    private static final String ARG_PARAM1 = "recipeID";
    private String recipeID;
    RecipeDetailsFragment fragment;


    public EditRecipeFragment() {}


    public static EditRecipeFragment newInstance(String recipeID) {
        EditRecipeFragment fragment = new EditRecipeFragment();

        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, recipeID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear(); // clears all menu items..
        getActivity().getMenuInflater().inflate(R.menu.empty_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View contentView= inflater.inflate(R.layout.fragment_edit_recipe, container, false);

        nameEt = (EditText) contentView.findViewById(R.id.editRecipe_name);
        idEt= (TextView) contentView.findViewById(R.id.editRecipe_ID);
        descriptionEt= (EditText) contentView.findViewById(R.id.editRecipe_description);
        recipebyEt= (EditText) contentView.findViewById(R.id.editRecipe_RecipeBy);
        directionEt= (EditText) contentView.findViewById(R.id.aeditRecipe_directions);
        ingredientEt= (EditText) contentView.findViewById(R.id.editRecipe_ingredient);
        //date = (MyDatePicker) contentView.findViewById(R.id.edit_recipe_birth_date);
        cb = (CheckBox) contentView.findViewById(R.id.editRecipe_CB);
        imageView = (ImageView) contentView.findViewById(R.id.editRecipe_Image);

        progressBar = (ProgressBar) contentView.findViewById(R.id.editRecipe_progressBar);
        progressBar.setVisibility(GONE);


        final String recipeID=getArguments().getString("recipeID");

               rcp = Model.instace.getRecipe(recipeID);
                nameEt.setText(rcp.recipeName);
                idEt.setText(rcp.id);
                descriptionEt.setText(rcp.shortDescription);
                directionEt.setText(rcp.direction);
                recipebyEt.setText(rcp.recipeBy);
                ingredientEt.setText(rcp.ingredient);
                cb.setChecked(rcp.vegetarian);
                imageView.setTag(rcp.imageUrl);

                imageView.setImageDrawable(MyApplication.getMyContext().getDrawable(R.drawable.food));

                if (rcp.imageUrl != null && !rcp.imageUrl.isEmpty() && !rcp.imageUrl.equals("")){
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


                Button saveBtn = (Button) contentView.findViewById(R.id.editRecipe_SaveBtn);
                Button cancelBtn = (Button) contentView.findViewById(R.id.editRecipe_CancelBtn);
                Button deleteBtn = (Button) contentView.findViewById(R.id.editRecipe_deleteBtn);


                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressBar.setVisibility(View.VISIBLE);

                        final Recipe rcp = new Recipe();
                        rcp.id = idEt.getText().toString();
                        rcp.recipeName = nameEt.getText().toString();
                        rcp.shortDescription= descriptionEt.getText().toString();
                        rcp.recipeBy= recipebyEt.getText().toString();
                        rcp.ingredient= ingredientEt.getText().toString();
                        rcp.direction= directionEt.getText().toString();
                        rcp.imageUrl = "";
                        rcp.vegetarian = false;
                        if (imageBitmap != null) {
                            Model.instace.saveImage(imageBitmap, rcp.id + ".jpeg", new Model.SaveImageListener() {
                                @Override
                                public void complete(String url) {
                                    rcp.imageUrl = url;
                                    Model.instace.addRecipe(rcp);
                                    progressBar.setVisibility(GONE);
                                }

                                @Override
                                public void fail() {
                                    //notify operation fail,...
                                    progressBar.setVisibility(GONE);
                                }
                            });
                        }else {
                            Model.instace.addRecipe(rcp);
                            progressBar.setVisibility(GONE);
                        }

                        RecipeListFragment listFragment = RecipeListFragment.newInstance();
                        FragmentTransaction tran = getFragmentManager().beginTransaction();
                        tran.replace(R.id.main_container,listFragment);
                        tran.commit();
                    }
                });

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getFragmentManager().popBackStack();
                    }
                });

                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Model.instace.deleteRecipe(recipeID);
                        RecipeListFragment listFragment = RecipeListFragment.newInstance();
                        FragmentTransaction tran = getFragmentManager().beginTransaction();
                        tran.replace(R.id.main_container,listFragment);
                        //getFragmentManager().popBackStack();
                        tran.commit();
                    }
                });
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dispatchTakePictureIntent();
                    }
                });
        return contentView;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(MyApplication.getMyContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

}

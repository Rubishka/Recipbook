package com.rubi.recipbook;

import android.app.Fragment;
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
import android.widget.Toast;

import com.rubi.recipbook.model.Model;
import com.rubi.recipbook.model.Recipe;
import com.rubi.recipbook.model.User;

import static android.R.attr.data;
import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;


public class AddRecipeFragment extends Fragment {

    //final static int RESAULT_SUCCESS = 0;
    //final static int RESAULT_FAIL = 1;

    ImageView imageView;
    Bitmap imageBitmap;
    ProgressBar progressBar;

    public AddRecipeFragment() {}

    public static AddRecipeFragment newInstance() {
        AddRecipeFragment fragment = new AddRecipeFragment();
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
        getActivity().setTitle("New recipe");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View contentView = inflater.inflate(R.layout.fragment_new_recipe, container, false);

        final EditText nameEt = (EditText) contentView.findViewById(R.id.addRecipe_name);
        final EditText idEt= (EditText) contentView.findViewById(R.id.addRecipe_ID);
        final EditText descriptionEt= (EditText) contentView.findViewById(R.id.addRecipe_description);

        final TextView recipeByEt= (TextView) contentView.findViewById(R.id.addRecipe_RecipeBy);

                recipeByEt.setText(Model.instace.getUser().name);

        final EditText ingredientEt= (EditText) contentView.findViewById(R.id.addRecipe_ingredient);
        final EditText directionEt= (EditText) contentView.findViewById(R.id.addRecipe_directions);
        //final EditText lastUpdateDateEt= (EditText) contentView.findViewById(R.id.mainPhoneTv);
        final CheckBox vegetarian = (CheckBox) contentView.findViewById(R.id.addRecipe_CB);
        //final MyDatePicker datePicker = (MyDatePicker) contentView.findViewById(R.id.add_recipe_birth_date);
        imageView = (ImageView) contentView.findViewById(R.id.addRecipe_Image);


        progressBar = (ProgressBar) contentView.findViewById(R.id.addRecipe_ProgressBar);
        progressBar.setVisibility(GONE);

        Button saveBtn = (Button) contentView.findViewById(R.id.addRecipe_saveBtn);
        Button cancelBtn = (Button) contentView.findViewById(R.id.addRecipe_cancelBtn);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                Log.d("TAG","Btn Save click");
                final Recipe rcp = new Recipe();

                rcp.id = idEt.getText().toString();
                rcp.recipeName = nameEt.getText().toString();
                rcp.shortDescription= descriptionEt.getText().toString();
                rcp.recipeBy= recipeByEt.getText().toString();
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
                            getFragmentManager().popBackStack();
                        }

                        @Override
                        public void fail() {
                            Toast.makeText(MyApplication.getMyContext(), "Saving recipe failed", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(GONE);
                            getFragmentManager().popBackStack();
                        }
                    });
                }else{
                    Model.instace.addRecipe(rcp);
                    progressBar.setVisibility(GONE);
                    getFragmentManager().popBackStack();
                }


            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        return contentView;
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(MyApplication.getMyContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }
}

package com.rubi.recipbook;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rubi.recipbook.model.Model;
import com.rubi.recipbook.model.Recipe;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by rubi on 22/05/2017.
 */


public class RecipeListAdapter extends BaseAdapter {


    List<Recipe> data = new ArrayList<>();

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater lInflater = (LayoutInflater)MyApplication.getMyContext().getSystemService(
                    Activity.LAYOUT_INFLATER_SERVICE);
            convertView = lInflater.inflate(R.layout.recipes_list_row, null);
        }

        final TextView nameTV = (TextView) convertView.findViewById(R.id.listAdapter_name);
        final TextView descriptionTV= (TextView) convertView.findViewById(R.id.listAdapter_description);
        final TextView recipeByTV= (TextView) convertView.findViewById(R.id.listAdapter_recipeBy);
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.listAdapter_Image);
        final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.listAdapter_progressBar);
        progressBar.setVisibility(View.GONE);

        final Recipe rcp = data.get(position);

        nameTV.setText(rcp.recipeName);
        descriptionTV.setText(rcp.shortDescription);
        recipeByTV.setText(rcp.recipeBy);
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
        return convertView;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
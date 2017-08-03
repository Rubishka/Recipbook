package com.rubi.recipbook.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.URLUtil;
import com.rubi.recipbook.MyApplication;
import org.greenrobot.eventbus.EventBus;
import java.util.List;
import static com.rubi.recipbook.model.ModelFiles.saveImageToFile;


public class Model {
    public final static Model instace = new Model();

    private ModelMem modelMem;
    private ModelSql modelSql;
    private ModelFirebase modelFirebase;

    private Model() {
        modelMem = new ModelMem();
        modelSql = new ModelSql(MyApplication.getMyContext());
        modelFirebase = new ModelFirebase();
        synchRecipesDbAndregisterRecipesUpdates();
    }

    public void addRecipe(Recipe rcp) {
        //RecipeSql.addRecipe(modelSql.getWritableDatabase(),rcp);
        modelFirebase.addRecipe(rcp);

    }

    public void deleteRecipe(String rcpID){
        Log.d("TAG", "Trying to delete recipe "+ rcpID);
        //RecipeSql.deleteRecipe(modelSql.getWritableDatabase(), rcpID);
        modelFirebase.deleteRecipe(rcpID, new ModelFirebase.DeleteRecipeCallback() {
            @Override
            public void onComplete() {
            }
        });
    }

    public interface GetRecipeCallback {
        void onComplete(Recipe recipe);
        void onCancel();
    }




    public void getRecipe(String stId, final GetRecipeCallback callback) {
        //Recipe recipe = RecipeSql.getRecipe(modelSql.getReadableDatabase(),stId);

        modelFirebase.getRecipe(stId, new ModelFirebase.GetRecipeCallback() {
            @Override
            public void onComplete(Recipe recipe) {
                callback.onComplete(recipe);
            }

            @Override
            public void onCancel() {
                callback.onCancel();
            }
        });

    }


    public interface GetAllRecipesAndObserveCallback {
        void onComplete(List<Recipe> list);
        void onCancel();
    }

    private void synchRecipesDbAndregisterRecipesUpdates() {
        //1. get local lastUpdateTade
        SharedPreferences pref = MyApplication.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        final double lastUpdateDate = pref.getFloat("RecipesLastUpdateDate ",0);
        Log.d("TAG","lastUpdateDate: " + lastUpdateDate);

        modelFirebase.registerRecipesUpdates(lastUpdateDate,new ModelFirebase.RegisterRecipesUpdatesCallback() {
            @Override
            public void onRecipeUpdate(Recipe recipe) {
                //3. update the local db
                RecipeSql.addRecipe(modelSql.getWritableDatabase(), recipe);
                //4. update the lastUpdateTade
                SharedPreferences pref = MyApplication.getMyContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
                final double lastUpdateDate = pref.getFloat("RecipesLastUpdateDate",0);
                if (lastUpdateDate < recipe.lastUpdateDate){
                    SharedPreferences.Editor prefEd = MyApplication.getMyContext().getSharedPreferences("TAG",
                            Context.MODE_PRIVATE).edit();
                    prefEd.putFloat("RecipesLastUpdateDate", (float) recipe.lastUpdateDate);
                    prefEd.commit();
                    Log.d("TAG","RecipeLastUpdateDate: " + recipe.lastUpdateDate);
                }

                EventBus.getDefault().post(new UpdateRecipeEvent(recipe));
            }

            @Override
            public void onRecipeDelete(Recipe recipe) {
                RecipeSql.deleteRecipe(modelSql.getWritableDatabase(), recipe.id);
            }
        });
    }

    public void getAllRecipes(final GetAllRecipesAndObserveCallback callback){

        //5. read from local db
        List<Recipe> data = RecipeSql.getAllRecipes(modelSql.getReadableDatabase());

        //6. return list of recipe
        callback.onComplete(data);
    }

    public interface SaveImageListener {
        void complete(String url);
        void fail();
    }

    public void saveImage(final Bitmap imageBmp, final String name, final SaveImageListener listener) {
        modelFirebase.saveImage(imageBmp, name, new SaveImageListener() {
            @Override
            public void complete(String url) {
                String fileName = URLUtil.guessFileName(url, null, null);
                saveImageToFile(imageBmp,fileName);
                listener.complete(url);
            }

            @Override
            public void fail() {
                listener.fail();
            }
        });
    }

    public interface GetImageListener{
        void onSuccess(Bitmap image);
        void onFail();
    }

    public void getImage(final String url, final GetImageListener listener) {
        //check if image exsist localy
        final String fileName = URLUtil.guessFileName(url, null, null);
        ModelFiles.loadImageFromFileAsynch(fileName, new ModelFiles.LoadImageFromFileAsynch() {
            @Override
            public void onComplete(Bitmap bitmap) {
                if (bitmap != null){
                    Log.d("TAG","getImage from local success " + fileName);
                    listener.onSuccess(bitmap);
                }else {
                    modelFirebase.getImage(url, new GetImageListener() {
                        @Override
                        public void onSuccess(Bitmap image) {
                            String fileName = URLUtil.guessFileName(url, null, null);
                            Log.d("TAG","getImage from FB success " + fileName);
                            saveImageToFile(image,fileName);
                            listener.onSuccess(image);
                        }

                        @Override
                        public void onFail() {
                            Log.d("TAG","getImage from FB fail ");
                            listener.onFail();
                        }
                    });

                }
            }
        });
    }

    public class UpdateRecipeEvent {
        public final Recipe recipe;
        public UpdateRecipeEvent(Recipe recipe) {
            this.recipe = recipe;
        }
    }

}
















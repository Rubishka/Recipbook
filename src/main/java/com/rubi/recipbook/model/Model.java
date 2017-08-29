package com.rubi.recipbook.model;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rubi.recipbook.LoginFragment;
import com.rubi.recipbook.MainActivity;
import com.rubi.recipbook.MyApplication;
import com.rubi.recipbook.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;
import static com.rubi.recipbook.model.ModelFiles.saveImageToFile;

public class Model {
    public final static Model instace = new Model();

    private ModelMem modelMem;
    private ModelSql modelSql;
    private ModelFirebase modelFirebase;
    private User user;
    private  FirebaseAuth mAuth;

    private Model() {
        modelMem = new ModelMem();
        modelSql = new ModelSql(MyApplication.getMyContext());
        modelFirebase = new ModelFirebase();
        mAuth = FirebaseAuth.getInstance();
        synchRecipesDbAndregisterRecipesUpdates();

    }

    public void addRecipe(Recipe rcp) {
        modelFirebase.addRecipe(rcp);
    }

    public void addUser(User user){
        modelFirebase.addUser(user);
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

    public interface GetUserCallback {
        void onComplete(User user);
        void onCancel();
    }

    public User getUser(){
        return this.user;
    }

    public Recipe getRecipe(String rcpID){
        Recipe rcp =RecipeSql.getRecipe(modelSql.getReadableDatabase(),rcpID);
        Log.d("TAG", "get the recipe " + rcp.id);
        return rcp;
}

    public void getRecipe(String rcpId, final GetRecipeCallback callback) {
        modelFirebase.getRecipe(rcpId, new ModelFirebase.GetRecipeCallback() {
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
            public void onRecipeAdded(Recipe recipe) {
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
                Log.d("TAG", "onRecipeDelete calld for delete recipe " + recipe.id);
                RecipeSql.deleteRecipe(modelSql.getWritableDatabase(), recipe.id);
                EventBus.getDefault().post(new DeleteRecipeEvent(recipe));
            }
            @Override
            public void onRecipeUpdate(Recipe recipe) {

            }
        });
    }

    public void getAllRecipes(final GetAllRecipesAndObserveCallback callback){
        //5. read from local db
        List<Recipe> data = RecipeSql.getAllRecipes(modelSql.getReadableDatabase());
        Log.d("TAG", "is getAllRecipes data empty? "+data.isEmpty());
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

    public class DeleteRecipeEvent {
        public final Recipe recipe;
        public DeleteRecipeEvent(Recipe recipe) {
            this.recipe = recipe;
        }
    }

    public interface signInCallback {
        void onComplete();
        void onCancel();
    }

    public void signIn(String email,String password,Activity activity,final signInCallback callback){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            modelFirebase.getUser(new ModelFirebase.GetUserCallback() {
                                @Override
                                public void onComplete(User FBuser) {
                                    user=FBuser;
                                    Log.d("TAG", "user name: "+user.name);
                                }
                                @Override
                                public void onCancel() {
                                    user.name="anonymous";
                                }
                            });
                            callback.onComplete();
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            callback.onCancel();
                        }
                    }
                });
    }
    
    public void createAccount(final String name, final String email, String password, Activity activity){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser FBuser = mAuth.getCurrentUser();
                            User user=new User(name, FBuser.getUid(),email);
                            Model.instace.addUser(user);
                            Toast.makeText(MyApplication.getMyContext(), "Authentication success.",
                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(MyApplication.getMyContext(), "Log in",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MyApplication.getMyContext(), "Authentication failed.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}

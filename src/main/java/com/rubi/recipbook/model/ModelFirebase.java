package com.rubi.recipbook.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class ModelFirebase {

    List<ChildEventListener> listeners = new LinkedList<ChildEventListener>();

    public void addRecipe(Recipe rcp) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("recipe");
        Map<String, Object> value = new HashMap<>();
        value.put("id", rcp.id);
        value.put("recipeName", rcp.recipeName);
        value.put("imageUrl", rcp.imageUrl);
        value.put("shortDescription", rcp.shortDescription);
        value.put("vegetarian", rcp.vegetarian);
        value.put("recipeBy", rcp.recipeBy);
        value.put("ingredient", rcp.ingredient);
        value.put("direction", rcp.direction);
        value.put("lastUpdateDate", ServerValue.TIMESTAMP);
        myRef.child(rcp.id).setValue(value);
    }

    public void addUser(User user){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        Map<String, Object> value = new HashMap<>();
        value.put("userID", user.userID);
        value.put("name", user.name);
        //value.put("email", user.Email);
        myRef.child(user.userID).setValue(value);
    }

    public interface GetUserCallback {
        void onComplete(User user);
        void onCancel();
    }

    public void getUser(final GetUserCallback callBack){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        if (user != null) {
            myRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user= dataSnapshot.getValue(User.class);
                    Log.d("TAG", user.name);
                    callBack.onComplete(user);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callBack.onCancel();
                }
            });
        } else {
            callBack.onCancel();
        }
    }

    interface GetRecipeCallback {
        void onComplete(Recipe recipe);
        void onCancel();
    }

    public void getRecipe(String rcpId, final GetRecipeCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("recipe");
        myRef.child(rcpId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                callback.onComplete(recipe);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancel();
            }
        });
    }

    interface DeleteRecipeCallback {
        void onComplete();
        //oid onCancel();
    }

    public void deleteRecipe(String rcpID ,final DeleteRecipeCallback callback){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("recipe");
        myRef.child(rcpID).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                callback.onComplete();
            }
        });
    }

    interface GetAllRecipesAndObserveCallback {
        void onComplete(List<Recipe> list);
        void onCancel();
    }

    interface RegisterRecipesUpdatesCallback{
        void onRecipeUpdate(Recipe recipe);
        void onRecipeDelete(Recipe recipe);
        void onRecipeAdded(Recipe recipe);
    }
    public void registerRecipesUpdates(double lastUpdateDate,
                                        final RegisterRecipesUpdatesCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("recipe");
        myRef.orderByChild("lastUpdateDate").startAt(lastUpdateDate)//;
        //ChildEventListener listener = myRef.orderByChild("lastUpdateDate").startAt(lastUpdateDate)
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                callback.onRecipeAdded(recipe);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                callback.onRecipeAdded(recipe);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                callback.onRecipeDelete(recipe);
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                callback.onRecipeUpdate(recipe);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void saveImage(Bitmap imageBmp, String name, final Model.SaveImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference imagesRef = storage.getReference().child("images").child(name);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                listener.fail();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                listener.complete(downloadUrl.toString());
            }
        });
    }

    public void getImage(String url, final Model.GetImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(url);
        final long ONE_MEGABYTE = 1024 * 1024;
        httpsReference.getBytes(3* ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                listener.onSuccess(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                listener.onFail();
            }
        });
    }

}
package com.rubi.recipbook.model;

import java.util.LinkedList;
import java.util.List;


public class ModelMem {

    private List<Recipe> data = new LinkedList<Recipe>();

    ModelMem(){}

    List<Recipe> getAllRecipes(){
        return data;
    }

    void addRecipe(Recipe st){
        data.add(st);
    }

    Recipe getRecipe(String rcpId) {
        for (Recipe s : data){
            if (s.id.equals(rcpId)){
                return s;
            }
        }
        return null;
    }

}

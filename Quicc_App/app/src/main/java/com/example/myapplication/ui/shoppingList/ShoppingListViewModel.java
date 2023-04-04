package com.example.myapplication.ui.shoppingList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ShoppingListViewModel extends ViewModel {

    private MutableLiveData<ArrayList<String>> mItemsName;
    private MutableLiveData<ArrayList<String>> mRecommandationName;

    public ShoppingListViewModel() {
        mItemsName = new MutableLiveData<>();
        mRecommandationName = new MutableLiveData<>();

        mItemsName.setValue(new ArrayList<>());
        mRecommandationName.setValue(new ArrayList<>());
    }


    public LiveData<ArrayList<String>> getItemsList() {
        return mItemsName;
    }

    public LiveData<ArrayList<String>> getRecommandationList() {
        return mRecommandationName;
    }

    public void setmItemsList(ArrayList<String> itemsLists) {
        mItemsName.setValue(itemsLists);
    }

    public void setmRecommandationList(ArrayList<String> recommandationList) {
        mItemsName.setValue(recommandationList);
    }

}
package com.moutamid.treesme.Hairdresser.ui.chart;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChartViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ChartViewModel() {
        mText = new MutableLiveData<>();
      //  mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
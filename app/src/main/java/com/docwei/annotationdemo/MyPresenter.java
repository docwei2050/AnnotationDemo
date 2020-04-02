package com.docwei.annotationdemo;

import android.util.Log;

import com.docwei.annotation.LifePhaseObserver;
import com.docwei.annotation.OnLifePhaseEvent;
import com.docwei.annotation.LifePhaseEvent;

public class MyPresenter implements LifePhaseObserver {
    @OnLifePhaseEvent(LifePhaseEvent.onCreate)
    public void onMyCreate() {
        Log.e("annotation1", "onMyCreate: ");
    }

    @OnLifePhaseEvent(LifePhaseEvent.onStart)
    public void onMyStart() {
        Log.e("annotation1", "onMyStart: ");
    }
}

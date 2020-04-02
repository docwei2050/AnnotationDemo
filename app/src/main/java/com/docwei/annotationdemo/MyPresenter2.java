package com.docwei.annotationdemo;

import android.util.Log;

import com.docwei.annotation.LifePhaseEvent;
import com.docwei.annotation.LifePhaseObserver;
import com.docwei.annotation.OnLifePhaseEvent;

public class MyPresenter2 implements LifePhaseObserver {
    @OnLifePhaseEvent(LifePhaseEvent.onCreate)
    public void onMyCreate2() {
        Log.e("annotation1", "onMyCreate2: ");
    }

    @OnLifePhaseEvent(LifePhaseEvent.onStart)
    public void onMyStart2() {
        Log.e("annotation1", "onMyStart2: ");
    }
    @OnLifePhaseEvent(LifePhaseEvent.onStart)
    public void onMyStart4() {
        Log.e("annotation1", "onMyStart2: ");
    }
    @OnLifePhaseEvent(LifePhaseEvent.onStart)
    public void onMyStart5() {
        Log.e("annotation1", "onMyStart2: ");
    }
}

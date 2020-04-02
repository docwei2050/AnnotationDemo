package com.docwei.annotationdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.docwei.annotation.owner.ILifePhaseOwner;
import com.docwei.annotation.LifePhaseEvent;
import com.docwei.annotation.owner.LifePhaseOwner;
import com.docwei.annotation.owner.LifePhaseRegistry;

public class MainActivity extends AppCompatActivity implements ILifePhaseOwner {

    private LifePhaseRegistry mLifePhaseRegistry;
    private MyPresenter2 mPresenter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLifePhaseRegistry = new LifePhaseRegistry();
        MyPresenter presenter=new MyPresenter();
        mPresenter2 = new MyPresenter2();
        mLifePhaseRegistry.addObserver(presenter);
        mLifePhaseRegistry.addObserver(mPresenter2);
        mLifePhaseRegistry.handleLifecycleEvent(LifePhaseEvent.onCreate);
    }

    @Override
    public LifePhaseOwner getLifePhase() {
        return mLifePhaseRegistry;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLifePhaseRegistry.handleLifecycleEvent(LifePhaseEvent.onStart);
        mLifePhaseRegistry.removeObserver(mPresenter2);
    }
}

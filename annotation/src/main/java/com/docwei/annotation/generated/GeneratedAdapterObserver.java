package com.docwei.annotation.generated;

import com.docwei.annotation.LifePhaseEvent;
import com.docwei.annotation.LifePhaseEventObserver;


//装饰器设计模式
public class GeneratedAdapterObserver implements LifePhaseEventObserver {
    private final GeneratedAdapter mGeneratedAdapters;

    public GeneratedAdapterObserver(GeneratedAdapter generatedAdapter) {
        mGeneratedAdapters = generatedAdapter;
    }
    @Override
    public void onStateChanged(LifePhaseEvent event) {
        mGeneratedAdapters.callMethod(event);
    }
}

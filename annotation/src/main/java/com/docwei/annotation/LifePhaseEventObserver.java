package com.docwei.annotation;

/**
 * 这个接口给 观察者的包装类使用
 */
public interface LifePhaseEventObserver extends LifePhaseObserver{
    void onStateChanged(LifePhaseEvent event);
}

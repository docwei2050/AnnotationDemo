package com.docwei.annotation.owner;

import com.docwei.annotation.LifePhaseEvent;
import com.docwei.annotation.LifePhaseEventObserver;
import com.docwei.annotation.LifePhaseObserver;
import com.docwei.annotation.LifePhasing;

import java.util.ArrayList;

/**
 * 被观察者实际干活的类
 */
public class LifePhaseRegistry extends LifePhaseOwner{
    public ArrayList<ObserverWrapper> mList = new ArrayList<>();
    @Override
    public void addObserver(LifePhaseObserver lifePhaseObserver) {
        if (lifePhaseObserver == null || mList.contains(lifePhaseObserver)) {
            return;
        }
        //添加进来的时候就需要分两种情况处理
        mList.add(new ObserverWrapper(lifePhaseObserver));
    }

    @Override
    public void removeObserver(LifePhaseObserver lifePhaseObserver) {
        if (lifePhaseObserver == null) {
            return;
        }
        for(ObserverWrapper observerWrapper:mList ){
            if(observerWrapper.mObserver==lifePhaseObserver){
                mList.remove(observerWrapper);
                return;
            }
        }
    }

    public void handleLifecycleEvent(LifePhaseEvent event) {
        for (ObserverWrapper observer : mList) {
            observer.dispatchEvent(event);
        }
    }
    static class ObserverWrapper {

        LifePhaseEventObserver mLifePhaseObserver;
        LifePhaseObserver mObserver;

        ObserverWrapper(LifePhaseObserver observer) {
            //一个观察者包装的目的是确认走反射还是走apt，并且是反射的话就在此预先添加到map保存起来。
            mLifePhaseObserver = LifePhasing.lifecycleEventObserver(observer);
            mObserver=observer;
        }

        void dispatchEvent(LifePhaseEvent event) {
            mLifePhaseObserver.onStateChanged(event);
        }
    }
}

package com.docwei.annotation.reflection;

import com.docwei.annotation.LifePhaseEvent;
import com.docwei.annotation.LifePhaseEventObserver;

public class ReflectAdapterObserver implements LifePhaseEventObserver {
    private final ClassesInfoCache.CallBackInfo mInfo;
    private Object target;

    public ReflectAdapterObserver(Object wrapped) {
        target=wrapped;
        mInfo = ClassesInfoCache.sInstance.getInfos(wrapped.getClass());
    }

    @Override
    public void onStateChanged(LifePhaseEvent event) {
        mInfo.invokeCallbacks(event,target);
    }
}

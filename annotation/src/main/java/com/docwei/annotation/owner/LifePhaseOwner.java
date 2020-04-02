package com.docwei.annotation.owner;

import com.docwei.annotation.LifePhaseObserver;

import java.util.ArrayList;

/**
 * 被观察者/被订阅者
 * 拥有生命周期
 * 职责是 管理订阅者+分发事件
 */
public abstract class LifePhaseOwner {

    public abstract void addObserver(LifePhaseObserver lifePhaseObserver);

    public abstract void removeObserver(LifePhaseObserver lifePhaseObserver);


}

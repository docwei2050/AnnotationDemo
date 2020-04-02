package com.docwei.annotation.owner;

/**
 * 被观察者的抽象出来的类尽量用接口，因为java单继承多实现的缘故
 */
public interface ILifePhaseOwner {
    LifePhaseOwner getLifePhase();
}

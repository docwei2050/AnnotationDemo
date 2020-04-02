package com.docwei.annotation.generated;

import com.docwei.annotation.LifePhaseEvent;


//声明接口的原因是这个接口的子类是所有apt生成的类，都具有一个callMethod方法
public interface GeneratedAdapter {
    void callMethod(LifePhaseEvent event);
}

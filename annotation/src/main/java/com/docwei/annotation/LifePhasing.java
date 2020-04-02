package com.docwei.annotation;

import com.docwei.annotation.generated.GeneratedAdapter;
import com.docwei.annotation.generated.GeneratedAdapterObserver;
import com.docwei.annotation.reflection.ClassesInfoCache;
import com.docwei.annotation.reflection.ReflectAdapterObserver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class LifePhasing {
    public static LifePhaseEventObserver lifecycleEventObserver(LifePhaseObserver lifePhaseObserver) {

        //默认先走走apt apt失败再走反射
        //从lifePhaseObserver获取到对应的apt编译生成的GeneratedAdapter，然后创建GeneratedAdapterObserver对象
        Constructor<? extends GeneratedAdapter> constructor=generatedConstructor(lifePhaseObserver.getClass());
        if(constructor!=null){
            try {
                return new GeneratedAdapterObserver(constructor.newInstance(lifePhaseObserver));
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }



        //判断走反射还是走Generated
        if (ClassesInfoCache.sInstance.hasLifecycleMethods(lifePhaseObserver.getClass())) {
            return new ReflectAdapterObserver(lifePhaseObserver);
        }
        return null;
    }


    private static Constructor<? extends GeneratedAdapter> generatedConstructor(Class<?> klass) {
        //本身的LifePhaseObserver：com.docwei.annotationdemo.MyPresenter
        //编译生成的类： com.docwei.annotationdemo.MyPresenter2_LifePhaseAdapter
        try {
            Package packageQ = klass.getPackage();
            if (packageQ != null) {
                String packageName = packageQ.getName();
                String className = klass.getSimpleName();
                String newClassPath = packageName + "." + getAdapterName(className);
                //调用
                Constructor<? extends GeneratedAdapter> constructor =
                        (Constructor<? extends GeneratedAdapter>) Class.forName(newClassPath).getDeclaredConstructor(klass);
                if (!constructor.isAccessible()) {
                    constructor.setAccessible(true);
                }
                return constructor;
            }
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        } catch (NoSuchMethodException e) {
            // this should not happen
            throw new RuntimeException(e);
        }

    }

    public static String getAdapterName(String className) {
        return className + "_LifePhaseAdapter";
    }
}

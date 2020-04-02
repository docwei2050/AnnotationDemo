package com.docwei.annotation.reflection;

import com.docwei.annotation.LifePhaseEvent;
import com.docwei.annotation.OnLifePhaseEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//反射处理类
public class ClassesInfoCache {
    public static ClassesInfoCache sInstance = new ClassesInfoCache();
    private final Map<Class<?>, Boolean> mHasLifecycleMethods = new HashMap<>();
    private final Map<Class<?>, CallBackInfo> mCallbackMap = new HashMap<>();

    //判断是否能走反射
    public boolean hasLifecycleMethods(Class klass) {
        Boolean hasLifecycleMethods = mHasLifecycleMethods.get(klass);
        if (hasLifecycleMethods != null) {
            return hasLifecycleMethods;
        }

        Method[] methods = klass.getDeclaredMethods();
        for (Method method : methods) {
            OnLifePhaseEvent annotation = method.getAnnotation(OnLifePhaseEvent.class);
            if (annotation != null) {
                createInfo(klass, methods);
                return true;
            }
        }
        mHasLifecycleMethods.put(klass, false);
        return false;

    }

    private void createInfo(Class klass, Method[] methods) {
        //把这个类所有的关于注解事件的方法全部归类
        //一个类中，可能有多个注解的方法，注解事件是同一个。
        Map<LifePhaseEvent, List<Method>> eventList = new HashMap<>();
        for (Method method : methods) {
            OnLifePhaseEvent annotation = method.getAnnotation(OnLifePhaseEvent.class);
            if (annotation == null) {
                continue;
            }
            LifePhaseEvent event = annotation.value();
            if (eventList.containsKey(event)) {
                List<Method> methodList = eventList.get(event);
                methodList.add(method);
            } else {
                List<Method> methodList = new ArrayList<>();
                methodList.add(method);
                eventList.put(event, methodList);
            }
        }
        CallBackInfo callBackInfo = new CallBackInfo(eventList);
        mCallbackMap.put(klass, callBackInfo);
    }


   public  static  class CallBackInfo{
       public Map<LifePhaseEvent, List<Method>> mEventToHandlers;
       public CallBackInfo(Map<LifePhaseEvent, List<Method>> eventToHandlers){
           mEventToHandlers=eventToHandlers;
       }
       public void invokeCallbacks(LifePhaseEvent event, Object target){
           if ( mEventToHandlers != null) {
               List<Method> list=mEventToHandlers.get(event);
               for (int i =  list.size() - 1; i >= 0; i--) {
                   try {
                       list.get(i).invoke(target);
                   } catch (IllegalAccessException e) {
                       e.printStackTrace();
                   } catch (InvocationTargetException e) {
                       e.printStackTrace();
                   }
               }
           }
       }
    }
    public CallBackInfo getInfos(Class kclass){
        return mCallbackMap.get(kclass);
    }
}

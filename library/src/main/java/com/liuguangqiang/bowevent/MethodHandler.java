package com.liuguangqiang.bowevent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Eric on 15/10/8.
 */
public class MethodHandler {

    private Object target;

    private Method method;

    public MethodHandler(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    public void invoke(Object parameter) {
        try {
            method.invoke(target, parameter);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}

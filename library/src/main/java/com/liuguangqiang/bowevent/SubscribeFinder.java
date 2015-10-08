package com.liuguangqiang.bowevent;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Eric on 15/10/8.
 */
public final class SubscribeFinder {

    private static final String TAG = "BowEvent";

    private static final HashMap<Class<?>, HashMap<Class<?>, Set<Method>>> CACHE = new HashMap<>();

    private static HashMap<Class<?>, Set<Method>> get(Class<?> clazz) {
        return CACHE.get(clazz);
    }

    private static void put(Class<?> clazz, HashMap<Class<?>, Set<Method>> methods) {
        CACHE.put(clazz, methods);
    }

    /**
     * Find all methods subscribed.
     */
    public static HashMap<Class<?>, Set<MethodHandler>> findSubscribedMethods(Object target) {
        Class<?> targetClass = target.getClass();
        HashMap<Class<?>, Set<MethodHandler>> handlers = new HashMap<>();
        HashMap<Class<?>, Set<Method>> methods = get(targetClass);

        if (methods == null) {
            methods = findSubscribedMethods(targetClass);
            put(targetClass, methods);
        }

        //load all method events.
        for (Map.Entry<Class<?>, Set<Method>> entry : methods.entrySet()) {
            Set<MethodHandler> handlerSet = new HashSet<>();
            for (Method method : entry.getValue()) {
                handlerSet.add(new MethodHandler(target, method));
            }
            if (!handlerSet.isEmpty()) {
                handlers.put(entry.getKey(), handlerSet);
            }
        }

        return handlers;
    }

    /**
     * Find all methods annotated with {@link Subscribe}
     */
    private static HashMap<Class<?>, Set<Method>> findSubscribedMethods(Class<?> targetClass) {
        HashMap<Class<?>, Set<Method>> subscriberMethods = new HashMap<>();
        Method[] declaredMethods = targetClass.getDeclaredMethods();

        for (Method method : declaredMethods) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                Log.i(TAG, "subscribed : " + method.toString());

                Class<?>[] parameterTypes = method.getParameterTypes();
                Class<?> paramType = parameterTypes[0];

                Log.i(TAG, "paramType : " + paramType);

                Set<Method> methodSet = subscriberMethods.get(paramType);
                if (methodSet == null) {
                    methodSet = new HashSet<>();
                    subscriberMethods.put(paramType, methodSet);
                }
                methodSet.add(method);
            }
        }
        return subscriberMethods;
    }

}
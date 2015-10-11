/*
 *  Copyright 2015 Eric Liu
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
        loadMethods(subscriberMethods, targetClass);
        return subscriberMethods;
    }

    private static void loadMethods(HashMap<Class<?>, Set<Method>> subscriberMethods, Class<?> targetClass) {
        Method[] declaredMethods = targetClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                Log.i(TAG, "Subscribe method : " + method);
                Class<?>[] parameterTypes = method.getParameterTypes();
                Class<?> type = parameterTypes[0];

                Set<Method> methodSet = subscriberMethods.get(type);
                if (methodSet == null) {
                    methodSet = new HashSet<>();
                    subscriberMethods.put(type, methodSet);
                }
                methodSet.add(method);
            }
        }
        Class<?> superClass = targetClass.getSuperclass();
        if (!isSystemClass(superClass)) {
            loadMethods(subscriberMethods, superClass);
        }
    }

    private static boolean isSystemClass(Class<?> clazz) {
        String name = clazz.getName();
        if (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android."))
            return true;
        return false;
    }

}

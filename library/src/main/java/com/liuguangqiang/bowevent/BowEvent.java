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

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by Eric on 15/10/8.
 */
public final class BowEvent {

    private static final String TAG = "BowEvent";

    private static BowEvent instance = new BowEvent();

    private final HashMap<Class<?>, Set<MethodHandler>> handlerMap = new HashMap<>();

    private BowEvent() {
    }

    public static BowEvent getInstance() {
        return instance;
    }

    public void unregister(Object target) {

    }

    public void register(Object target) {
        HashMap<Class<?>, Set<MethodHandler>> methodEvents = SubscribeFinder.findSubscribedMethods(target);

        for (Class<?> type : methodEvents.keySet()) {
            Set<MethodHandler> methodEventSet = methodEvents.get(type);
            handlerMap.put(type, methodEventSet);
        }
    }

    /**
     * Post an event to all methods annotated with {@link Subscribe}.
     *
     * @param event event to post
     */
    public void post(@NonNull Object event) {
        post("", event);
    }

    /**
     * Post an event to all methods annotated with {@link Subscribe} and tagged with {@code tag}.
     *
     * @param tag   subscribed with a tag.
     * @param event event to post
     */
    public void post(@NonNull String tag, @NonNull Object event) {
        if (tag == null) throw new NullPointerException("The tag must not be null.");

        if (event == null) throw new NullPointerException("The event must not be null.");

        Set<Class<?>> classesSet = loadClasses(event.getClass());
        if (classesSet != null && !classesSet.isEmpty()) {
            for (Class<?> clazz : classesSet) {
                dispatch(clazz, tag, event);
            }
        }
    }

    private void dispatch(Class<?> clazz, String tag, Object event) {
        Set<MethodHandler> handlerSet = handlerMap.get(clazz);
        if (handlerSet != null && !handlerSet.isEmpty()) {
            for (MethodHandler handler : handlerSet) {
                dispatch(tag, handler, event);
            }
        }
    }

    private void dispatch(String tag, MethodHandler handler, Object event) {
        if (handler.getTag().equals(tag))
            handler.invoke(event);
    }

    /**
     * Return all supper classes of a class.
     *
     * @param targetClass
     * @return
     */
    private Set<Class<?>> loadClasses(Class<?> targetClass) {
        List<Class<?>> parents = new LinkedList<>();
        Set<Class<?>> classes = new HashSet<>();
        parents.add(targetClass);

        while (!parents.isEmpty()) {
            Class<?> clazz = parents.remove(0);
            classes.add(clazz);

            Class<?> parent = clazz.getSuperclass();
            if (parent != null) {
                parents.add(parent);
            }
        }
        return classes;
    }

}

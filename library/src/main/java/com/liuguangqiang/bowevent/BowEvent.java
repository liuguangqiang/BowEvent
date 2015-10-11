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
import android.util.Log;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Eric on 15/10/8.
 */
public final class BowEvent {

    private static final String TAG = "BowEvent";

    private static BowEvent instance = new BowEvent();

    private HashMap<Class<?>, Set<MethodHandler>> handlerMap;
    private HashMap<Class<?>, Boolean> registerObjects = new HashMap<>();

    private BowEvent() {
        handlerMap = new HashMap<>();
    }

    public static BowEvent getInstance() {
        return instance;
    }

    public void register(Object target) {
        Class<?> targetClass = target.getClass();
        if (!registerObjects.containsKey(targetClass)) {
            registerObjects.put(targetClass, true);
            HashMap<Class<?>, Set<MethodHandler>> methodEvents = SubscribeFinder.findSubscribedMethods(target);

            for (Class<?> type : methodEvents.keySet()) {
                Set<MethodHandler> methodEventSet = methodEvents.get(type);
                if (handlerMap.containsKey(type)) {
                    handlerMap.get(type).addAll(methodEventSet);
                } else {
                    handlerMap.put(type, methodEventSet);
                }
            }
        }
    }

    public void unregister(Object target) {
        Class<?> targetClass = target.getClass();
        registerObjects.remove(targetClass);


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

        dispatch(event.getClass(), tag, event);
    }

    private void dispatch(Class<?> clazz, String tag, Object event) {
        Set<MethodHandler> handlerSet = handlerMap.get(clazz);
        if (handlerSet != null && !handlerSet.isEmpty()) {
            Log.i(TAG, handlerSet.toString());

            for (MethodHandler handler : handlerSet) {
                dispatch(tag, handler, event);
            }
        }
    }

    private void dispatch(String tag, MethodHandler handler, Object event) {
        Log.i(TAG, "dispatch--->" + handler.toString());
        if (handler.getTag().equals(tag))
            handler.invoke(event);
    }

}

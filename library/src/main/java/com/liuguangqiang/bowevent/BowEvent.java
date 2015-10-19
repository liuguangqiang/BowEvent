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
import java.util.Set;

/**
 * Created by Eric on 15/10/8.
 */
public final class BowEvent {

    private static final String TAG = "BowEvent";

    private static BowEvent instance = new BowEvent();

    /**
     * All subscribed methods.
     * <p>
     * key : event type
     * value : method
     */
    private HashMap<Class<?>, Set<MethodHandler>> handlersMap;

    /**
     * All subscribed objects.
     * <p>
     * key : the class of subscriber
     * value : boolean
     */
    private HashMap<Class<?>, Boolean> subscribers;

    private BowEvent() {
        handlersMap = new HashMap<>();
        subscribers = new HashMap<>();
    }

    public static BowEvent getInstance() {
        return instance;
    }

    public boolean isRegistered(@NonNull Object subscriber) {
        return subscribers.containsKey(subscriber.getClass());
    }

    public void register(@NonNull Object subscriber) {
        if (!isRegistered(subscriber)) {
            Class<?> targetClass = subscriber.getClass();
            subscribers.put(targetClass, true);
            HashMap<Class<?>, Set<MethodHandler>> methodEvents = SubscribeFinder.findSubscribedMethods(subscriber);

            for (Class<?> type : methodEvents.keySet()) {
                Set<MethodHandler> methodEventSet = methodEvents.get(type);
                if (handlersMap.containsKey(type)) {
                    handlersMap.get(type).addAll(methodEventSet);
                } else {
                    handlersMap.put(type, methodEventSet);
                }
            }
        }
    }

    public void unregister(@NonNull Object subscriber) {
        if (isRegistered(subscriber)) {
            subscribers.remove(subscriber.getClass());
            Set<MethodHandler> methodHandlerSet;
            Set<MethodHandler> removedHandlers;
            for (Class<?> clazz : handlersMap.keySet()) {
                methodHandlerSet = handlersMap.get(clazz);
                removedHandlers = new HashSet<>();
                for (MethodHandler handler : methodHandlerSet) {
                    if (handler.getSubscriber().equals(subscriber)) {
                        removedHandlers.add(handler);
                    }
                }
                methodHandlerSet.removeAll(removedHandlers);
            }
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

        dispatch(event.getClass(), tag, event);
    }

    private void dispatch(Class<?> clazz, String tag, Object event) {
        Set<MethodHandler> handlers = handlersMap.get(clazz);
        if (handlers != null && !handlers.isEmpty()) {
            for (MethodHandler handler : handlers) {
                dispatch(tag, handler, event);
            }
        }
    }

    private void dispatch(String tag, MethodHandler handler, Object event) {
        if (handler.getTag().equals(tag))
            handler.invoke(event);
    }

}

/*
 *  Copyright 2015-2017 Eric Liu
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
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Eric on 15/10/8.
 */
public final class BowEvent {

  private static final String TAG = "BowEvent";

  private static volatile BowEvent instance;

  /**
   * All subscribed methods.
   * <p>
   * key : the type of a event, like "com.liuguangqiang.bowevent.sample.event.TestEvent"
   * value : the subscribed method handler.
   */
  private HashMap<Class<?>, Set<MethodHandler>> subscribedMethodHandlers;

  /**
   * All subscribed objects.
   * <p>
   * key : the class of subscriber, like "com.liuguangqiang.bowevent.sample.MainActivity"
   * value : boolean
   */
  private HashMap<Class<?>, Boolean> subscribers;

  private BowEvent() {
    subscribedMethodHandlers = new HashMap<>();
    subscribers = new HashMap<>();
  }

  /**
   * Return the global instance.
   */
  public static BowEvent getInstance() {
    if (instance == null) {
      synchronized (BowEvent.class) {
        instance = new BowEvent();
      }
    }
    return instance;
  }

  public boolean isRegistered(@NonNull Object subscriber) {
    return subscribers.containsKey(subscriber.getClass());
  }

  public void register(@NonNull Object subscriber) {
    if (!isRegistered(subscriber)) {
      Class<?> targetClass = subscriber.getClass();
      subscribers.put(targetClass, true);
      findSubscribedMethods(subscriber);
    }
  }

  private void findSubscribedMethods(Object subscriber) {
    //Subscribed methods.
    HashMap<Class<?>, Set<MethodHandler>> methodHandlers = SubscribeFinder
        .findSubscribedMethods(subscriber);

    for (Class<?> eventType : methodHandlers.keySet()) {
      if (subscribedMethodHandlers.containsKey(eventType)) {
        subscribedMethodHandlers.get(eventType).addAll(methodHandlers.get(eventType));
      } else {
        subscribedMethodHandlers.put(eventType, methodHandlers.get(eventType));
      }
    }
  }

  public void unregister(@NonNull Object subscriber) {
    if (isRegistered(subscriber)) {
      subscribers.remove(subscriber.getClass());

      for (Class<?> clazz : subscribedMethodHandlers.keySet()) {
        unregister(subscribedMethodHandlers.get(clazz), subscriber);
      }
    }
  }

  private void unregister(@NonNull Set<MethodHandler> methodHandlers, @NonNull Object subscriber) {
    Iterator<MethodHandler> iterator = methodHandlers.iterator();
    while (iterator.hasNext()) {
      if (iterator.next().getSubscriber().equals(subscriber)) {
        iterator.remove();
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
   * @param tag subscribed with a tag.
   * @param event event to post
   */
  public void post(@NonNull String tag, @NonNull Object event) {
    if (tag == null) {
      throw new NullPointerException("The tag must not be null.");
    }

    if (event == null) {
      throw new NullPointerException("The event must not be null.");
    }

    dispatch(event.getClass(), tag, event);
  }

  private void dispatch(Class<?> clazz, String tag, Object event) {
    Set<MethodHandler> handlers = subscribedMethodHandlers.get(clazz);
    if (handlers != null && !handlers.isEmpty()) {
      for (MethodHandler handler : handlers) {
        dispatch(tag, handler, event);
      }
    }
  }

  private void dispatch(String tag, MethodHandler handler, Object event) {
    if (handler.getTag().equals(tag)) {
      handler.invoke(event);
    }
  }

}

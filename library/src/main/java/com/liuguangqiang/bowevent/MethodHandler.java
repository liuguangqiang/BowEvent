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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A container for keeping the subscriber and the method.
 *
 * Created by Eric on 15/10/8.
 */
public class MethodHandler {

  /**
   * A object that hold the method.
   */
  private Object subscriber;

  /**
   * The method will be invoked.
   */
  private Method method;

  /**
   * If the tag is not null or empty , the method just only be invoked with a specific tag.
   */
  private String tag;

  public MethodHandler(Object subscriber, Method method) {
    if (subscriber == null) {
      throw new NullPointerException("the subscriber must not be null.");
    }

    if (method == null) {
      throw new NullPointerException("the method must not be null.");
    }

    this.subscriber = subscriber;
    this.method = method;

    Subscribe subscribe = method.getAnnotation(Subscribe.class);
    if (subscribe != null) {
      tag = subscribe.tag();
    }
  }

  public Object getSubscriber() {
    return subscriber;
  }

  /**
   * Return the tag of handler.
   */
  public String getTag() {
    return tag;
  }

  /**
   * Invoke the method.
   *
   * @param arg the arguments to the method
   */
  public void invoke(Object arg) {
    try {
      method.invoke(subscriber, arg);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String toString() {
    return "MethodHandler{" +
        "subscriber=" + subscriber +
        ", method=" + method +
        ", tag='" + tag + '\'' +
        '}';
  }

}

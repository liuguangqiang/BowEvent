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
 * Created by Eric on 15/10/8.
 */
public class MethodHandler {

    /**
     * A object that hold the method.
     */
    private Object target;

    /**
     * Handler method
     */
    private Method method;

    /**
     * If the tag is not null or empty , the method just only be invoked with a correct tag.
     */
    private String tag;

    public MethodHandler(Object target, Method method) {
        if (target == null) throw new NullPointerException("the target must not be null.");

        if (method == null) throw new NullPointerException("the method must not be null.");

        this.target = target;
        this.method = method;

        Subscribe subscribe = method.getAnnotation(Subscribe.class);
        if (subscribe != null) {
            tag = subscribe.tag();
        }
    }

    /**
     * Return the tag of handler.
     *
     * @return
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
            method.invoke(target, arg);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "MethodHandler{" +
                "target=" + target +
                ", method=" + method +
                ", tag='" + tag + '\'' +
                '}';
    }

}

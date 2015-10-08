package com.liuguangqiang.bowevent;

import android.util.Log;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Eric on 15/10/8.
 */
public final class BowEvent {

    private static final String TAG = "BowEvent";

    private static BowEvent instance = new BowEvent();

    private HashMap<Class<?>, Set<MethodHandler>> handlerMap = new HashMap<>();

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
            Log.i(TAG, "listening type : " + type.toString());

            Set<MethodHandler> methodEventSet = methodEvents.get(type);
            handlerMap.put(type, methodEventSet);
        }
    }

    public void post(Object object) {
        Log.i(TAG, "post type : " + object.getClass());

        Set<MethodHandler> handlerSet = handlerMap.get(object.getClass());
        if (handlerSet != null && !handlerSet.isEmpty()) {
            for (MethodHandler handler : handlerSet) {
                handler.invoke(object);
            }
        }
    }

}

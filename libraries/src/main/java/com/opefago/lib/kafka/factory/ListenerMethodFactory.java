package com.opefago.lib.kafka.factory;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static com.opefago.lib.ReflectionUtils.getMethodsAnnotatedWith;


public class ListenerMethodFactory {
    private static final Map<Class<? extends Annotation>, Set<Pair<Method, Object>>> methodListenersFactory
            = new HashMap<>();
    public static  Set<Pair<Method, Object>> getMethodListeners(
            final Set<Object> objects,
            final Class<? extends Annotation> annotation
    ){
        synchronized (methodListenersFactory){

            Set<Pair<Method, Object>> listeners = methodListenersFactory.get(annotation);
            if(listeners == null){
                listeners = initialiseListenerMethods(objects, annotation);
                methodListenersFactory.put(annotation, listeners);
            }
            return listeners;
        }
    }

    private static  Set<Pair<Method, Object>> initialiseListenerMethods(
            final Set<Object> objects,
            final Class<? extends Annotation> annotation
    ) {
        Set<Pair<Method, Object>> found = new HashSet<>();
        objects.forEach(o -> {
            List<Method> methods = getMethodsAnnotatedWith(o.getClass(), annotation);
            methods.forEach(method -> found.add(new ImmutablePair<>(method, o)));
        });
        return found;
    }
}

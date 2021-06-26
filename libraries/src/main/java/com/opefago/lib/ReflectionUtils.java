package com.opefago.lib;

import com.opefago.lib.kafka.AnnotationWalker;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReflectionUtils {

    public static List<Method> getMethodsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
        return getMethodsAnnotatedWith(type,annotation, null);
    }

    public static List<Method> getMethodsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation, AnnotationWalker walker) {
        final List<Method> methods = new ArrayList<Method>();
        Class<?> klass = type;
        while (klass != Object.class) { // need to iterated thought hierarchy in order to retrieve methods from above the current instance
            // iterate though the list of methods declared in the class represented by klass variable, and add those annotated with the specified annotation
            for (final Method method : klass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotation)) {
                    methods.add(method);
                    if(walker != null){

                        walker.match(method.getAnnotation(annotation), method);
                    }
                }
            }
            // move to the upper class in the hierarchy in search for more methods
            klass = klass.getSuperclass();
        }
        return methods;
    }

    public static Set<Method> getMethodsAnnotatedWith(
            final String packageName,
            final Class<? extends Annotation> annotation)
    {
        Reflections reflections = new Reflections(
                new ConfigurationBuilder().setUrls(
                        ClasspathHelper.forPackage( packageName ) ).setScanners(
                        new MethodAnnotationsScanner() ) );
        return reflections.getMethodsAnnotatedWith(annotation);
    }


    public static <T> Set<Class<? extends T>> getClassesOfType(
            final String packageName,
            final Class<T> subType)
    {
        Reflections reflections = new Reflections(packageName);
        return reflections.getSubTypesOf(subType);
    }

    public static Set<Class<?>> getClassesAnnotatedWith(
            final String packageName,
            final Class<? extends Annotation> annotation)
    {
        return new Reflections(packageName).getTypesAnnotatedWith(annotation);
    }


}

package com.opefago.lib.kafka;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface AnnotationWalker {
    void match(final Annotation annotation, Method method);
}

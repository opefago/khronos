package com.opefago.lib.idempotence;
import com.opefago.lib.cache.AnnotationCache;
import com.opefago.lib.idempotence.model.IMessage;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class IdempotentInterceptor implements MethodInterceptor {
    Logger logger = LoggerFactory.getLogger(IdempotentInterceptor.class);
    @Inject
    public AnnotationCache<Object, Object> idempotentCache;

    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
        Object[] arguments = methodInvocation.getArguments();
        if(arguments.length != 2){
            return null;
        }
        Object proceed;
        try {
            if (arguments[1] instanceof IMessage) {
                final IMessage message;
                final String messageHandle = (String) arguments[0];
                message = (IMessage) arguments[1];

                if (idempotentCache.get(message.getId()) != null) {
                    logger.info("Message with id {} has already been processed", message.getId());
                    handleDuplicateAction(methodInvocation, messageHandle);
                    return null;
                }
                proceed = methodInvocation.proceed();
                idempotentCache.put(message.getId(), message.serialize());
                return proceed;
            }
        }catch (Exception e){
            logger.info("Exception {} returned for interceptor", e.getMessage());
        }
        return null;
    }

    private void handleDuplicateAction(final MethodInvocation methodInvocation, final String argument){
        Class<?> parentClass = methodInvocation.getMethod().getDeclaringClass();
        Idempotent idempotent = methodInvocation.getMethod().getAnnotation(Idempotent.class);
        if(idempotent.actionOnDuplicateMethod().equals("")){
            return;
        }
        try {
            Method fallback = parentClass.getMethod(idempotent.actionOnDuplicateMethod(), String.class);
            fallback.invoke(methodInvocation.getThis(), argument);
        } catch (NoSuchMethodException | IllegalAccessException |InvocationTargetException e) {
            e.printStackTrace();
            logger.info("Exception {} returned for interceptor", e.getMessage());
        }
    }
}

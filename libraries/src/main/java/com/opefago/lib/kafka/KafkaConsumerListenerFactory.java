package com.opefago.lib.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opefago.lib.cache.AnnotationCache;
import com.opefago.lib.idempotence.model.IMessage;
import com.opefago.lib.kafka.annotations.IdempotentKafkaTopicListener;
import com.opefago.lib.kafka.annotations.KafkaTopicListener;
import com.opefago.lib.kafka.factory.ListenerMethodFactory;
import com.opefago.lib.util.MapperUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.opefago.lib.ReflectionUtils.getMethodsAnnotatedWith;


@Singleton
public class KafkaConsumerListenerFactory {
    private final Logger logger = LoggerFactory.getLogger(KafkaConsumerListenerFactory.class);
    private final ExecutorService consumers = Executors.newCachedThreadPool();
    private long pollTimeout = 1000L;
    private final KafkaConsumerConfiguration kafkaConsumerConfiguration;
    private final Set<Object> listeners;
    private final Map<String, Class<?>> methodObjectMap = new HashMap<>();
    public final AnnotationCache<String, Object> idempotentCache;

    @Inject
    public KafkaConsumerListenerFactory(
            final KafkaConsumerConfiguration kafkaConsumerConfiguration,
            final Set<Object> listeners,
            final AnnotationCache<String, Object> idempotentCache
    ){
        this.kafkaConsumerConfiguration = kafkaConsumerConfiguration;
        this.listeners = listeners;
        this.idempotentCache = idempotentCache;
    }

    public void start(){
        startConsumer(getProperty());
    }

    public void stop(){
        consumers.shutdown();
    }

    public void setPollTimeout(long timeout){
        this.pollTimeout = timeout;
    }

    private Properties getProperty(){
        final Properties props = new Properties();
        props.put("bootstrap.servers", kafkaConsumerConfiguration.getBootstrapServer());
        props.put("group.id", kafkaConsumerConfiguration.getConsumerGroupId());
        props.put("enable.auto.commit", kafkaConsumerConfiguration.isAutoCommitEnabled());
        props.put("auto.commit.interval.ms", (int)kafkaConsumerConfiguration.getAutoCommitInterval().toMilliseconds());
        props.put("max.poll.records", kafkaConsumerConfiguration.getMaxPollRecords());
        props.put("max.poll.interval.ms", (int)kafkaConsumerConfiguration.getMaxPollInterval().toMilliseconds());
        props.put("send.buffer.bytes", kafkaConsumerConfiguration.getSendBufferBytes());
        props.put("receive.buffer.bytes", kafkaConsumerConfiguration.getReceiveBufferBytes());
        props.put("key.deserializer", kafkaConsumerConfiguration.getDeserializer());
        props.put("value.deserializer", kafkaConsumerConfiguration.getDeserializer());
        props.put("value.deserializer.class", kafkaConsumerConfiguration.getDeserializer());
        return props;
    }

    private boolean isIdempotent(final Method method){
        return method.getAnnotation(IdempotentKafkaTopicListener.class) != null;
    }

    private void startConsumer(final Properties properties){
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(getTopics(listeners));
        consumers.execute(()->{
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(pollTimeout));
                for (ConsumerRecord<String, String> record : records) {
                    List<Pair<Method, Object>> allMethods =
                            getMethodsAnnotatedWithTopic(record.topic());
                    allMethods.forEach(m-> {
                        try {
                            proceed(m, record);
                        } catch (InvocationTargetException | IllegalAccessException e) {
                            logger.error("Invoking Kafka listener returned error {}", e.getMessage(), e);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
    }

    private void proceed(Pair<Method, Object> m, ConsumerRecord<String, String> record) throws JsonProcessingException, InvocationTargetException, IllegalAccessException {
        final Object origin = MapperUtil.INSTANCE.getObjectMapper().readValue(
                record.value(),
                methodObjectMap.get(record.topic()));
        if(isIdempotent(m.getKey())){
            if(origin instanceof IMessage){
                final String key = String.format(
                        "%s:%s",
                        getTopic(m.getKey()),
                        ((IMessage)origin).getId()
                );
                Object id = idempotentCache.get(key);
                if(id != null){
                    logger.info("Duplicate transaction detected{}", origin);
                    return;
                }
            }
        }
        m.getKey().invoke(m.getValue(),
                origin
        );
        if(isIdempotent(m.getKey())){
            if(origin instanceof IMessage){
                final String key = String.format(
                        "%s:%s",
                        getTopic(m.getKey()),
                        ((IMessage)origin).getId()
                );
                idempotentCache.put(key, origin);
            }
        }
    }

    private String getTopic(Method m){
        if(m.getAnnotation(KafkaTopicListener.class) != null) {
            return m.getAnnotation(KafkaTopicListener.class).topic();
        }else if(isIdempotent(m)){
            return m.getAnnotation(IdempotentKafkaTopicListener.class).topic();
        }
        return null;
    }

    private List<String> getTopics(final Set<Object> listeners){
        List<Method> methods = new ArrayList<>();
        listeners.forEach(o -> {
            methods
                    .addAll(
                            getMethodsAnnotatedWith(
                                    o.getClass(),
                                    KafkaTopicListener.class,
                                    (annotation, method) -> methodObjectMap.put(
                                            ((KafkaTopicListener) annotation).topic(),
                                            method.getParameters()[0].getType()
                                    )
                            )
                    );
            methods
                    .addAll(
                            getMethodsAnnotatedWith(
                                    o.getClass(),
                                    IdempotentKafkaTopicListener.class,
                                    (annotation, method) -> methodObjectMap.put(
                                            ((IdempotentKafkaTopicListener) annotation).topic(),
                                            method.getParameters()[0].getType()
                                    )
                            )
                    );
                }
        );
        List<String> topics = new ArrayList<>();
        methods.forEach(m->{
            String topic = getTopic(m);
            if(topic != null){
                topics.add(topic);
            }
        });
        return topics;
    }

    public List<Pair<Method, Object>> getMethodsAnnotatedWithTopic(final String topic) {
        final List<Pair<Method, Object>> methods = new ArrayList<>();
        ListenerMethodFactory
                .getMethodListeners(listeners, KafkaTopicListener.class)
            .forEach(methodPair -> {

                KafkaTopicListener annotInstance = methodPair.getKey().getAnnotation(KafkaTopicListener.class);
                if (annotInstance.topic().equals(topic)) {
                    methods.add(methodPair);
                }
            });
        ListenerMethodFactory
                .getMethodListeners(listeners, IdempotentKafkaTopicListener.class)
                .forEach(methodPair -> {

                    IdempotentKafkaTopicListener annotInstance = methodPair.getKey().getAnnotation(IdempotentKafkaTopicListener.class);
                    if (annotInstance.topic().equals(topic)) {
                        methods.add(methodPair);
                    }
                });
        return methods;
    }
}

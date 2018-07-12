package com.zhenbianshu.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Function;

/**
 * created by zbs on 2018/6/27
 */
public class BatchHandler<E> {

    private static final Logger logger = LoggerFactory.getLogger(BatchHandler.class);

    private static volatile Map<Class, BatchHandler> instance = Maps.newConcurrentMap();

    private volatile List<E> batchContainer = Lists.newLinkedList(); // todo thread safe container

    private volatile long lastCleanTime = System.currentTimeMillis();

    private Function<List<E>, Boolean> cleaner;

    private long interval;

    private int threshHold;

    private BatchHandler(Function<List<E>, Boolean> cleaner, int threshHold, long interval) {
        this.cleaner = cleaner;
        this.threshHold = threshHold;
        this.interval = interval;
        init();
    }

    public void init() {
        // todo thread pool schedule
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("start to run timer clean");
                if (System.currentTimeMillis() - lastCleanTime >= interval) {
                    clean();
                    logger.info("batch handler clean finished");
                }
            }
        }, interval, interval);
    }

    public void submit(E event) {
        batchContainer.add(event);
        if (batchContainer.size() >= threshHold) {
            clean();
        }
    }

    private void clean() {
        // todo thread safe
        List<E> transferList = batchContainer;
        batchContainer = Lists.newLinkedList();
        lastCleanTime = System.currentTimeMillis();

        if (CollectionUtils.isEmpty(transferList)) {
            return;
        }

        try {
            cleaner.apply(transferList);
        } catch (Exception e) {
            logger.error("batch execute error, transferList:{}", transferList, e);
        }
    }

    public static <E> BatchHandler getInstance(Function<List<E>, Boolean> cleaner, int threshHold, long interval) {

        Class jobClass = cleaner.getClass();
        if (instance.get(jobClass) == null) {
            synchronized (BatchHandler.class) {
                if (instance.get(jobClass) == null) {
                    instance.put(jobClass, new BatchHandler<>(cleaner, threshHold, interval));
                }
            }
        }

        return instance.get(jobClass);
    }

    protected void finalize() {
        clean();
    }
}

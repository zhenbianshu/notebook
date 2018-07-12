package com.zhenbianshu.collapser;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Future;

/**
 * created by zbs on 2018/7/10
 */
@Service
public class HystrixCollapseService {

    @HystrixCollapser(batchMethod = "getBatch",
            scope = com.netflix.hystrix.HystrixCollapser.Scope.GLOBAL,
            collapserProperties = {
                    @HystrixProperty(name = "maxRequestsInBatch", value = "100"),
                    @HystrixProperty(name = "timerDelayInMilliseconds", value = "3000")
            })
    public Future<String> getSingle(String a) {
        System.out.println("single");
        return null;
    }

    @HystrixCommand
    public List<String> getBatch(List<String> list) {
        System.out.println("batch");
        return list;
    }

    public static void main(String[] args) {
        HystrixCollapseService testService = new HystrixCollapseService();
        HystrixRequestContext hystrixRequestContext = HystrixRequestContext.initializeContext();


        testService.getSingle("a");
        testService.getSingle("a");
        testService.getSingle("a");

        hystrixRequestContext.shutdown();
    }
}
/*
  Defined a hystrixAspect spring bean and import hystrix-strategy.xml;
  Annotate single method with @Hystrix Collapser annotate batch method with @HystrixCommand;
  Make your single method need 1 parameter(ArgType) return Future , batch method need List return List and make sure size of args be equal to size of return.
  Set hystrix properties batchMethod, scope, if you want to collapse requests from multiple user threads, you must set the scope to GLOBAL;
  Before you submit a single request, you must init the hystrix context with HystrixRequestContext.initializeContext(), and shutdown the context when your request finish;
  <p>
  bean:
  <aop:aspectj-autoproxy />
  <bean id="hystrixAspect" class="com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect"/>
  <import resource="classpath:spring/hystrix-strategy.xml"/>
 */

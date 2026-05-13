package com.itzixi;

import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

/**
 * StopWatch 使用示例测试。
 */
public class MyTest {

    @Test
    public void testStopWatch() throws Exception {
        // 创建 StopWatch 并模拟多个任务耗时。
        StopWatch stopWatch = new StopWatch();

        stopWatch.start("task1");
        Thread.sleep(500);
        stopWatch.stop();

        stopWatch.start("task2");
        Thread.sleep(800);
        stopWatch.stop();

        stopWatch.start("task3");
        Thread.sleep(300);
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());
        System.out.println(stopWatch.shortSummary());
        System.out.println("所有任务总耗时:" + stopWatch.getTotalTimeMillis());
        System.out.println("任务总数:" + stopWatch.getTaskCount());
    }
}

package Thread;

import com.easy.nettyClient.NettyClientApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author mc
 * @version v1.0
 * Copyright (c) 2021, 芒果听见有限公司 All Rights Reserved.
 * @description
 * @date 2021/6/18
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NettyClientApplication.class)
@Slf4j
public class testThread {

    public class MyThread extends  Thread{
        @Override
        public void run() {
            System.out.println("I'm a child task");
        }
    }

    public class RunnableTask implements  Runnable{
        @Override
        public void run() {
            System.out.println("I'm a child task(runnable)");
        }
    }

    public class CallerTask implements Callable<String>{

        @Override
        public String call() throws Exception {
            System.out.println("im a futureTask");
            return "hello";
        }
    }

    @Test
    public void runTask(){
        Thread myThread = new MyThread();
        myThread.start();

        RunnableTask runnableTask = new RunnableTask();
        new Thread(runnableTask).start();
        new Thread(runnableTask).start();

        FutureTask<String> futureTask = new FutureTask<>(new CallerTask());
        new Thread(futureTask).start();

        try {
            String s = futureTask.get();
            System.out.println(s);
        } catch (InterruptedException | ExecutionException e) {
           log.error("futureTask 异常" ,e);
        }

    }
}

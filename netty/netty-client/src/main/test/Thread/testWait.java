package Thread;

import com.easy.nettyClient.NettyClientApplication;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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
public class testWait {

    BlockingQueue blockingQueue = new ArrayBlockingQueue(20);

    public synchronized void push(int a)  {
        while (blockingQueue.size()>10){
            try {
                this.wait();
            } catch (InterruptedException e) {

            }
        }
        System.out.println(a);
        blockingQueue.add(a);
        this.notifyAll();
    }

    public synchronized int pull()  {
        Object take = null;
        try {
            while (blockingQueue.size()==0){
                Thread.sleep(10);
                this.wait();
            }
            System.out.println("size"+blockingQueue.size());
            take = blockingQueue.take();
            System.out.println("size"+blockingQueue.size());
            this.notifyAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return (int) take;
    }

    @Test
    public void runTest() throws InterruptedException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 200; i++) {
                    push(i);
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    int pull = pull();
                    System.out.println("pull:" + pull);
                }
            }
        }).start();
        Thread.sleep(30000);
    }

    @Test
    public void testNotify() throws InterruptedException {
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                synchronized (blockingQueue){
//                     测试 就没写 while了
                        blockingQueue.wait();
                }
                System.out.println("hello");
            }
        }).start();

        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                synchronized (blockingQueue){
                    Thread.sleep(30000);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                synchronized (blockingQueue){
                   blockingQueue.notify();
                }
            }
        }).start();

        Thread.sleep(40000);
    }
}

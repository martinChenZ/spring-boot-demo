package bufer;

import com.easy.nettyClient.NettyClientApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author mc
 * @version v1.0
 * Copyright (c) 2021, 芒果听见有限公司 All Rights Reserved.
 * @description   缓冲区 测试
 * @date 2021/6/11
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NettyClientApplication.class)
@Slf4j
public class test001 {

    @Test
    public void testBufferAttr() throws  Exception{
        log.info("test Buffer attr start...");

        FileInputStream fIn = new FileInputStream("C:\\Users\\HP\\Documents\\doc\\spring-boot-demo\\netty\\netty-client\\src\\main\\test\\bufer/tom.txt");
        FileChannel fc = fIn.getChannel();

//        也可以包装现有的wrap
//        byte[] bytes = new byte[10];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        ByteBuffer buffer = ByteBuffer.allocate(10);
        outPut("初始化" , buffer);

        fc.read(buffer);
        outPut("调用 read()" , buffer);

        buffer.flip();
        outPut("调用 flip()" , buffer);

        // 判断有无可读数据
        while (buffer.remaining()>0){
            byte b = buffer.get();
            log.info(String.valueOf((char) b));
        }
        outPut("调用get()" , buffer);

        // 解锁
        buffer.clear();
        outPut("调用clear()" , buffer);

        fIn.close();
    }
    private void outPut(String step , Buffer buffer){
        log.info(step + ":");
        log.info("capacity:"+ buffer.capacity() + ",");
        log.info("position:"+ buffer.position() + ",");
//        flip 之前， 数据操作只能在 position 和 limit 之间
        log.info("limit :" + buffer.limit());
        log.info("\n");

    }

    /**
     * 子缓冲区 slice ? 切片
     */
    @Test
    public void testNodeBuffer(){
        ByteBuffer buf = ByteBuffer.allocate(8);
        for (int i = 0; i < buf.capacity() ; i++) {
            buf.put((byte)i);
        }
        buf.position(3);
        buf.limit(7);
        ByteBuffer slice = buf.slice();

        for (int i = 0; i < slice.capacity() ; i++) {
            byte b = slice.get(i);
            b *= 10;
            slice.put(b);
        }

        buf.position(0);
        buf.limit(buf.capacity());
        while (buf.remaining()>0){
            byte c= buf.get();
            log.info(String.valueOf(c));
        }
    }

    /**
     * 只读缓冲区 ， 与原缓冲区共享数据， 并且随之改变
     */
    @Test
    public void testReadOnlyBuf(){
        ByteBuffer buf = ByteBuffer.allocate(8);
        for (int i = 0; i < buf.capacity() ; i++) {
            buf.put((byte)i);
        }
        ByteBuffer readOnlyBuffer = buf.asReadOnlyBuffer();
        outPut("初始化readonly" , readOnlyBuffer);
        readOnlyBuffer.flip();
        while (readOnlyBuffer.remaining()>0){
            byte c= readOnlyBuffer.get();
            log.info(String.valueOf(c));
        }
        // 只读 ， 写报错 java.nio.ReadOnlyBufferException
//        readOnlyBuffer.put(2, (byte) 21);
        buf.put(2, (byte) 20);
        readOnlyBuffer.flip();
        while (readOnlyBuffer.remaining()>0){
            byte c= readOnlyBuffer.get();
            log.info(String.valueOf(c));
        }
    }

    /**
     * 直接缓冲区 是为加快I/O速度，使用一种特殊方式为其分配内存的缓冲区，JDK文档中的描述为：给定一个直接字节缓冲区，Java虚拟机将尽最大努力直接对它执行本机I/O操作。也就是说，它会在每一次调用底层操作系统的本机I/O操作之前（或之后），尝试避免将缓冲区的内容拷贝到一个中间缓冲区或者从一个中间缓冲区拷贝数据。要分配直接缓冲区，需要调用allocateDirect()方法，而不是allocate()方法，使用方式与普通缓冲区并无区别，如下面的文件所示。
     * @throws Exception
     */
    @Test
    public void testBufferDirect() throws  Exception{
        log.info("test Buffer Direct start...");

        FileInputStream fIn = new FileInputStream("C:\\Users\\HP\\Documents\\doc\\spring-boot-demo\\netty\\netty-client\\src\\main\\test\\bufer/tom.txt");
        FileChannel fc = fIn.getChannel();

        String outFile = "C:\\Users\\HP\\Documents\\doc\\spring-boot-demo\\netty\\netty-client\\src\\main\\test\\bufer/copy.txt";
        FileOutputStream fos = new FileOutputStream(outFile);
        FileChannel foc = fos.getChannel();


//        也可以包装现有的wrap
//        byte[] bytes = new byte[10];
//        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

        while (true){
            buffer.clear();
            int read = fc.read(buffer);
            if(read==-1){
                break;
            }
            buffer.flip();
            foc.write(buffer);
        }
        foc.close();
        fIn.close();
    }

    /**
     * 内存映射
     */
    @Test
    public void  testMappedBuffer() throws IOException {

        RandomAccessFile raf = new RandomAccessFile("C:\\Users\\HP\\Documents\\doc\\spring-boot-demo\\netty\\netty-client\\src\\main\\test\\bufer/tom.txt" , "rw");
        FileChannel channel = raf.getChannel();

        // 把缓存去和 文件系统进行映射关联， 改变缓存去内容， 文件随之改变
        MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, 1024);
        map.put(0, (byte) 97);
        map.put(1023, (byte) 122);

        raf.close();

    }
}

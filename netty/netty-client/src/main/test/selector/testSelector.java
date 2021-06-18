package selector;

import com.easy.nettyClient.NettyClientApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author mc
 * @version v1.0
 * Copyright (c) 2021, 芒果听见有限公司 All Rights Reserved.
 * @description
 * @date 2021/6/11
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = NettyClientApplication.class)
@Slf4j
public class testSelector {

    ByteBuffer buf =ByteBuffer.allocate(1024);
//    Selector selector = getSelector();




    /**
     * 向  selector 注册事件
     * @return
     */
    private Selector getSelector() throws IOException {
        Selector open = Selector.open();

        ServerSocketChannel server = ServerSocketChannel.open();
        server.configureBlocking(false);
        ServerSocket socket = server.socket();
        InetSocketAddress address = new InetSocketAddress(8000);
        socket.bind(address);

        server.register(open, SelectionKey.OP_ACCEPT);

        return open;
    }

    public void listen(){
        log.info(("listen on 8000" ));
        try {
            Selector selector = getSelector();
            while (true) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    process(key);
                }
            }
        }catch (Exception e){
            log.error("listen is error:" , e);
        }
    }

    private void process(SelectionKey key) throws IOException {
        log.info(key.toString());
        Selector selector = getSelector();
//        接受请求
        if(key.isAcceptable()){
            ServerSocketChannel server = (ServerSocketChannel)key.channel();
            SocketChannel accept = server.accept();
            accept.configureBlocking(false);
            server.register(selector,SelectionKey.OP_READ);
        }
        else if (key.isReadable()){
            SocketChannel socket = (SocketChannel)key.channel();
            int read = socket.read(buf);
            if(read >0){
                buf.flip();
                String content = new String(buf.array(), 0, read);
                SelectionKey register = socket.register(selector, SelectionKey.OP_WRITE);
                register.attach(content);
            }else{
                socket.close();
            }
            buf.clear();
        }
        else if (key.isWritable()){
            SocketChannel channel = (SocketChannel)key.channel();
            String attachment = (String)key.attachment();
            ByteBuffer block = ByteBuffer.wrap(("输出内容：" + attachment).getBytes());
            if(block!=null){
                channel.write(block);
            }else {
                channel.close();
            }
        }

    }
}

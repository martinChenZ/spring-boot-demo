package com.easy.nettyClient.test;

import java.nio.IntBuffer;

/**
 * @author mc
 * @version v1.0
 * Copyright (c) 2021, 芒果听见有限公司 All Rights Reserved.
 * @description
 * @date 2021/6/10
 */
public class TestIntBuffer {
    public static void main(String[] args) {
        IntBuffer buf = IntBuffer.allocate(8);
        for (int i = 0; i < buf.capacity() ; i++) {
            int j = 2* (i+1);
            buf.put(j);
        }
//        重设当前buf 限制位置改成当前位置， 当前位置改为0
        buf.flip();
        buf.flip();
        while (buf.hasRemaining()){
            int j = buf.get();
            System.out.print(j + " ");
        }
    }
}

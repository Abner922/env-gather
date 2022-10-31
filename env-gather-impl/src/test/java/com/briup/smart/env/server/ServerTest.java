package com.briup.smart.env.server;


import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

/**
 * @author SDX
 * @create 2022-08-26 11:47
 */

public class ServerTest {
    @Test
    public void testServer() throws Exception {
        Server s = new ServerImpl();
        s.reciver();
    }
    @Test
    public void testClose() throws IOException {
        Socket socket = new Socket("127.0.0.1",9999);
    }
}

package com.briup.smart.env.client;


import com.briup.smart.env.Configuration;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Log;
import org.junit.Test;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

/**
 * @author SDX
 * @create 2022-08-26 11:03
 */

public class ClientImpl implements Client, ConfigurationAware, PropertiesAware {
    String host;
    int port;
    Log log;

    @Override
    public void send(Collection<Environment> c) throws Exception {
//        准备一个socket
        log.info("准备连接"+host+":"+port);
//        InetAddress address = InetAddress.getByName("127.0.0.1");
        Socket socket = new Socket(host,port);
        //2.把对象c通过对象流的形式发送给服务端（对象流）
        OutputStream os = socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        log.info("准备发送数据");
        oos.writeObject(c);
        log.info("已发送数据");
        oos.flush();
        //3.关闭客户端
        oos.close();
        socket.close();
        log.info("客户端任务完成，已关闭");
    }

    @Override
    public void setConfiguration(Configuration configuration) throws Exception {
        this.log = configuration.getLogger();
    }

    @Override
    public void init(Properties properties) throws Exception {
//        this.port = Integer.parseInt(properties.getProperty("port"));
        this.port = Integer.valueOf(properties.getProperty("port"));
        this.host = properties.getProperty("host");
    }
}

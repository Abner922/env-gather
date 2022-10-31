package com.briup.smart.env.server;


import com.briup.smart.env.Configuration;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Log;
import com.briup.smart.env.util.LogImpl;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

/**
 * @author SDX
 * @create 2022-08-26 11:03
 */
@SuppressWarnings("all")
public class ServerImpl implements Server, ConfigurationAware, PropertiesAware{
    ServerSocket ss = null;
    DBStore dbStore;
    Log log;
    int serverPort,stopPort;
    //    接收客户端发送的数据
    @Override
    public void reciver() throws Exception {
        log.debug("等待客户端的监听,服务端的端口号是："+serverPort);
        ss = new ServerSocket(serverPort);
        //1.开启一个服务ServerSocket
        toshutDown();
        while(true){
            //2.监听客户端的连接accept
            Socket socket = ss.accept();
            log.debug("监听到客户端的连接");
            //3.接收客户端发送过来的数据流
            InputStream is = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            Collection<Environment> c = (Collection<Environment>) ois.readObject();
            //4.打印对象的长度
//            System.out.println("接收到的长度为：" + c.size());
            dbStore.saveDB(c);
        }

    }
    //关闭服务器
    //1.设置标志值
    //2.暴力关闭ServerSocket.close()
    //开启一个线程，目的是关闭8989客户端
    public void toshutDown(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("等待断开服务监听，端口号是：" + stopPort);
                    ServerSocket closeSoclet = new ServerSocket(stopPort);
                    //只要有人连接9999说明要关闭8989客户端
                    closeSoclet.accept();
                    //监听9999客户端的连接
                    shutdown();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    //关闭服务器
    @Override
    public void shutdown() throws Exception {
        //1.设置标志值
        //2.暴力关闭ServerSocket.close()
        ss.close();
    }


    @Override
    public void setConfiguration(Configuration configuration) throws Exception {
        this.dbStore = configuration.getDbStore();
        this.log = configuration.getLogger();
    }

    @Override
    public void init(Properties properties) throws Exception {
        this.serverPort = Integer.parseInt(properties.getProperty("server-port"));
        this.stopPort = Integer.parseInt(properties.getProperty("stop-port"));
    }
}

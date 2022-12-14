package com.briup.smart.env.server;


import com.briup.smart.env.Configuration;
import com.briup.smart.env.entity.Environment;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.util.Log;
import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Collection;
import java.util.Properties;

/**
 * @author SDX
 * @create 2022-08-27 9:06
 */
@SuppressWarnings("all")
public class DBStoreImpl implements DBStore, ConfigurationAware {
    Log log;
    private static DataSource dataSource;
//初始化连接池
static{
    try {
        //1.创建properties对象
        Properties properties = new Properties();
        //2.获取文件输入流
        InputStream inputStream =
                DBStoreImpl.class.getClassLoader().getResourceAsStream("druid.properties");

        //3.读取文件输入流
        properties.load(inputStream);
        //4.创建DataSource对象
        dataSource = DruidDataSourceFactory.createDataSource(properties);
    } catch (Exception e) {
        e.printStackTrace();
    }

}
    @Override
    public void saveDB(Collection<Environment> c) throws Exception {

        //1.获取连接
        Connection conn = dataSource.getConnection();
        //当前日期
        int curr_day = -1;
        //上一个日期
        int last_day = -1;
        String sql = null;
        //3.获取statement对象
        PreparedStatement ps = null;
        log.info("准备入库，所需入库的数量为："+c.size());
        //循环处理集合中的Environment
        for (Environment environment : c) {
            //根据时间戳获取具体的天
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(environment.getGather_date());
            curr_day = calendar.get(Calendar.DAY_OF_MONTH);
//            System.out.println("通过时间戳得到的天是：" + day);

            //说明是第一条数据
            if(last_day == -1){
                sql = "insert into E_DETAIL_" + curr_day +
                        " VALUES (?,?,?,?,?,?,?,?,?,?)";
                ps = conn.prepareStatement(sql);
            }
            //如果当前天不等于上一条的天 则重新定位表
            if(curr_day != last_day){
                sql = "insert into E_DETAIL_" + curr_day +
                        " VALUES (?,?,?,?,?,?,?,?,?,?)";
                ps = conn.prepareStatement(sql);
            }
            ps.setString(1,environment.getName());
            ps.setString(2,environment.getSrcId());
            ps.setString(3,environment.getDesId());
            ps.setString(4,environment.getDevId());
            ps.setString(5,environment.getSersorAddress());
            ps.setInt(6,environment.getCount());
            ps.setString(7,environment.getCmd());
            ps.setInt(8,environment.getStatus());
            ps.setFloat(9, environment.getData());
            ps.setTimestamp(10,environment.getGather_date());
            ps.executeUpdate();
            last_day = curr_day;
        }
        if (ps != null) {
            ps.close();
        }
        conn.close();
    }

    @Override
    public void setConfiguration(Configuration configuration) throws Exception {
        this.log = configuration.getLogger();
    }
}

package com.lagou.edu.utils;

import com.lagou.edu.annotation.MyComponent;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 单例模式
 */
@MyComponent
public class ConnectionUtils {

    //存储当前线程的连接
    private ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    public Connection getCurrentThreadConn() throws SQLException {
        //判断当前线程中是否已经绑定链接，若未绑定，则需从线程池中获取连接绑定到当前线程中
        Connection connection = threadLocal.get();

        if (connection == null) {
            //当前线程未绑定连接,从连接池中获取连接
            connection = DruidUtils.getInstance().getConnection();
            //将获取到的连接和当前线程绑定
            threadLocal.set(connection);
        }

        return connection;
    }

}

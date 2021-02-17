package com.lagou.edu.utils;

import com.lagou.edu.annotation.MyAutowired;
import com.lagou.edu.annotation.MyComponent;

import java.sql.SQLException;

/**
 * 事务管理器：负责管理事务的开启、提交、回滚
 */
@MyComponent
public class TranscationManager {

    @MyAutowired("com.lagou.edu.utils.ConnectionUtils")
    private ConnectionUtils connectionUtils;

    public void beginTranscation() throws SQLException {
        connectionUtils.getCurrentThreadConn().setAutoCommit(false);
    }

    public void commitTranscation() throws SQLException {
        connectionUtils.getCurrentThreadConn().commit();
    }

    public void rollbackTranscation() throws SQLException {
        connectionUtils.getCurrentThreadConn().rollback();
    }

}

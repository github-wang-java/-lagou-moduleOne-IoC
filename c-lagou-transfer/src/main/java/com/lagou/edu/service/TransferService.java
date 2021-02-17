package com.lagou.edu.service;

/**
 * @author 木笔
 */
public interface TransferService {

    void transfer(String fromCardNo, String toCardNo, int money) throws Exception;
}

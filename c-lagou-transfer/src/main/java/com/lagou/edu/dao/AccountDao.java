package com.lagou.edu.dao;

import com.lagou.edu.pojo.Account;

/**
 * @author 木笔
 */
public interface AccountDao {

    Account queryAccountByCardNo(String cardNo) throws Exception;

    int updateAccountByCardNo(Account account) throws Exception;
}

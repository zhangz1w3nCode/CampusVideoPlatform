package com.zzw.service;

import com.zzw.dao.UserCoinDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserCoinService {
    @Autowired
    private UserCoinDao userCoinDao;

    public Long getUserCoinsAmount(Long userId) {
        return userCoinDao.getUserCoinsAmount(userId);
    }

    public void updateUserCoinsAmount(Long userId, long amount) {
        userCoinDao.updateUserAmount(userId,amount,new Date());
    }
}

package com.gp9.game_MS.service;

import com.gp9.game_MS.client.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserClient userClient;

    @Autowired
    public UserService(UserClient userClient) {
        this.userClient = userClient;
    }

    public boolean hasEnoughBalance(Long userId, double amount) {
        return userClient.checkBalance(userId, amount);
    }

    public void addBalance(Long userId, double amount) {
        userClient.addBalance(userId, amount);
    }

    public boolean deductBalance(Long userId, double amount) {
        return userClient.deductBalance(userId, amount);
    }
}

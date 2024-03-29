package com.CachWeb.Cach.service;

import com.CachWeb.Cach.dto.UserDto;
import com.CachWeb.Cach.entity.ExchangeRequest;
import com.CachWeb.Cach.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();

    void deleteUser(String email);
    void Save(ExchangeRequest exchangeRequest);

     List<ExchangeRequest> getExchangeRequestsForUser(String email);

    boolean emailExists(String email);

    void savePasswordResetToken(User user);

    String validatePasswordResetToken(String resetToken);

    User getUserByToken(String resetToken);

    void updatePassword(User user, String newPassword);
}
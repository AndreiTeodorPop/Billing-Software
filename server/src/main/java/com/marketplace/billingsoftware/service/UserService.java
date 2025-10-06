package com.marketplace.billingsoftware.service;

import com.marketplace.billingsoftware.io.UserRequest;
import com.marketplace.billingsoftware.io.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);

    String getUserRole(String email);

    List<UserResponse> readUsers();

    void deleteUser(String id);

}

package com.anakinud.repository;

import com.anakinud.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(String userId);

    void save(User user);
}

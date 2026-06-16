package com.parking.system.repository;

import com.parking.system.entity.User;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    User save(User user);
}

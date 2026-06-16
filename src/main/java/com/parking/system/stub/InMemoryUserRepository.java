package com.parking.system.stub;

import com.parking.system.entity.User;
import com.parking.system.repository.UserRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class InMemoryUserRepository implements UserRepository {

    private final AtomicLong sequence = new AtomicLong(1);
    private final Map<Long, User> users = new LinkedHashMap<>();

    @Override
    public Optional<User> findByUsername(String username) {
        return users.values().stream()
                .filter(user -> user.getUsername() != null && user.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(sequence.getAndIncrement());
        }
        users.put(user.getId(), user);
        return user;
    }
}

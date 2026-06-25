package com.parking.system.config;

import com.parking.system.entity.User;
import com.parking.system.enums.UserRole;
import com.parking.system.enums.UserStatus;
import com.parking.system.repository.UserRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthBootstrapConfig {

    @Bean
    @Profile("!test")
    public CommandLineRunner bootstrapAuthData(JdbcTemplate jdbcTemplate,
                                               UserRepository userRepository,
                                               PasswordEncoder passwordEncoder) {
        return args -> {
            jdbcTemplate.update("""
                    UPDATE users
                    SET role = CASE role
                        WHEN 'PARKING_MANAGER' THEN 'MANAGER'
                        WHEN 'PARKING_STAFF' THEN 'STAFF'
                        ELSE role
                    END
                    WHERE role IN ('PARKING_MANAGER', 'PARKING_STAFF')
                    """);

            jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check");
            jdbcTemplate.execute("""
                    ALTER TABLE users
                    ADD CONSTRAINT users_role_check
                    CHECK (role IN ('MANAGER', 'STAFF', 'DRIVER', 'SYSTEM_ADMIN'))
                    """);

            List<SeedUser> seedUsers = List.of(
                    new SeedUser("manager", "Manager User", "manager@parking.local", "0900000001", UserRole.MANAGER, "manager123"),
                    new SeedUser("staff", "Staff User", "staff@parking.local", "0900000002", UserRole.STAFF, "staff123"),
                    new SeedUser("driver", "Driver User", "driver@parking.local", "0900000003", UserRole.DRIVER, "driver123"),
                    new SeedUser("admin", "System Admin", "admin@parking.local", "0900000004", UserRole.SYSTEM_ADMIN, "admin123"),
                    new SeedUser("system_admin", "System Administrator", "system-admin@parking.local", "0900000005", UserRole.SYSTEM_ADMIN, "admin123")
            );

            for (SeedUser seedUser : seedUsers) {
                if (userRepository.findByUsername(seedUser.username()).isPresent()) {
                    continue;
                }

                User user = new User();
                user.setUsername(seedUser.username());
                user.setFullName(seedUser.fullName());
                user.setEmail(seedUser.email());
                user.setPhone(seedUser.phone());
                user.setRole(seedUser.role());
                user.setStatus(UserStatus.ACTIVE);
                user.setPassword(passwordEncoder.encode(seedUser.rawPassword()));
                userRepository.save(user);
            }
        };
    }

    private record SeedUser(
            String username,
            String fullName,
            String email,
            String phone,
            UserRole role,
            String rawPassword
    ) {
    }
}

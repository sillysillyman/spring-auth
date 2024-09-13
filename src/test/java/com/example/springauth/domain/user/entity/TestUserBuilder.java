package com.example.springauth.domain.user.entity;

import java.lang.reflect.Field;

public class TestUserBuilder {

    public static User createUserWithId(Long id) {
        User user = User.builder()
            .username("testUser")
            .password("encodedPassword")
            .email("test@example.com")
            .status(User.UserStatus.ACTIVE)
            .role(User.UserRole.ROLE_USER)
            .build();

        setIdForTest(user, id);
        return user;
    }

    private static void setIdForTest(User user, Long id) {
        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

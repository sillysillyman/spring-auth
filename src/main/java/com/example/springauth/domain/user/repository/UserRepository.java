package com.example.springauth.domain.user.repository;

import com.example.springauth.common.exception.user.DuplicateEmailException;
import com.example.springauth.common.exception.user.DuplicateUsernameException;
import com.example.springauth.common.exception.user.UserNotFoundException;
import com.example.springauth.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    default User findByIdOrThrow(Long userId) {
        return findById(userId).orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
    }

    default User findByUsernameOrThrow(String username) {
        return findByUsername(username).orElseThrow(
            () -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    default void validateEmailUniqueness(String email) {
        findByEmail(email).ifPresent(user -> {
            throw new DuplicateEmailException("중복된 이메일입니다.");
        });
    }

    default void validateUsernameUniqueness(String username) {
        findByUsername(username).ifPresent(user -> {
            throw new DuplicateUsernameException("중복된 사용자이름입니다.");
        });
    }
}

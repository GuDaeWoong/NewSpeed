package com.example.newspeed.user.repository;

import com.example.newspeed.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

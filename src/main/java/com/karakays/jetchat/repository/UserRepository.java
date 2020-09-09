package com.karakays.jetchat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.karakays.jetchat.domain.User;

public interface UserRepository extends JpaRepository<User, String> {

}

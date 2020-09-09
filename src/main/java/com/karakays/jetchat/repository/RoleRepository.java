package com.karakays.jetchat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.karakays.jetchat.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
	
	Role findByName(String name);
}

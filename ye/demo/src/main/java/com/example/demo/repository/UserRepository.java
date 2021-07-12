package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	// @EntityGraph 어노테이션은 해당 쿼리가 수행될 때 Lazy 조회가 아닌 Eager 조회로 authorities 정보를 조인해서 가져온다.
	@EntityGraph(attributePaths = "authorities")
	Optional<User> findOneWithAuthoritiesByUsername(String username);	
}

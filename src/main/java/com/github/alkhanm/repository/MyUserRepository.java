package com.github.alkhanm.repository;

import com.github.alkhanm.domain.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyUserRepository extends JpaRepository<MyUser, Long> {
    List<MyUser> findByUsername(String username);
}
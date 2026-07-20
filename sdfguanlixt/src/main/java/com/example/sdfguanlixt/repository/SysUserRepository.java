package com.example.sdfguanlixt.repository;

import com.example.sdfguanlixt.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SysUserRepository extends JpaRepository<SysUser, String> {

    Optional<SysUser> findByUsernameAndPasswordAndRole(String username, String password, String role);

    Optional<SysUser> findByUsername(String username);

    Optional<SysUser> findByResidentIdAndRole(String residentId, String role);
}

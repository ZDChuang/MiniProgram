package com.dech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dech.domain.Secret;

@Repository
public interface SecretRepository extends JpaRepository<Secret, Object>{
}

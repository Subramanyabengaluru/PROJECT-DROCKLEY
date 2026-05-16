package com.platform.drockley.repository;

import com.platform.drockley.entity.Expertise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExpertiseRepository extends JpaRepository<Expertise, UUID> {
    Optional<Expertise> findByNameAndCategory(String name, String category);
}

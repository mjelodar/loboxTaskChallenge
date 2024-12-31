package com.lobox.demo.repository;

import com.lobox.demo.repository.model.Crew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrewJpaRepository extends JpaRepository<Crew, String> {
}

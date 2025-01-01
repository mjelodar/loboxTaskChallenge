package com.lobox.demo.repository;

import com.lobox.demo.repository.model.Names;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NamesJpaRepository extends JpaRepository<Names, String> {
}

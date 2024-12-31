package com.lobox.demo.repository;

import com.lobox.demo.repository.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingJpaRepository extends JpaRepository<Rating, String> {
}

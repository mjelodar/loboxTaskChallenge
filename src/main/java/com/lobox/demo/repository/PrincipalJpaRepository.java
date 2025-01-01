package com.lobox.demo.repository;

import com.lobox.demo.repository.model.PrincipalPK;
import com.lobox.demo.repository.model.Principals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrincipalJpaRepository extends JpaRepository<Principals, PrincipalPK> {
}

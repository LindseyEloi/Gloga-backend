package com.centremedical.backend.repository;

import com.centremedical.backend.entity.Visiter;
import com.centremedical.backend.entity.VisiterId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisiterRepository extends JpaRepository<Visiter, VisiterId> {
}

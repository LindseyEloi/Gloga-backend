package com.centremedical.backend.repository;

import com.centremedical.backend.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {

    // Recherche par code (contient, insensible à la casse)
    List<Patient> findByCodepatContainingIgnoreCase(String codepat);

    // Recherche par nom (contient, insensible à la casse)
    List<Patient> findByNomContainingIgnoreCase(String nom);
}

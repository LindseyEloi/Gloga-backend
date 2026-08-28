package com.centremedical.backend.service;

import com.centremedical.backend.entity.Patient;
import com.centremedical.backend.exception.ResourceNotFoundException;
import com.centremedical.backend.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    public Patient findById(String codepat) {
        return patientRepository.findById(codepat)
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable : " + codepat));
    }

    public Patient create(Patient patient) {
        if (patientRepository.existsById(patient.getCodepat())) {
            throw new IllegalArgumentException("Un patient avec ce code existe déjà : " + patient.getCodepat());
        }
        return patientRepository.save(patient);
    }

    public Patient update(String codepat, Patient patient) {
        Patient existant = findById(codepat);
        existant.setNom(patient.getNom());
        existant.setPrenom(patient.getPrenom());
        existant.setSexe(patient.getSexe());
        existant.setAdresse(patient.getAdresse());
        return patientRepository.save(existant);
    }

    public void delete(String codepat) {
        Patient existant = findById(codepat);
        patientRepository.delete(existant);
    }

    // Recherche par code OU par nom (utilisée par le client Swing)
    public List<Patient> rechercher(String code, String nom) {
        if (code != null && !code.isBlank()) {
            return patientRepository.findByCodepatContainingIgnoreCase(code.trim());
        }
        if (nom != null && !nom.isBlank()) {
            return patientRepository.findByNomContainingIgnoreCase(nom.trim());
        }
        return patientRepository.findAll();
    }
}

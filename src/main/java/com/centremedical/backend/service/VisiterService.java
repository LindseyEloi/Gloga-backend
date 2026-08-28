package com.centremedical.backend.service;

import com.centremedical.backend.entity.Medecin;
import com.centremedical.backend.entity.Patient;
import com.centremedical.backend.entity.Visiter;
import com.centremedical.backend.entity.VisiterId;
import com.centremedical.backend.exception.ResourceNotFoundException;
import com.centremedical.backend.repository.MedecinRepository;
import com.centremedical.backend.repository.PatientRepository;
import com.centremedical.backend.repository.VisiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class VisiterService {

    private final VisiterRepository visiterRepository;
    private final MedecinRepository medecinRepository;
    private final PatientRepository patientRepository;

    @Autowired
    public VisiterService(VisiterRepository visiterRepository,
                           MedecinRepository medecinRepository,
                           PatientRepository patientRepository) {
        this.visiterRepository = visiterRepository;
        this.medecinRepository = medecinRepository;
        this.patientRepository = patientRepository;
    }

    public List<Visiter> findAll() {
        return visiterRepository.findAll();
    }

    public Visiter findById(String codemed, String codepat, LocalDate date) {
        VisiterId id = new VisiterId(codemed, codepat, date);
        return visiterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visite introuvable"));
    }

    public Visiter create(String codemed, String codepat, LocalDate date) {
        Medecin medecin = medecinRepository.findById(codemed)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable : " + codemed));
        Patient patient = patientRepository.findById(codepat)
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable : " + codepat));

        VisiterId id = new VisiterId(codemed, codepat, date);
        if (visiterRepository.existsById(id)) {
            throw new IllegalArgumentException("Cette visite existe déjà.");
        }
        Visiter visiter = new Visiter(medecin, patient, date);
        return visiterRepository.save(visiter);
    }

    public Visiter update(String codemed, String codepat, LocalDate date, LocalDate nouvelleDate) {
        Visiter existant = findById(codemed, codepat, date);
        // La date fait partie de la clé : on supprime l'ancienne ligne et on recrée avec la nouvelle date
        visiterRepository.delete(existant);
        return create(codemed, codepat, nouvelleDate);
    }

    public void delete(String codemed, String codepat, LocalDate date) {
        Visiter existant = findById(codemed, codepat, date);
        visiterRepository.delete(existant);
    }
}

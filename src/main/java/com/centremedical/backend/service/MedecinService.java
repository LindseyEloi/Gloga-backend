package com.centremedical.backend.service;

import com.centremedical.backend.entity.Medecin;
import com.centremedical.backend.exception.ResourceNotFoundException;
import com.centremedical.backend.repository.MedecinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedecinService {

    private final MedecinRepository medecinRepository;

    @Autowired
    public MedecinService(MedecinRepository medecinRepository) {
        this.medecinRepository = medecinRepository;
    }

    public List<Medecin> findAll() {
        return medecinRepository.findAll();
    }

    public Medecin findById(String codemed) {
        return medecinRepository.findById(codemed)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin introuvable : " + codemed));
    }

    public Medecin create(Medecin medecin) {
        if (medecinRepository.existsById(medecin.getCodemed())) {
            throw new IllegalArgumentException("Un médecin avec ce code existe déjà : " + medecin.getCodemed());
        }
        return medecinRepository.save(medecin);
    }

    public Medecin update(String codemed, Medecin medecin) {
        Medecin existant = findById(codemed);
        existant.setNom(medecin.getNom());
        existant.setPrenom(medecin.getPrenom());
        existant.setGrade(medecin.getGrade());
        return medecinRepository.save(existant);
    }

    public void delete(String codemed) {
        Medecin existant = findById(codemed);
        medecinRepository.delete(existant);
    }
}

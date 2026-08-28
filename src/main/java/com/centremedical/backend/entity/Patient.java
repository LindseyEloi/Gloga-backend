package com.centremedical.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "patient")
public class Patient {

    @Id
    @Column(name = "codepat", length = 10)
    private String codepat;

    @NotBlank
    @Column(name = "nom", nullable = false, length = 50)
    private String nom;

    @Column(name = "prenom", length = 50)
    private String prenom;

    // 'M' ou 'F'
    @Column(name = "sexe", length = 1)
    private String sexe;

    @Column(name = "adresse", length = 100)
    private String adresse;

    public Patient() {
    }

    public Patient(String codepat, String nom, String prenom, String sexe, String adresse) {
        this.codepat = codepat;
        this.nom = nom;
        this.prenom = prenom;
        this.sexe = sexe;
        this.adresse = adresse;
    }

    public String getCodepat() {
        return codepat;
    }

    public void setCodepat(String codepat) {
        this.codepat = codepat;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
}

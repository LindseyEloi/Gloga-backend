package com.centremedical.backend.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "visiter")
public class Visiter {

    @EmbeddedId
    private VisiterId id = new VisiterId();

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("codemed")
    @JoinColumn(name = "codemed")
    private Medecin medecin;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("codepat")
    @JoinColumn(name = "codepat")
    private Patient patient;

    public Visiter() {
    }

    public Visiter(Medecin medecin, Patient patient, java.time.LocalDate date) {
        this.medecin = medecin;
        this.patient = patient;
        this.id = new VisiterId(medecin.getCodemed(), patient.getCodepat(), date);
    }

    public VisiterId getId() {
        return id;
    }

    public void setId(VisiterId id) {
        this.id = id;
    }

    public Medecin getMedecin() {
        return medecin;
    }

    public void setMedecin(Medecin medecin) {
        this.medecin = medecin;
        if (this.id == null) this.id = new VisiterId();
        this.id.setCodemed(medecin != null ? medecin.getCodemed() : null);
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
        if (this.id == null) this.id = new VisiterId();
        this.id.setCodepat(patient != null ? patient.getCodepat() : null);
    }

    public java.time.LocalDate getDate() {
        return id != null ? id.getDate() : null;
    }

    public void setDate(java.time.LocalDate date) {
        if (this.id == null) this.id = new VisiterId();
        this.id.setDate(date);
    }
}

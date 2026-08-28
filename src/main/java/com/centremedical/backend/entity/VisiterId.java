package com.centremedical.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Clé primaire composite de la table VISITER (codemed, codepat, date).
 */
@Embeddable
public class VisiterId implements Serializable {

    @Column(name = "codemed", length = 10)
    private String codemed;

    @Column(name = "codepat", length = 10)
    private String codepat;

    @Column(name = "date_visite")
    private LocalDate date;

    public VisiterId() {
    }

    public VisiterId(String codemed, String codepat, LocalDate date) {
        this.codemed = codemed;
        this.codepat = codepat;
        this.date = date;
    }

    public String getCodemed() {
        return codemed;
    }

    public void setCodemed(String codemed) {
        this.codemed = codemed;
    }

    public String getCodepat() {
        return codepat;
    }

    public void setCodepat(String codepat) {
        this.codepat = codepat;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VisiterId)) return false;
        VisiterId that = (VisiterId) o;
        return Objects.equals(codemed, that.codemed)
                && Objects.equals(codepat, that.codepat)
                && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codemed, codepat, date);
    }
}

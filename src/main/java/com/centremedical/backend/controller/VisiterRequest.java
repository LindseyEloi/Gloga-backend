package com.centremedical.backend.controller;

import java.time.LocalDate;

/**
 * Objet reçu du client (JSON) pour créer ou modifier une visite.
 * Format de date attendu : "yyyy-MM-dd"
 */
public class VisiterRequest {

    private String codemed;
    private String codepat;
    private LocalDate date;

    public VisiterRequest() {
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
}

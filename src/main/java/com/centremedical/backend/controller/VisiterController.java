package com.centremedical.backend.controller;

import com.centremedical.backend.entity.Visiter;
import com.centremedical.backend.service.VisiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/visites")
public class VisiterController {

    private final VisiterService visiterService;

    @Autowired
    public VisiterController(VisiterService visiterService) {
        this.visiterService = visiterService;
    }

    @GetMapping
    public List<Visiter> getAll() {
        return visiterService.findAll();
    }

    @GetMapping("/{codemed}/{codepat}/{date}")
    public Visiter getOne(@PathVariable String codemed,
                           @PathVariable String codepat,
                           @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return visiterService.findById(codemed, codepat, date);
    }

    @PostMapping
    public ResponseEntity<Visiter> create(@RequestBody VisiterRequest requete) {
        Visiter cree = visiterService.create(requete.getCodemed(), requete.getCodepat(), requete.getDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }

    // Modifie la date d'une visite existante (codemed + codepat + ancienne date dans l'URL, nouvelle date dans le corps)
    @PutMapping("/{codemed}/{codepat}/{date}")
    public Visiter update(@PathVariable String codemed,
                           @PathVariable String codepat,
                           @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                           @RequestBody VisiterRequest requete) {
        return visiterService.update(codemed, codepat, date, requete.getDate());
    }

    @DeleteMapping("/{codemed}/{codepat}/{date}")
    public ResponseEntity<Void> delete(@PathVariable String codemed,
                                        @PathVariable String codepat,
                                        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        visiterService.delete(codemed, codepat, date);
        return ResponseEntity.noContent().build();
    }
}

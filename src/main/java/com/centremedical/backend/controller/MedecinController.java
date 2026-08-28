package com.centremedical.backend.controller;

import com.centremedical.backend.entity.Medecin;
import com.centremedical.backend.service.MedecinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medecins")
public class MedecinController {

    private final MedecinService medecinService;

    @Autowired
    public MedecinController(MedecinService medecinService) {
        this.medecinService = medecinService;
    }

    @GetMapping
    public List<Medecin> getAll() {
        return medecinService.findAll();
    }

    @GetMapping("/{codemed}")
    public Medecin getOne(@PathVariable String codemed) {
        return medecinService.findById(codemed);
    }

    @PostMapping
    public ResponseEntity<Medecin> create(@RequestBody Medecin medecin) {
        Medecin cree = medecinService.create(medecin);
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }

    @PutMapping("/{codemed}")
    public Medecin update(@PathVariable String codemed, @RequestBody Medecin medecin) {
        return medecinService.update(codemed, medecin);
    }

    @DeleteMapping("/{codemed}")
    public ResponseEntity<Void> delete(@PathVariable String codemed) {
        medecinService.delete(codemed);
        return ResponseEntity.noContent().build();
    }
}

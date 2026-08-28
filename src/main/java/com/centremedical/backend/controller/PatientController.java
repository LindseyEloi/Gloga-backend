package com.centremedical.backend.controller;

import com.centremedical.backend.entity.Patient;
import com.centremedical.backend.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public List<Patient> getAll() {
        return patientService.findAll();
    }

    @GetMapping("/{codepat}")
    public Patient getOne(@PathVariable String codepat) {
        return patientService.findById(codepat);
    }

    // Recherche par code ou par nom : /api/patients/recherche?code=P001  ou  ?nom=Rakoto
    @GetMapping("/recherche")
    public List<Patient> rechercher(@RequestParam(required = false) String code,
                                     @RequestParam(required = false) String nom) {
        return patientService.rechercher(code, nom);
    }

    @PostMapping
    public ResponseEntity<Patient> create(@RequestBody Patient patient) {
        Patient cree = patientService.create(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    }

    @PutMapping("/{codepat}")
    public Patient update(@PathVariable String codepat, @RequestBody Patient patient) {
        return patientService.update(codepat, patient);
    }

    @DeleteMapping("/{codepat}")
    public ResponseEntity<Void> delete(@PathVariable String codepat) {
        patientService.delete(codepat);
        return ResponseEntity.noContent().build();
    }
}

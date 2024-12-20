package com.example.controller;

import com.example.entity.Candidate;
import com.example.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    @Autowired
    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping
    public ResponseEntity<?> createCandidate(@RequestBody Candidate candidate) {
        // Validate input
        if (candidate.getEmailId() == null || candidate.getEmailId().isEmpty()) {
            return ResponseEntity.badRequest().body("Email ID cannot be null or empty.");
        }

        try {
            // Check if email ID already exists
            if (candidateService.isEmailIdExists(candidate.getEmailId())) {
                return ResponseEntity.badRequest().body("Email ID already exists, please enter a different email ID.");
            }

            Candidate createdCandidate = candidateService.createCandidate(candidate);
            return ResponseEntity.status(HttpStatus.CREATED).body("Candidate ID: " + createdCandidate.getCandidateId() + " generated successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error creating candidate: " + e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<?> getAllCandidates() {
        List<Candidate> candidates = candidateService.getAllCandidates();
        if (candidates.isEmpty()) {
            return ResponseEntity.ok("No Candidates available.");
        }
        return ResponseEntity.ok(candidates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCandidateById(@PathVariable int id) {
        Candidate candidate = candidateService.getCandidateById(id);
        if (candidate == null) {
            return ResponseEntity.badRequest().body("Invalid Candidate ID.");
        }
        return ResponseEntity.ok(candidate);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCandidate(@PathVariable int id, @RequestBody Candidate candidateDetails) {
        try {
            Candidate updatedCandidate = candidateService.updateCandidate(id, candidateDetails);
            return ResponseEntity.ok("Updated candidate ID " + id + " successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error updating candidate: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCandidate(@PathVariable int id) {
        try {
            candidateService.deleteCandidate(id);
            return ResponseEntity.ok("Deleted candidate ID " + id + " successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error deleting candidate: " + e.getMessage());
        }
    }
}

package com.example.controller;

import com.example.entity.Answer;
import com.example.entity.Candidate;
import com.example.entity.Question;
import com.example.service.AnswerService;
import com.example.service.CandidateService;
import com.example.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/assessment")
public class AssessmentController {
    @Autowired
    private CandidateService candidateService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private AnswerService answerService;



    @GetMapping("/questions/{candidateId}")
    public ResponseEntity<?> getQuestions(@PathVariable int candidateId) {
        Candidate candidate = candidateService.getCandidateById(candidateId);
        if (candidate == null) {
            return ResponseEntity.badRequest().body("Candidate Id doesn’t exist.");
        }

        if (candidate.isStarted()) {
            return ResponseEntity.ok("Exam Assessment Running.");
        }

        candidate.setStarted(true);
        candidateService.updateCandidate(candidateId, candidate);

        List<Question> allQuestions = questionService.getAllQuestions();
        Random random = new Random();
        List<Question> selectedQuestions = allQuestions.stream()
                .limit(10)
                .collect(Collectors.toList());
        return ResponseEntity.ok(selectedQuestions);
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitAnswers(@RequestBody List<Answer> answer) {
        if (answer.isEmpty()) {
            return ResponseEntity.badRequest().body("No answers provided.");
        }

        int candidateId = answer.get(0).getCandidateId();
        Candidate candidate = candidateService.getCandidateById(candidateId);
        if (candidate == null) {
            return ResponseEntity.badRequest().body("Candidate Id doesn’t exist.");
        }

        if (candidate.isSubmit()) {
            return ResponseEntity.badRequest().body("Answer already submitted.");
        }

        // Start a transaction here (if not managed automatically by your setup)
        try {
            for (Answer answers : answer) {
                answerService.createAnswer(answers);
            }
            candidate.setSubmit(true);
            candidateService.updateCandidate(candidateId, candidate);
        } catch (Exception e) {
            // Rollback transaction (if not managed automatically by your setup)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to submit answers.");
        }

        return ResponseEntity.ok("Answers submitted successfully.");
    }

}

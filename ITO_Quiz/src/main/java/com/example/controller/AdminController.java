package com.example.controller;

import com.example.dto.CandidateAnswerRequest;
import com.example.entity.Answer;
import com.example.entity.Question;
import com.example.service.CandidateService;
import com.example.service.EvaluationService;
import com.example.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private CandidateService candidateService;

    @PostMapping("/question")
    public ResponseEntity<?> createQuestion(@RequestBody Question question) {
        if (question.getOption1() != null && question.getOption2() != null && question.getOption3() != null && question.getOption4() != null && question.getAnswer() != null) {
            Question createdQuestion = questionService.createQuestion(question);
            if (createdQuestion != null) {
                return ResponseEntity.ok("Question " + createdQuestion.getQuestionId() + " generated successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to Generate Question");
            }
        } else {
            return ResponseEntity.badRequest().body("Failed to Generate Question - missing option or answer");
        }
    }

    @PostMapping("/questions")
    public ResponseEntity<?> createQuestions(@RequestBody List<Question> questions) {
        List<Question> validQuestions = questions.stream()
                .filter(q -> q.getOption1() != null && q.getOption2() != null && q.getOption3() != null && q.getOption4() != null && q.getAnswer() != null)
                .collect(Collectors.toList());

        if (!validQuestions.isEmpty()) {
            List<Question> createdQuestions = questionService.createQuestions(validQuestions);
            if (!createdQuestions.isEmpty()) {
                StringBuilder successMessage = new StringBuilder("Questions ");
                for (Question q : createdQuestions) {
                    successMessage.append(q.getQuestionId()).append(", ");
                }
                successMessage.append("generated successfully.");
                return ResponseEntity.ok(successMessage.toString());
            } else {
                return ResponseEntity.badRequest().body("Failed to Generate Questions");
            }
        } else {
            return ResponseEntity.badRequest().body("Failed to Generate Questions - missing options or answers");
        }
    }

    @GetMapping("/question")
    public ResponseEntity<?> getAllQuestions() {
        List<Question> questions = questionService.getAllQuestions();
        if (questions.isEmpty()) {
            return ResponseEntity.ok("No Questions available");
        }
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/question/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable int id) {
        Question question = questionService.getQuestionById(id);
        if (question == null) {
            return ResponseEntity.ok("Invalid Question Number");
        }
        return ResponseEntity.ok(question);
    }

    @PutMapping("/question/{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable int id, @RequestBody Question questionDetails) {
        Question updatedQuestion = questionService.updateQuestion(id, questionDetails);
        if (updatedQuestion != null) {
            return ResponseEntity.ok("Updated question number " + id + " successfully.");
        }
        return ResponseEntity.ok("Invalid Question Number");
    }

    @DeleteMapping("/question/{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable int id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok("Deleted question number " + id + " successfully.");
    }

    @PostMapping("/submitAnswerSheet")
    public ResponseEntity<?> submitAnswerSheet(@RequestBody CandidateAnswerRequest request) {
        try {
            String result = candidateService.submitAnswerSheet(request.getCandidateId(), request.getAnswers());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/evaluate/{candidateId}")
    public ResponseEntity<?> evaluateCandidate(@PathVariable int candidateId) {
        String result = evaluationService.evaluateCandidate(candidateId);
        return ResponseEntity.ok(result);
    }
}

package com.example.service;

import com.example.entity.Answer;
import com.example.entity.Candidate;
import com.example.entity.Question;
import com.example.repository.AnswerRepository;
import com.example.repository.CandidateRepository;
import com.example.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EvaluationService {
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private QuestionRepository questionRepository;

    public String evaluateCandidate(int candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);
        if (candidate == null) {
            return "Candidate Id doesn’t exist.";
        }

        List<Answer> answers = answerRepository.findByCandidateId(candidateId);
        int correctCount = 0;

        for (Answer answer : answers) {
            Question question = questionRepository.findById(answer.getQuestionId()).orElse(null);
            if (question != null && question.getAnswer().equals(answer.getAnswer())) {
                correctCount++;
            }
        }
        int totalQuestions = answers.size();
        int incorrectCount = totalQuestions - correctCount;
        String resultMessage;

        if (correctCount > 6) {
            resultMessage = candidateId + " : " + candidate.getName() + " is selected for the next round.";
        } else {
            resultMessage = candidateId + " : " + candidate.getName() + " is rejected in this round.";
        }

        resultMessage += "Correct Answers: " + correctCount + "\n";
        resultMessage += "Incorrect Answers: " + incorrectCount + "\n";
        return resultMessage;
    }
}

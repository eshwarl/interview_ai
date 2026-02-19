package com.interviewai.service.impl;

import com.interviewai.model.Interview;
import com.interviewai.model.InterviewStatus;
import com.interviewai.model.Role;
import com.interviewai.model.User;
import com.interviewai.repository.InterviewRepository;
import com.interviewai.repository.UserRepository;
import com.interviewai.service.InterviewService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;



@Service
public class InterviewServiceImpl implements InterviewService {

    private final InterviewRepository interviewRepository;
    private final UserRepository userRepository;

    public InterviewServiceImpl(InterviewRepository interviewRepository,
                                UserRepository userRepository) {
        this.interviewRepository = interviewRepository;
        this.userRepository = userRepository;
    }

    // ==============================
    // 1️⃣ ADMIN – Schedule Interview
    // ==============================
    @Override
    public Interview scheduleInterview(String title,
                                       String candidateEmail,
                                       LocalDateTime scheduledTime,
                                       Integer durationMinutes,
                                       String adminEmail) {

        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("Only admin can schedule interviews");
        }

        User candidate = userRepository.findByEmail(candidateEmail)
                .orElseThrow(() -> new RuntimeException("Candidate not registered"));

        if (candidate.getRole() != Role.USER) {
            throw new RuntimeException("Selected user is not a candidate");
        }

        String passkey = generatePasskey();

        Interview interview = Interview.builder()
                .title(title)
                .admin(admin)
                .candidate(candidate)
                .scheduledTime(scheduledTime)
                .durationMinutes(durationMinutes)
                .passkey(passkey)
                .Status(InterviewStatus.SCHEDULED)
                .createdAt(LocalDateTime.now())
                .build();

        return interviewRepository.save(interview);
    }

    // ==============================
    // 2️⃣ CANDIDATE – Join Lobby
    // ==============================
    @Override
    public Interview joinLobby(String passkey, String userEmail) {

        Interview interview = interviewRepository.findByPasskey(passkey)
                .orElseThrow(() -> new RuntimeException("Invalid passkey"));

        if (!interview.getCandidate().getEmail().equals(userEmail)) {
            throw new RuntimeException("You are not authorized for this interview");
        }

        if (interview.getStatus() != InterviewStatus.SCHEDULED) {
            throw new RuntimeException("Interview is not in scheduled state");
        }

        interview.setStatus(InterviewStatus.LOBBY);

        return interviewRepository.save(interview);
    }

    // ==============================
    // 3️⃣ CANDIDATE – Start Interview
    // ==============================
    @Override
    public Interview startInterview(Long interviewId, String userEmail) {

        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        if (!interview.getCandidate().getEmail().equals(userEmail)) {
            throw new RuntimeException("You are not authorized");
        }

        if (interview.getStatus() != InterviewStatus.LOBBY) {
            throw new RuntimeException("Interview must be in lobby state");
        }

        interview.setStatus(InterviewStatus.IN_PROGRESS);

        return interviewRepository.save(interview);
    }
//end interview code here
@Override
public Interview endInterview(Long interviewId, String userEmail) {

    Interview interview = interviewRepository.findById(interviewId)
            .orElseThrow(() -> new RuntimeException("Interview not found"));

    if (!interview.getCandidate().getEmail().equals(userEmail)) {
        throw new RuntimeException("You are not authorized");
    }

    if (interview.getStatus() != InterviewStatus.IN_PROGRESS) {
        throw new RuntimeException("Interview is not in progress");
    }

    interview.setStatus(InterviewStatus.COMPLETED);

    return interviewRepository.save(interview);
}

    // ==============================
    // 4️⃣ CANDIDATE – View My Interviews
    // ==============================
    @Override
    public List<Interview> getCandidateInterviews(String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return interviewRepository.findByCandidate(user);
    }

    // ==============================
    // Utility
    // ==============================
    private String generatePasskey() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}

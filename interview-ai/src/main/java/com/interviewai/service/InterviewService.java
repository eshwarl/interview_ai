package com.interviewai.service;

import com.interviewai.model.Interview;

import java.time.LocalDateTime;
import java.util.List;

public interface InterviewService {

    Interview scheduleInterview(
            String title,
            String candidateEmail,
            LocalDateTime scheduledTime,
            Integer durationMinutes,
            String adminEmail
    );

    Interview joinLobby(String passkey, String userEmail);

    Interview startInterview(Long interviewId, String userEmail);
    Interview endInterview(Long interviewId, String userEmail);


    List<Interview> getCandidateInterviews(String userEmail);
}


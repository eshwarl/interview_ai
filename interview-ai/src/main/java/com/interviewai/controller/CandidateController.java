package com.interviewai.controller;

import com.interviewai.model.Interview;
import com.interviewai.service.InterviewService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class CandidateController {

    private final InterviewService interviewService;

    public CandidateController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    // 1️⃣ Join Lobby using Passkey
    @PostMapping("/join")
    public Interview joinLobby(@RequestParam String passkey,
                               Authentication authentication) {

        String userEmail = authentication.getName();

        return interviewService.joinLobby(passkey, userEmail);
    }

    // 2️⃣ Start Interview
    @PostMapping("/start/{id}")
    public Interview startInterview(@PathVariable Long id,
                                    Authentication authentication) {

        String userEmail = authentication.getName();

        return interviewService.startInterview(id, userEmail);
    }

    // 3️⃣ End Interview
    @PostMapping("/end/{id}")
    public Interview endInterview(@PathVariable Long id,
                                  Authentication authentication) {

        String userEmail = authentication.getName();

        return interviewService.endInterview(id, userEmail);
    }

    // 4️⃣ View My Interviews
    @GetMapping("/my-interviews")
    public List<Interview> getMyInterviews(Authentication authentication) {

        String userEmail = authentication.getName();

        return interviewService.getCandidateInterviews(userEmail);
    }
}

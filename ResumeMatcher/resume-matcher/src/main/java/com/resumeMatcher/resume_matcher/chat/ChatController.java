package com.resumeMatcher.resume_matcher.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /** Manually reseed Qdrant with all candidate + job PDFs */
    @PostMapping("/seed")
    public ResponseEntity<String> seed() {
        chatService.seedContext();
        return ResponseEntity.ok("Context seeded successfully");
    }

    /** Ask a question — auto-seeds on first call */
    @PostMapping("/ask")
    public ResponseEntity<ChatDtos.ChatResponse> ask(@RequestBody ChatDtos.ChatRequest request) {
        return ResponseEntity.ok(chatService.ask(request.question()));
    }

    /** Clear Qdrant session so next ask triggers a fresh reseed */
    @DeleteMapping("/session")
    public ResponseEntity<Void> clearSession() {
        chatService.clearSession();
        return ResponseEntity.noContent().build();
    }
}
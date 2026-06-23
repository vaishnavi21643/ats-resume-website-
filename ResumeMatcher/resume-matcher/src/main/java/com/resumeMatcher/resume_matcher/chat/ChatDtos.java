
package com.resumeMatcher.resume_matcher.chat;

import java.util.List;

public class ChatDtos {
    public record ChatRequest(String question) {}
    public record ChatResponse(String answer, List<String> sources) {}
}
package com.example.demo;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/chat")
public class ChatController {
    private final BedRockService bedrockService;

    public ChatController(BedRockService bedrockService) {
        this.bedrockService = bedrockService;
    }

    @PostMapping(produces = "application/json")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        long start = System.currentTimeMillis();
        String reply = bedrockService.invokeModel(request.getMessage());
        long latency = System.currentTimeMillis() - start;
        return new ChatResponse(reply, latency);
    }
}
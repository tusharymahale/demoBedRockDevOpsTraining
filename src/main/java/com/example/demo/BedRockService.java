package com.example.demo;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import software.amazon.awssdk.core.SdkBytes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

@Service
public class BedRockService {
    private final BedrockRuntimeClient client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BedRockService(BedrockRuntimeClient client) {
        this.client = client;
    }

    public String invokeModel(String userMessage) {
        try {
            // Build the payload using ObjectMapper to properly handle escaping
            ObjectNode rootNode = objectMapper.createObjectNode();
            ArrayNode messagesArray = objectMapper.createArrayNode();

            ObjectNode messageNode = objectMapper.createObjectNode();
            messageNode.put("role", "user");

            ArrayNode contentArray = objectMapper.createArrayNode();
            ObjectNode contentNode = objectMapper.createObjectNode();
            contentNode.put("text", userMessage);
            contentArray.add(contentNode);

            messageNode.set("content", contentArray);
            messagesArray.add(messageNode);
            rootNode.set("messages", messagesArray);

            String payload = objectMapper.writeValueAsString(rootNode);

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId("eu.amazon.nova-micro-v1:0")
                    .contentType("application/json")
                    .accept("application/json")
                    .body(SdkBytes.fromUtf8String(payload))
                    .build();

            InvokeModelResponse response = client.invokeModel(request);
            String responseBody = response.body().asUtf8String();

            // Parse the response to extract the text content
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // Navigate through the response structure to get the text
            if (jsonNode.has("content") && jsonNode.get("content").isArray()) {
                JsonNode firstContent = jsonNode.get("content").get(0);
                if (firstContent.has("text")) {
                    return firstContent.get("text").asText();
                }
            }

            // Fallback if structure is different
            return responseBody;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
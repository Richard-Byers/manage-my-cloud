package org.mmc.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mmc.response.CustomDriveItem;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;

public class JsonUtils {
    public static boolean validateContentFormat(String content) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(content);

            // Check if the root is an object and contains the "duplicates" array
            if (!jsonNode.isObject() || !jsonNode.has("duplicates")) {
                return false;
            }

            // Check if each object in the "duplicates" array has the required fields
            for (JsonNode duplicate : jsonNode.get("duplicates")) {
                if (!duplicate.isObject() || !duplicate.has("name") || !duplicate.has("count") || !duplicate.has("files")) {
                    return false;
                }

                // Check if each object in the "files" array has the required fields
                for (JsonNode file : duplicate.get("files")) {
                    if (!file.isObject() || !file.has("id") || !file.has("name") || !file.has("type") || !file.has("createdDateTime") || !file.has("lastModifiedDateTime") || !file.has("webUrl")) {
                        return false;
                    }
                }
            }

            // If all checks passed, the content matches the required format
            return true;
        } catch (Exception e) {
            // If an exception is thrown, the content does not match the format
            return false;
        }
    }

    public static JsonNode transformJson(JsonNode inputJson) {
        CustomDriveItem root = new CustomDriveItem();
        ObjectMapper objectMapper = new ObjectMapper();
        root.setName("root");
        root.setType("Folder");
        root.setChildren(new ArrayList<>());

        for (JsonNode duplicate : inputJson.get("duplicates")) {
            for (JsonNode file : duplicate.get("files")) {
                CustomDriveItem child = new CustomDriveItem();
                child.setId(file.get("id").asText());
                child.setName(file.get("name").asText());
                child.setType(file.get("type").asText());
                child.setWebUrl(file.get("webUrl").asText());
                child.setChildren(new ArrayList<>());
                root.getChildren().add(child);
            }
        }

        return objectMapper.valueToTree(root);
    }

    public static JsonNode removeEmailFields(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.remove("emails");
            objectNode.elements().forEachRemaining(JsonUtils::removeEmailFields);
        } else if (node.isArray()) {
            node.elements().forEachRemaining(JsonUtils::removeEmailFields);
        }
        return node;
    }
}

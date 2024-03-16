package org.mmc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.mmc.util.JsonUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JsonUtilsTest {
    @Test
    public void testValidateContentFormat() {
        String validInput = "{\n" +
                "  \"duplicates\": [\n" +
                "    {\n" +
                "      \"name\": \"SaulGone.jpg\",\n" +
                "      \"count\": 4,\n" +
                "      \"files\": [\n" +
                "        {\n" +
                "          \"id\": \"1ShY23qyvPLHZaqVdXslXVJHJGpyUPg6y\",\n" +
                "          \"name\": \"SaulGone (7).jpg\",\n" +
                "          \"type\": \"image/jpeg\",\n" +
                "          \"createdDateTime\": 1710191563.614,\n" +
                "          \"lastModifiedDateTime\": 1709945510,\n" +
                "          \"webUrl\": \"https://drive.google.com/file/d/1ShY23qyvPLHZaqVdXslXVJHJGpyUPg6y/view?usp=drivesdk\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"1gM5xR3jbN0OfVZG-Zmd9LxrHwvkbUrDe\",\n" +
                "          \"name\": \"SaulGone (1).jpg\",\n" +
                "          \"type\": \"image/jpeg\",\n" +
                "          \"createdDateTime\": 1710191543.341,\n" +
                "          \"lastModifiedDateTime\": 1709945510,\n" +
                "          \"webUrl\": \"https://drive.google.com/file/d/1gM5xR3jbN0OfVZG-Zmd9LxrHwvkbUrDe/view?usp=drivesdk\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"19o8KYq5LgWvPvPZt4MxFmkv55MZA0QKH\",\n" +
                "          \"name\": \"SaulGone (5).jpg\",\n" +
                "          \"type\": \"image/jpeg\",\n" +
                "          \"createdDateTime\": 1710191557.811,\n" +
                "          \"lastModifiedDateTime\": 1709945510,\n" +
                "          \"webUrl\": \"https://drive.google.com/file/d/19o8KYq5LgWvPvPZt4MxFmkv55MZA0QKH/view?usp=drivesdk\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"id\": \"1vXICR0Vke-mt-z1fi6vthi50anyVRLsV\",\n" +
                "          \"name\": \"SaulGone (3).jpg\",\n" +
                "          \"type\": \"image/jpeg\",\n" +
                "          \"createdDateTime\": 1710191551.279,\n" +
                "          \"lastModifiedDateTime\": 1709945510,\n" +
                "          \"webUrl\": \"https://drive.google.com/file/d/1vXICR0Vke-mt-z1fi6vthi50anyVRLsV/view?usp=drivesdk\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        String invalidInput = "ThisShouldReturnFalse";

        assertTrue(JsonUtils.validateContentFormat(validInput));
        assertFalse(JsonUtils.validateContentFormat(invalidInput));
    }

    @Test
    public void testTransformJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String mockOutPutFromAI = "{\"duplicates\": [{\"name\": \"SaulGone.jpg\", \"count\": 4, \"files\": [{\"id\": \"1ShY23qyvPLHZaqVdXslXVJHJGpyUPg6y\", \"name\": \"SaulGone (7).jpg\", \"type\": \"image/jpeg\", \"createdDateTime\": 1.710191563614E9, \"lastModifiedDateTime\": 1709945510, \"webUrl\": \"https://drive.google.com/file/d/1ShY23qyvPLHZaqVdXslXVJHJGpyUPg6y/view?usp=drivesdk\"}, {\"id\": \"1gM5xR3jbN0OfVZG-Zmd9LxrHwvkbUrDe\", \"name\": \"SaulGone (1).jpg\", \"type\": \"image/jpeg\", \"createdDateTime\": 1.710191543341E9, \"lastModifiedDateTime\": 1709945510, \"webUrl\": \"https://drive.google.com/file/d/1gM5xR3jbN0OfVZG-Zmd9LxrHwvkbUrDe/view?usp=drivesdk\"}, {\"id\": \"19o8KYq5LgWvPvPZt4MxFmkv55MZA0QKH\", \"name\": \"SaulGone (5).jpg\", \"type\": \"image/jpeg\", \"createdDateTime\": 1.710191577811E9, \"lastModifiedDateTime\": 1709945510, \"webUrl\": \"https://drive.google.com/file/d/19o8KYq5LgWvPvPZt4MxFmkv55MZA0QKH/view?usp=drivesdk\"}, {\"id\": \"1vXICR0Vke-mt-z1fi6vthi50anyVRLsV\", \"name\": \"SaulGone (3).jpg\", \"type\": \"image/jpeg\", \"createdDateTime\": 1.710191551279E9, \"lastModifiedDateTime\": 1709945510, \"webUrl\": \"https://drive.google.com/file/d/1vXICR0Vke-mt-z1fi6vthi50anyVRLsV/view?usp=drivesdk\"}]}]}";
        JsonNode inputJson = mapper.readTree(mockOutPutFromAI);

        String expectedOutputJsonString = "{\"id\":null,\"name\":\"root\",\"type\":\"Folder\",\"createdDateTime\":null,\"lastModifiedDateTime\":null,\"webUrl\":null,\"children\":[{\"id\":\"1ShY23qyvPLHZaqVdXslXVJHJGpyUPg6y\",\"name\":\"SaulGone (7).jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":null,\"lastModifiedDateTime\":null,\"webUrl\":\"https://drive.google.com/file/d/1ShY23qyvPLHZaqVdXslXVJHJGpyUPg6y/view?usp=drivesdk\",\"children\":[],\"emails\":null,\"gmail\":false},{\"id\":\"1gM5xR3jbN0OfVZG-Zmd9LxrHwvkbUrDe\",\"name\":\"SaulGone (1).jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":null,\"lastModifiedDateTime\":null,\"webUrl\":\"https://drive.google.com/file/d/1gM5xR3jbN0OfVZG-Zmd9LxrHwvkbUrDe/view?usp=drivesdk\",\"children\":[],\"emails\":null,\"gmail\":false},{\"id\":\"19o8KYq5LgWvPvPZt4MxFmkv55MZA0QKH\",\"name\":\"SaulGone (5).jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":null,\"lastModifiedDateTime\":null,\"webUrl\":\"https://drive.google.com/file/d/19o8KYq5LgWvPvPZt4MxFmkv55MZA0QKH/view?usp=drivesdk\",\"children\":[],\"emails\":null,\"gmail\":false},{\"id\":\"1vXICR0Vke-mt-z1fi6vthi50anyVRLsV\",\"name\":\"SaulGone (3).jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":null,\"lastModifiedDateTime\":null,\"webUrl\":\"https://drive.google.com/file/d/1vXICR0Vke-mt-z1fi6vthi50anyVRLsV/view?usp=drivesdk\",\"children\":[],\"emails\":null,\"gmail\":false}],\"emails\":null,\"gmail\":false}";
        JsonNode expectedOutputJson = mapper.readTree(expectedOutputJsonString);

        JsonNode outputJson = JsonUtils.transformJson(inputJson);

        assertEquals(expectedOutputJson, outputJson);
    }
}

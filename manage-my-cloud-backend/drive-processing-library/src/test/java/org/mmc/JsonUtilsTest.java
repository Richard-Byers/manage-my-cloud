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

        String expectedOutputJsonString = "{\"id\":null,\"name\":\"root\",\"type\":\"Folder\",\"createdDateTime\":null,\"lastModifiedDateTime\":null,\"webUrl\":null,\"children\":[{\"id\":\"1ShY23qyvPLHZaqVdXslXVJHJGpyUPg6y\",\"name\":\"SaulGone (7).jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":null,\"lastModifiedDateTime\":null,\"webUrl\":\"https://drive.google.com/file/d/1ShY23qyvPLHZaqVdXslXVJHJGpyUPg6y/view?usp=drivesdk\",\"children\":[],\"emails\":null,\"gaveGmailPermissions\":false},{\"id\":\"1gM5xR3jbN0OfVZG-Zmd9LxrHwvkbUrDe\",\"name\":\"SaulGone (1).jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":null,\"lastModifiedDateTime\":null,\"webUrl\":\"https://drive.google.com/file/d/1gM5xR3jbN0OfVZG-Zmd9LxrHwvkbUrDe/view?usp=drivesdk\",\"children\":[],\"emails\":null,\"gaveGmailPermissions\":false},{\"id\":\"19o8KYq5LgWvPvPZt4MxFmkv55MZA0QKH\",\"name\":\"SaulGone (5).jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":null,\"lastModifiedDateTime\":null,\"webUrl\":\"https://drive.google.com/file/d/19o8KYq5LgWvPvPZt4MxFmkv55MZA0QKH/view?usp=drivesdk\",\"children\":[],\"emails\":null,\"gaveGmailPermissions\":false},{\"id\":\"1vXICR0Vke-mt-z1fi6vthi50anyVRLsV\",\"name\":\"SaulGone (3).jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":null,\"lastModifiedDateTime\":null,\"webUrl\":\"https://drive.google.com/file/d/1vXICR0Vke-mt-z1fi6vthi50anyVRLsV/view?usp=drivesdk\",\"children\":[],\"emails\":null,\"gaveGmailPermissions\":false}],\"emails\":null,\"gaveGmailPermissions\":false}";
        JsonNode expectedOutputJson = mapper.readTree(expectedOutputJsonString);

        JsonNode outputJson = JsonUtils.transformJson(inputJson);

        assertEquals(expectedOutputJson, outputJson);
    }

    @Test
    public void testExtractChildren() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "{\"id\":null,\"name\":\"root\",\"type\":\"Folder\",\"createdDateTime\":null,\"lastModifiedDateTime\":null,\"webUrl\":null,\"children\":[{\"id\":\"1HsGG5u48a94C1Xm3yapUyBuX9mjgz_-9\",\"name\":\"SaulGone (2) copy 8.jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":1.711291413582E9,\"lastModifiedDateTime\":1709945523,\"webUrl\":\"https://drive.google.com/file/d/1HsGG5u48a94C1Xm3yapUyBuX9mjgz_-9/view?usp=drivesdk\",\"children\":null,\"emails\":null},{\"id\":\"1EyycMOL21SDrtMhAXjyhv8jgx4RcsLUI\",\"name\":\"SaulGone (2) copy 3.jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":1.711291414832E9,\"lastModifiedDateTime\":1709945523,\"webUrl\":\"https://drive.google.com/file/d/1EyycMOL21SDrtMhAXjyhv8jgx4RcsLUI/view?usp=drivesdk\",\"children\":null,\"emails\":null},{\"id\":\"1UmWQv22UDlQ1KLi_fScLTBklZkWX2UJo\",\"name\":\"SaulGone (2) copy 6.jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":1.711291414832E9,\"lastModifiedDateTime\":1709945523,\"webUrl\":\"https://drive.google.com/file/d/1UmWQv22UDlQ1KLi_fScLTBklZkWX2UJo/view?usp=drivesdk\",\"children\":null,\"emails\":null},{\"id\":\"1Xk2RxpRprz5TqcmbWLqjcuRCWy0Iw8Sm\",\"name\":\"SaulGone (2) copy 9.jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":1.711291414832E9,\"lastModifiedDateTime\":1709945523,\"webUrl\":\"https://drive.google.com/file/d/1Xk2RxpRprz5TqcmbWLqjcuRCWy0Iw8Sm/view?usp=drivesdk\",\"children\":null,\"emails\":null},{\"id\":\"1hSFXb3j2HCt8_2Ws6SL-E2f1hBMkPbvQ\",\"name\":\"SaulGone (2) copy 2.jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":1.711291414832E9,\"lastModifiedDateTime\":1709945523,\"webUrl\":\"https://drive.google.com/file/d/1hSFXb3j2HCt8_2Ws6SL-E2f1hBMkPbvQ/view?usp=drivesdk\",\"children\":null,\"emails\":null},{\"id\":\"1n3IhOor-Y7_AKjtoTCRSv50ahK4rq4Ok\",\"name\":\"SaulGone (2) copy (1).jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":1.711291415654E9,\"lastModifiedDateTime\":1709945523,\"webUrl\":\"https://drive.google.com/file/d/1n3IhOor-Y7_AKjtoTCRSv50ahK4rq4Ok/view?usp=drivesdk\",\"children\":null,\"emails\":null},{\"id\":\"1vhluubX3E9HF1gR6DnuCoJbwFGpWgsbb\",\"name\":\"SaulGone (2) copy 10.jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":1.711291414832E9,\"lastModifiedDateTime\":1709945523,\"webUrl\":\"https://drive.google.com/file/d/1vhluubX3E9HF1gR6DnuCoJbwFGpWgsbb/view?usp=drivesdk\",\"children\":null,\"emails\":null},{\"id\":\"1FFJnHv6RiH8xgGyKeKft_XleBdWURhCB\",\"name\":\"SaulGone (2) copy 4.jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":1.711291414832E9,\"lastModifiedDateTime\":1709945523,\"webUrl\":\"https://drive.google.com/file/d/1FFJnHv6RiH8xgGyKeKft_XleBdWURhCB/view?usp=drivesdk\",\"children\":null,\"emails\":null},{\"id\":\"18ZMvR9HyP_ibgFKwvGya-L7JmTv-KnUm\",\"name\":\"SaulGone (2) copy.jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":1.711122363875E9,\"lastModifiedDateTime\":1709945523,\"webUrl\":\"https://drive.google.com/file/d/18ZMvR9HyP_ibgFKwvGya-L7JmTv-KnUm/view?usp=drivesdk\",\"children\":null,\"emails\":null},{\"id\":\"1tNo3BjnmJsdL5Cu2W0uDTXwxfoCMeZx6\",\"name\":\"SaulGone (2) copy 5.jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":1.711291417279E9,\"lastModifiedDateTime\":1709945523,\"webUrl\":\"https://drive.google.com/file/d/1tNo3BjnmJsdL5Cu2W0uDTXwxfoCMeZx6/view?usp=drivesdk\",\"children\":null,\"emails\":null},{\"id\":\"1QyDXLDTmPeU64C3gHlkogcWq-sLmzFEp\",\"name\":\"SaulGone (2) copy 7.jpg\",\"type\":\"image/jpeg\",\"createdDateTime\":1.711291414832E9,\"lastModifiedDateTime\":1709945523,\"webUrl\":\"https://drive.google.com/file/d/1QyDXLDTmPeU64C3gHlkogcWq-sLmzFEp/view?usp=drivesdk\",\"children\":null,\"emails\":null},{\"id\":\"1aMsWH7P5nC3zl-9VAoQwN9e29mkCpCbB\",\"name\":\"40298639-1 (1).mp4\",\"type\":\"video/mp4\",\"createdDateTime\":1.711122617962E9,\"lastModifiedDateTime\":1702304408,\"webUrl\":\"https://drive.google.com/file/d/1aMsWH7P5nC3zl-9VAoQwN9e29mkCpCbB/view?usp=drivesdk\",\"children\":null,\"emails\":null}]}";
        JsonNode inputJson = mapper.readTree(jsonString);

        JsonNode outputJson = JsonUtils.extractChildren(inputJson);

        // Assert that the outputJson has the same structure as the inputJson's "children" array
        assertEquals(inputJson.get("children").size(), outputJson.size());
        inputJson.get("children").forEach(child -> {
            String childId = child.get("id").asText();
            assertTrue(outputJson.has(childId));
            assertEquals(child, outputJson.get(childId));
        });
    }
}

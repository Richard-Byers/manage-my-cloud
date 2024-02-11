package com.authorisation.givens;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public class JsonNodeGivens {

    public static JsonNode generateJsonNode() {
        return JsonNodeFactory.instance.objectNode().set("test", JsonNodeFactory.instance.textNode("test valud"));
    }

}

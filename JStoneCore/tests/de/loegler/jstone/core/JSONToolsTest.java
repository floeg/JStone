package de.loegler.jstone.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JSONToolsTest {
    String expectedJSON = "{\"other\":{\"otherInner\":\"otherInnerValue\"}, \"innerObjectKey\":{\"innerKey\":\"innerValue\"}, \"testKey\":\"testValue\"}";
    private JSONObject createJSONObject() {
        JSONObject testObject = new JSONObject();
        testObject.map("testKey", "testValue");
        JSONObject inner = new JSONObject();
        inner.map("innerKey", "innerValue");
        testObject.map("innerObjectKey", inner);
        JSONObject otherInner = new JSONObject();
        otherInner.map("otherInner", "otherInnerValue");
        testObject.map("other", otherInner);
        return testObject;
    }
    @Test
    public void createJSON() {
        JSONObject testObject = createJSONObject();
        String json = JSONTools.getJSON(testObject);
        assertEquals(expectedJSON, json);
    }
    @Test
    public void fromJSON() throws JSONTools.JSONMalformedException {
        JSONObject exp = createJSONObject();
        JSONObject result = JSONTools.fromJSON(this.expectedJSON);
        assertEquals(exp, result);
        String scannerException = "{blub:\"blubb\"}";
        assertThrows(JSONTools.JSONMalformedException.class, () -> JSONTools.fromJSON(scannerException));
        String parserException = "{\"key:\"\"key:\"}";
        assertThrows(JSONTools.JSONMalformedException.class, () -> JSONTools.fromJSON(parserException));

    }

}
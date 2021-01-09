package mech.mania.engine.model;

import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SerializationTest {

    @Test
    public void deserializationTest() {
        Gson gson = new Gson();
        Position pos = new Position(1, 2);
        String json = gson.toJson(pos);
        assertEquals(json, "{\"x\":1,\"y\":2}");
    }
}

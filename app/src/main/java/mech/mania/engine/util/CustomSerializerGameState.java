package mech.mania.engine;

import com.google.gson.*;
import mech.mania.engine.model.GameLog;

import java.lang.reflect.Type;



public class CustomSerializerGameState implements JsonSerializer<GameLog>{
    public JsonElement serialize(GameLog gameLog, Type t, JsonSerializationContext jsc) {
        Gson serializer = new Gson();
        JsonObject gameLogJson = (JsonObject)serializer.toJsonTree(gameLog);
        JsonArray states = gameLogJson.getAsJsonArray("states");
        for (int stateI = 0; stateI < states.size(); stateI++) {
            JsonObject state = states.get(stateI).getAsJsonObject();
            JsonObject tileMap = state.getAsJsonObject("tileMap");
            JsonArray tiles = tileMap.getAsJsonArray("tiles");
            for (int tileI = 0; tileI < tiles.size(); tileI++) {
                JsonArray tileRow = tiles.get(tileI).getAsJsonArray();
                for (int tileJ = 0; tileJ < tileRow.size(); tileJ++) {
                    JsonObject tile = tileRow.get(tileJ).getAsJsonObject();
                    String type = tile.getAsJsonPrimitive("type").getAsString();
                    if (type.equals("GRASS") || type.equals("SOIL")) {
                        tileRow.set(tileJ, new JsonObject());
                    }
                }
            }
        }

        return gameLogJson;

//        for (GameState gameState in gameLog.getStates()) {
//
//            for (ArrayList<Tile> tiles in gameState.getTileMap().getTiles()) {
//                for (tile in tiles) {
//                    if (tile.getType().equals("GRASS")) {
//                        jObj
//                    }
//                }
//            }
//        }


    }
}
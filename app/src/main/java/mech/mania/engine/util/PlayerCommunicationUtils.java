package mech.mania.engine.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mech.mania.engine.model.*;
import mech.mania.engine.model.decisions.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class PlayerCommunicationUtils {
    /**
     * Parse an item object from a string
     * @param item String to parse
     * @return Item enum corresponding to string
     */
    public static ItemType itemFromString(String item) {
        return ItemType.getEnum(item);
    }

    /**
     * Parse an upgrade object from a string
     * @param upgrade String to parse
     * @return Upgrade enum corresponding to string
     */
    public static UpgradeType upgradeFromString(String upgrade) {
        return UpgradeType.getEnum(upgrade);
    }

    /**
     * Parse a plant type from a string
     * @param plant String to parse
     * @return PlantType that was parsed
     */
    protected static CropType plantTypeFromString(String plant) {
        return CropType.getEnum(plant);
    }

    /**
     * Function that generates the String to be sent to the bot given the GameState
     * @param gameState GameState to use
     * @param playerNum player number to set for the game state (0 or 1)
     * @return String to give to bot
     */
    public static String sendInfoFromGameState(GameState gameState, int playerNum) {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        gameState.setPlayerNum(playerNum + 1);
        return gson.toJson(gameState, GameState.class);
    }

    /**
     * Modify the decision object by parsing the decisionString and putting the necessary
     *
     * information inside the decision object
     * @param playerID ID of the player who made this decision (0 for player1, 1 for player2)
     * @param decisionString String to parse
     * @return PlayerDecision object
     */
    public static PlayerDecision parseDecision(int playerID, String decisionString) throws PlayerDecisionParseException {
        int space = decisionString.indexOf(' ');
        if (space == -1) {
            throw new PlayerDecisionParseException(String.format("Action type not found in string: %s", decisionString));
        }

        String action = decisionString.substring(0, space);
        // substring from space to end and strip all trailing whitespace
        String args = decisionString.substring(space + 1).replaceAll("\\s+$", "");

        switch (action.toLowerCase()) {
            case "move":
                return new MoveAction(playerID).parse(args);
            case "plant":
                return new PlantAction(playerID).parse(args);
            case "harvest":
                return new HarvestAction(playerID).parse(args);
            case "buy":
                return new BuyAction(playerID).parse(args);
            case "useitem": case "use_item":
                return new UseItemAction(playerID).parse(args);
            default:
                throw new PlayerDecisionParseException(String.format("Unrecognized action: %s", action));
        }
    }

    /**
     * Tokenize command line.
     * https://stackoverflow.com/questions/3259143/split-a-string-containing-command-line-parameters-into-a-string-in-java
     *
     * @param toProcess the command line to process.
     * @return the command line broken into strings.
     * An empty or null toProcess parameter results in a zero sized array.
     */
    public static String[] translateCommandline(String toProcess) {
        if (toProcess == null || toProcess.length() == 0) {
            //no command? no string
            return new String[0];
        }
        // parse with a simple finite state machine

        final int normal = 0;
        final int inQuote = 1;
        final int inDoubleQuote = 2;
        int state = normal;
        final StringTokenizer tok = new StringTokenizer(toProcess, "\"' ", true);
        final ArrayList<String> result = new ArrayList<>();
        final StringBuilder current = new StringBuilder();
        boolean lastTokenHasBeenQuoted = false;

        while (tok.hasMoreTokens()) {
            String nextTok = tok.nextToken();
            switch (state) {
                case inQuote:
                    if ("'".equals(nextTok)) {
                        lastTokenHasBeenQuoted = true;
                        state = normal;
                    } else {
                        current.append(nextTok);
                    }
                    break;
                case inDoubleQuote:
                    if ("\"".equals(nextTok)) {
                        lastTokenHasBeenQuoted = true;
                        state = normal;
                    } else {
                        current.append(nextTok);
                    }
                    break;
                default:
                    if ("'".equals(nextTok)) {
                        state = inQuote;
                    } else if ("\"".equals(nextTok)) {
                        state = inDoubleQuote;
                    } else if (" ".equals(nextTok)) {
                        if (lastTokenHasBeenQuoted || current.length() != 0) {
                            result.add(current.toString());
                            current.setLength(0);
                        }
                    } else {
                        current.append(nextTok);
                    }
                    lastTokenHasBeenQuoted = false;
                    break;
            }
        }
        if (lastTokenHasBeenQuoted || current.length() != 0) {
            result.add(current.toString());
        }
        if (state == inQuote || state == inDoubleQuote) {
            throw new RuntimeException("unbalanced quotes in " + toProcess);
        }
        return result.toArray(new String[0]);
    }

    /**
     * Helper function to get process ID of a Process object in Java. Only works
     * on UNIX computers due to syscall limitations
     *
     * https://stackoverflow.com/questions/4750470/how-to-get-pid-of-process-ive-just-started-within-java-program
     * @param process a java Process from ProcessBuilder
     * @return process id
     */
    public static int tryGetPid(Process process) {
        if (process.getClass().getName().equals("java.lang.UNIXProcess")) {
            try {
                Field f = process.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                return f.getInt(process);
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e) {
                System.out.println("Caught exception");
            }
        }

        return 0;
    }
}

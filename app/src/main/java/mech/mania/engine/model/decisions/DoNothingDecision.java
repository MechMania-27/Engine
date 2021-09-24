package mech.mania.engine.model.decisions;

import mech.mania.engine.logging.JsonLogger;
import mech.mania.engine.model.GameState;
import mech.mania.engine.model.PlayerDecisionParseException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoNothingDecision extends PlayerDecision {
    public DoNothingDecision(int playerID, JsonLogger playerLogger, JsonLogger engineLogger) {
        super(playerLogger, engineLogger);
        this.playerID = playerID;
    }

    public PlayerDecision parse(String args) throws PlayerDecisionParseException {
        String regex = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(args);

        if (!matcher.find()) {
            String message = "Arguments did not match DoNothing regex (make sure the command is only do_nothing/donothing)";
            playerLogger.feedback(message);
            throw new PlayerDecisionParseException(message);
        }
        return this;
    }

    @Override
    public void performAction(GameState state) {
        // do nothing
    }
}

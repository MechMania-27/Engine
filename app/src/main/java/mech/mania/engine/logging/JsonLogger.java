package mech.mania.engine.logging;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JsonLogger {
    @Expose
    @SerializedName("Logs")
    private List<InfraSingleTurnLog> logs = new ArrayList<>();

    private int turn;
    private List<String> infoLogs = new ArrayList<>();
    private List<String> debugLogs = new ArrayList<>();
    private List<String> exceptionLogs = new ArrayList<>();
    private List<String> turnFeedback = new ArrayList<>();

    private final String loggerName;

    /** Print debug statements? */
    private boolean debug = true;

    /** Print all log statements to stdout? */
    private final boolean printToStdout;

    public JsonLogger() {
        this(0, "JsonLogger", false);
    }

    public JsonLogger(int startingTurn) {
        this(startingTurn, "JsonLogger", false);
    }

    public JsonLogger(int startingTurn, String loggerName) {
        this(startingTurn, loggerName, false);
    }

    public JsonLogger(int startingTurn, String loggerName, boolean printToStdout) {
        this.loggerName = loggerName;
        this.turn = startingTurn;
        this.printToStdout = printToStdout;
    }

    /**
     * Increment the turn on this logger so that the correct turn is being logged.
     * This function will also create an InfraSingleTurnLog and flush out the contents
     * of the currently stored logs.
     */
    public void incrementTurn() {
        logs.add(new InfraSingleTurnLog(turn,
                new ArrayList<>(infoLogs),
                new ArrayList<>(debugLogs),
                new ArrayList<>(exceptionLogs)));
        infoLogs.clear();
        debugLogs.clear();
        exceptionLogs.clear();
        turn++;
    }

    public List<String> getInfoLogs() {
        return infoLogs;
    }

    public List<String> getDebugLogs() {
        return debugLogs;
    }

    public List<String> getExceptionLogs() {
        return exceptionLogs;
    }

    public List<String> getFeedback() {
        List<String> returnFeedback = new ArrayList<>(turnFeedback);
        turnFeedback.clear();
        return returnFeedback;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void feedback(String log) {
        turnFeedback.add(log);
    }

    public void info(String log) {
        if (log.length() > 0) {
            infoLogs.addAll(stringLines(log));
            if (printToStdout) {
                System.out.printf("[%s][info     ] %s%n", loggerName, log);
            }
        }
    }

    public void debug(String log) {
        if (debug && log.length() > 0) {
            debugLogs.addAll(stringLines(log));
            if (printToStdout) {
                System.out.printf("[%s][debug    ] %s%n", loggerName, log);
            }
        }
    }

    public void severe(String log) {
        if (log.length() > 0) {
            exceptionLogs.addAll(stringLines(log));
            if (printToStdout) {
                System.out.printf("[%s][stderr   ] %s%n", loggerName, log);
            }
        }
    }

    public void severe(String message, Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        String res = String.format("%s: %s\nStack Trace: %s",
                e.getClass().getSimpleName(), message, sw);
        exceptionLogs.addAll(stringLines(res));

        if (printToStdout) {
            System.out.printf("[%s][exception] %s%n", loggerName, res);
        }
    }

    /**
     * Will split into lines and return as a List of String for each line
     *
     * @param input String to parse
     * @return List of Strings for each line
     */
    private List<String> stringLines(String input) {
        String[] lines = input.split("\n");
        return Arrays.asList(lines);
    }

    public void severe(Exception e) {
        severe(e.getMessage(), e);
    }

    public String serializedString() {
        if (!infoLogs.isEmpty() || (!debugLogs.isEmpty() && debug) || !exceptionLogs.isEmpty()) {
            incrementTurn();
        }

        Gson serializer = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .addSerializationExclusionStrategy(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getName().equals("debugLogs") && !debug;
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .disableHtmlEscaping()
                .create();
        return serializer.toJson(this, JsonLogger.class);
    }

    public void writeToFile(String fileName) {
        try {
            Files.write(Paths.get(fileName), Collections.singletonList(serializedString()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            severe(String.format("Could not write to file (%s)", fileName), e);
        }
    }

    /**
     * Class containing logs for a single turn that should be serialized to JSON
     */
    private static class InfraSingleTurnLog {
        @Expose
        @SerializedName("Turn")
        private final int turn;
        @Expose
        @SerializedName("Info")
        private final List<String> infoLogs;
        @Expose
        @SerializedName("Debug")
        private final List<String> debugLogs;
        @Expose
        @SerializedName("Exception")
        private final List<String> exceptionLogs;

        public InfraSingleTurnLog(int turn, List<String> infoLogs,
                                  List<String> debugLogs, List<String> exceptionLogs) {
            this.turn = turn;
            this.infoLogs = infoLogs;
            this.debugLogs = debugLogs;
            this.exceptionLogs = exceptionLogs;
        }
    }
}

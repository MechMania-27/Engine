package mech.mania.engine;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Scanner;

/**
 * Hello world!
 */
public class Main {
    public static void main( String[] args ) {
        String p1Exec = args[0];
        String p2Exec = args[1];

        ProcessBuilder p1Pb = new ProcessBuilder(p1Exec.split(" "));
        ProcessBuilder p2Pb = new ProcessBuilder(p2Exec.split(" "));

        Process p1Pr, p2Pr;
        try {
            p1Pr = p1Pb.start();
        } catch (IOException e) {
            System.err.println("Player 1 failed to start, stacktrace below:");
            e.printStackTrace(System.err);
            return;
        }

        try {
            p2Pr = p2Pb.start();
        } catch (IOException e) {
            System.err.println("Player 2 failed to start, stacktrace below:");
            e.printStackTrace(System.err);
            return;
        }

        Scanner p1Reader = new Scanner(p1Pr.getErrorStream());
        Scanner p2Reader = new Scanner(p2Pr.getErrorStream());

        BufferedWriter p1Writer = new BufferedWriter(new OutputStreamWriter(p1Pr.getOutputStream()));
        BufferedWriter p2Writer = new BufferedWriter(new OutputStreamWriter(p2Pr.getOutputStream()));

        int turn = 1;
        while (true) {
            System.out.println(String.format("================\nTurn %s", turn));
            System.out.println("Writing");
            writeToPlayer(p1Writer, "Hello!");
            writeToPlayer(p2Writer, "Hello!");

            System.out.println("Reading");
            String p1Response = getResponseFromPlayer(p1Reader);
            String p2Response = getResponseFromPlayer(p2Reader);

            if (p1Response != null && p2Response != null) {
                System.out.println(String.format("Both players responded:\nPlayer 1: %s\nPlayer 2: %s", p1Response, p2Response));

                if (p1Response.equals("q") || p2Response.equals("q")) {
                    System.out.println("Game ending");
                    p1Reader.close();
                    p2Reader.close();
                    return;
                }
            }
            turn++;
        }
    }

    public static void writeToPlayer(BufferedWriter writer, String message) {
        try {
            writer.append(message).append("\n");
            writer.flush();
        } catch (IOException e) {
            System.err.println("Player failed to receive message, stacktrace below:");
            e.printStackTrace(System.err);
        }
    }

    public static String getResponseFromPlayer(Scanner reader) {
        String response;
        while (!reader.hasNextLine()) { }
        response = reader.nextLine();
        return response;
    }
}

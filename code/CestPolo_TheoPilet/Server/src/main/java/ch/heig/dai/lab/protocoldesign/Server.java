package ch.heig.dai.lab.protocoldesign;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Server {
    final static int SERVER_PORT = 8080;

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }

    private void run() {
        Gson gson = new Gson();
        try (ServerSocket serverSocket = new ServerSocket(Server.SERVER_PORT)) {
            System.out.println("Server listening on port " + Server.SERVER_PORT);
            while(true) {
                try(Socket clientSocket = serverSocket.accept()) {
                    System.out.println("New client connected");
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
                    String line;
                    while((line = in.readLine()) != null) {
                        if(line.equalsIgnoreCase("exit")) {
                            System.out.println("Client disconnected");
                            break;
                        }
                        System.out.println("Received: " + line);
                        JsonObject response = new JsonObject();

                        try {
                            JsonObject request = gson.fromJson(line, JsonObject.class);
                            String operation = request.has("operation") ? request.get("operation").getAsString() : null;
                            double operand1 = request.has("operand1") ? request.get("operand1").getAsDouble() : 0;
                            double operand2 = request.has("operand2") ? request.get("operand2").getAsDouble() : 0;

                            if(operation != null) {
                                switch (operation) {
                                    case "add":
                                        response.addProperty("result", operand1 + operand2);
                                        response.addProperty("error", (String) null);
                                        break;
                                    case "sub":
                                        response.addProperty("result", operand1 - operand2);
                                        response.addProperty("error", (String) null);
                                        break;
                                    case "mul":
                                        response.addProperty("result", operand1 * operand2);
                                        response.addProperty("error", (String) null);
                                        break;
                                    case "div":
                                        if (operand2 == 0) {
                                            response.addProperty("result", (String) null);
                                            response.addProperty("error", "Division by zero is not allowed");
                                        } else {
                                            response.addProperty("result", operand1 / operand2);
                                            response.addProperty("error", (String) null);
                                        }
                                        break;
                                }
                            }
                        } catch (JsonSyntaxException e) {
                            response.addProperty("result", (String) null);
                            response.addProperty("error", "Invalid JSON");
                        }
                        out.println(gson.toJson(response));
                    }
                } catch (Exception e) {
                    System.err.println("Error while handling client: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error while starting the server: " + e.getMessage());
            System.exit(1);
        }
    }
}
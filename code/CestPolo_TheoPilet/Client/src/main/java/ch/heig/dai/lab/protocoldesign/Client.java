package ch.heig.dai.lab.protocoldesign;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * The Client class connects to a server, sends arithmetic operation requests, and receives the results.
 */
public class Client {
    //private static final String SERVER_ADDRESS = "10.193.24.2";
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;

    /**
     * The main method to start the client.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    /**
     * Runs the client, connecting to the server and handling user input for arithmetic operations.
     */
    public void run() {
        // Initialize Gson object for JSON serialization/deserialization
        Gson gson = new Gson();

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to the server. Type ‘exit’ to quit.");

            while (true) {
                System.out.print("Enter an operation (e.g. ‘add 10 20’) or ‘exit’ to exit. : ");
                String input = scanner.nextLine().trim();

                // Check if the user wants to exit
                if (input.equalsIgnoreCase("exit")) {
                    out.write("exit\\n");
                    out.flush();
                    break;
                }

                // Split user input into parts
                String[] parts = input.split(" ");
                if (parts.length != 3) {
                    System.out.println("Invalid format. Use the format: <operation> <operand1> <operand2>");
                    continue;
                }

                String operation = parts[0];
                // Check if the operation is valid
                if ((!operation.equalsIgnoreCase("add")) &&
                        (!operation.equalsIgnoreCase("sub")) &&
                        (!operation.equalsIgnoreCase("mul")) &&
                        (!operation.equalsIgnoreCase("div"))) {
                    System.out.println("Invalid operation. Use one of the following: add, sub, mul, div");
                    continue;
                }
                try {
                    double operand1 = Double.parseDouble(parts[1]);
                    double operand2 = Double.parseDouble(parts[2]);

                    // Create a JSON object for the request
                    JsonObject request = new JsonObject();
                    request.addProperty("operation", operation);
                    request.addProperty("operand1", operand1);
                    request.addProperty("operand2", operand2);
                    out.write(gson.toJson(request) + "\n");
                    out.flush();

                    // Read the server's response
                    String jsonResponse = in.readLine();
                    if (jsonResponse == null) {
                        System.out.println("The connection to the server has been closed.");
                        break;
                    }

                    // Deserialize the JSON response
                    JsonObject response = gson.fromJson(jsonResponse, JsonObject.class);
                    if (response.has("error") && !response.get("error").isJsonNull()) {
                        System.out.println("Error : " + response.get("error").getAsString());
                    } else {
                        System.out.println("Result : " + response.get("result").getAsDouble());
                    }
                } catch (NumberFormatException e) {
                    System.out.println("The operands must be numbers.");
                }
            }
        } catch (IOException e) {
            System.out.println("Server connection error : " + e.getMessage());
        }
    }
}

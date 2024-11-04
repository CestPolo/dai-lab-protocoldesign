package ch.heig.dai.lab.protocoldesign;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    private static final String SERVER_ADDRESS = "10.193.24.2";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        Gson gson = new Gson();

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connecté au serveur. Tapez 'exit' pour quitter.");

            while (true) {
                System.out.print("Entrez une opération (par ex. 'add 10 20') ou 'exit' pour quitter : ");
                String input = scanner.nextLine().trim();

                // Vérifie si l'utilisateur veut quitter
                if ("exit".equalsIgnoreCase(input)) {
                    out.write("exit\n");
                    out.flush();
                    break;
                }

                // Parse l'entrée utilisateur pour créer une requête JSON
                String[] parts = input.split(" ");
                if (parts.length != 3) {
                    System.out.println("Format invalide. Utilisez le format : <opération> <opérande1> <opérande2>");
                    continue;
                }

                String operation = parts[0];
                try {
                    double operand1 = Double.parseDouble(parts[1]);
                    double operand2 = Double.parseDouble(parts[2]);

                    JsonObject request = new JsonObject();
                    request.addProperty("operation", operation);
                    request.addProperty("operand1", operand1);
                    request.addProperty("operand2", operand2);
                    out.write(gson.toJson(request) + "\n");
                    out.flush();

                    // Attend et affiche la réponse JSON du serveur
                    String jsonResponse = in.readLine();
                    if (jsonResponse == null) {
                        System.out.println("La connexion au serveur a été fermée.");
                        break;
                    }

                    // Parse la réponse JSON pour extraire le résultat ou l'erreur
                    Response response = gson.fromJson(jsonResponse, Response.class);
                    if (response.error != null) {
                        System.out.println("Erreur : " + response.error);
                    } else {
                        System.out.println("Résultat : " + response.result);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Les opérandes doivent être des nombres entiers.");
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur de connexion au serveur : " + e.getMessage());
        }
    }

    // Classe interne simple pour parser la réponse JSON
    private static class Response {
        Double result;
        String error;
    }
}

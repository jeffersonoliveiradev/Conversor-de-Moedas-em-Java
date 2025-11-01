package br.com.jo.conversor;

import java.net.http.*;      // Para fazer requisições HTTP
import java.net.URI;          // Para criar o endereço da API
import java.util.Map;         // Para armazenar o JSON convertido em um mapa
import java.util.Scanner;     // Para ler as opções do usuário
import com.google.gson.Gson;  // Para converter o JSON em objeto Java
import com.google.gson.JsonObject;

public class ConversorDeMoedas {

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);
        Gson gson = new Gson();

        // 🔑 Coloque aqui a sua chave obtida em exchangerate-api.com
        String apiKey = "fa26d2310be3ec3a3221b394";
        String baseUrl = "https://v6.exchangerate-api.com/v6/" + apiKey + "/latest/";

        System.out.println("=== CONVERSOR DE MOEDAS ===");

        // 🔁 LOOP PRINCIPAL: repete até o usuário sair
        while (true) {

            // Menu de moedas base
            System.out.println("\nEscolha a moeda base:");
            System.out.println("1. USD (Dólar Americano)");
            System.out.println("2. BRL (Real Brasileiro)");
            System.out.println("3. EUR (Euro)");
            System.out.println("4. GBP (Libra Esterlina)");
            System.out.println("5. JPY (Iene Japonês)");
            System.out.println("0. Sair");
            System.out.print("Opção: ");
            int opcao = sc.nextInt();

            if (opcao == 0) {
                System.out.println("\nEncerrando o conversor... 👋");
                break;
            }

            // Escolhe a moeda base com base no número digitado
            String moedaBase = switch (opcao) {
                case 1 -> "USD";
                case 2 -> "BRL";
                case 3 -> "EUR";
                case 4 -> "GBP";
                case 5 -> "JPY";
                default -> {
                    System.out.println("Opção inválida! Voltando ao menu...");
                    yield null;
                }
            };

            if (moedaBase == null) continue;

            // Monta o endereço da API com a moeda base
            String url = baseUrl + moedaBase;

            // Faz a requisição HTTP GET
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Converte o JSON em um Map (chave e valor)
            Map<String, Object> jsonMap = gson.fromJson(response.body(), Map.class);
            Map<String, Double> taxas = (Map<String, Double>) jsonMap.get("conversion_rates");

            // Escolhe a moeda de destino
            System.out.println("\nConverter de " + moedaBase + " para:");
            System.out.println("1. USD");
            System.out.println("2. BRL");
            System.out.println("3. EUR");
            System.out.println("4. GBP");
            System.out.println("5. JPY");
            System.out.print("Opção: ");
            int destinoOp = sc.nextInt();

            String moedaDestino = switch (destinoOp) {
                case 1 -> "USD";
                case 2 -> "BRL";
                case 3 -> "EUR";
                case 4 -> "GBP";
                case 5 -> "JPY";
                default -> {
                    System.out.println("Opção inválida! Voltando ao menu...");
                    yield null;
                }
            };

            if (moedaDestino == null) continue;

            // Pergunta o valor a converter
            System.out.print("\nDigite o valor a converter: ");
            double valor = sc.nextDouble();

            // Faz o cálculo da conversão
            if (taxas.containsKey(moedaDestino)) {
                double taxa = taxas.get(moedaDestino);
                double convertido = valor * taxa;
                System.out.printf("\n%.2f %s = %.2f %s%n", valor, moedaBase, convertido, moedaDestino);
            } else {
                System.out.println("Erro: taxa de conversão não encontrada!");
            }

            System.out.println("\nDeseja converter novamente? (s/n)");
            String resposta = sc.next().toLowerCase();
            if (!resposta.equals("s")) {
                System.out.println("\nEncerrando o conversor... 👋");
                break;
            }
        }

        sc.close();
    }
}

package com.tum.bank;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class App {
   
    private static final String ETHERSCAN_API_KEY = "CFH3XBEEIX8F36CBR2HPT6SWWBN8TDRID6";
    private static final String ETH_ADDRESS = "0xc280b28bE8592e3d8806E9dddE8e3E59F7Db3a4c";
    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();
 
     
    public static void main(String[] args) throws Exception {
    //using infura.io api (for node connecting)and web3j to fetch the balance of an address
       final Web3j client = Web3j.build(
        new HttpService(
        "https://mainnet.infura.io/v3/311c3aa96c084ba8963626960fd1b92b"
       )
    );
    
    final EthGetBalance balanceResponse =
    
     client.ethGetBalance(ETH_ADDRESS,DefaultBlockParameter
     .valueOf("latest")).sendAsync()
     .get(10,TimeUnit.SECONDS);

    
    final BigInteger unscaledbalance = balanceResponse.getBalance();
    final BigDecimal scaledbalance = new BigDecimal(unscaledbalance)
    .divide(new BigDecimal(1000000000000000000L),18,RoundingMode.HALF_UP);

    
//fetch the token(ETH) price from Binance API 
     try {
            // 1. send GET request
            String url = "https://www.binance.com/api/v3/ticker/price?symbol=ETHUSDT";
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");

            // 2. read response
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // 3. analyse JSON data
            JSONObject json = new JSONObject(response.toString());
            double ethPrice = json.getDouble("price");

            // 4. print and save
            System.out.println("ETH Price: $" + ethPrice);

             BigDecimal balanceInUSD = scaledbalance.multiply(BigDecimal.valueOf(ethPrice));
             System.out.println("ETH Balance in USD: $" + balanceInUSD);
             
        } catch (Exception e) {
            e.printStackTrace();
        }

    CompletableFuture.runAsync(() -> {
            try {
                fetchAndPrintTransactions();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).join();

    }

   public static void fetchAndPrintTransactions() throws IOException {
        String url = String.format(
                "https://api.etherscan.io/api?module=account&action=txlist&address=%s&startblock=0&endblock=99999999&page=1&offset=100&sort=asc&apikey=%s",
                ETH_ADDRESS, ETHERSCAN_API_KEY
        );

        Request request = new Request.Builder().url(url).get().build();
        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            System.out.println("Error fetching transactions: " + response.message());
            return;
        }

        JsonNode rootNode = objectMapper.readTree(response.body().string());
        JsonNode transactions = rootNode.path("result");

        if (!transactions.isArray() || transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        printTransactionTable(transactions);
    }

private static void printTransactionTable(JsonNode transactions) {
    // print the header
    System.out.printf("%-5s %-15s %-15s %-15s %-20s %-20s %-15s%n",
            "ID", "BlockNumber", "TxHash", "From", "To", "Value (ETH)", "GasUsed");
    System.out.println("--------------------------------------------------------------------------------------------------------------------");

    // iterate the transaction history
    int transactionId = 1; 
    for (JsonNode tx : transactions) {
        String blockNumber = tx.get("blockNumber").asText();
        String hash = tx.get("hash").asText().substring(0, 10) + "...";
        String from = tx.get("from").asText().substring(0, 10) + "...";
        String to = tx.get("to").asText().substring(0, 10) + "...";
        String value = String.format("%.6f", Double.parseDouble(tx.get("value").asText()) / 1e18); 
        String gasUsed = tx.get("gasUsed").asText();

        System.out.printf("%-5d %-15s %-15s %-15s %-20s %-20s %-15s%n",
                transactionId, blockNumber, hash, from, to, value, gasUsed);
        
        transactionId++; 
    }
}
    }

    //fetch the transaction history

//   this is the url to call the transaction history    
//     https://api.etherscan.io/api
//    ?module=account
//    &action=txlist
//    &address=0xc280b28bE8592e3d8806E9dddE8e3E59F7Db3a4c
//    &startblock=0
//    &endblock=99999999
//    &page=1
//    &offset=100
//    &sort=asc
//    &apikey=CFH3XBEEIX8F36CBR2HPT6SWWBN8TDRID6
    


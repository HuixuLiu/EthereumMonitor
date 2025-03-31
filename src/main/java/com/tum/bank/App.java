package com.tum.bank;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;


public class App {


    public static void main(String[] args) throws Exception {
    //using infura.io api (for node connecting)and web3j to fetch the balance of an address
       final Web3j client = Web3j.build(
        new HttpService(
        "https://mainnet.infura.io/v3/311c3aa96c084ba8963626960fd1b92b"
       )
    );
    final String ethAddress ="0xc280b28bE8592e3d8806E9dddE8e3E59F7Db3a4c";
    final EthGetBalance balanceResponse =
    
     client.ethGetBalance(ethAddress,DefaultBlockParameter
     .valueOf("latest")).sendAsync()
     .get(10,TimeUnit.SECONDS);

    
    final BigInteger unscaledbalance = balanceResponse.getBalance();
    final BigDecimal scaledbalance = new BigDecimal(unscaledbalance)
    .divide(new BigDecimal(1000000000000000000L),18,RoundingMode.HALF_UP);
    System.out.print(unscaledbalance);
    System.out.print(scaledbalance);
    

     try {
            //fetch the token(ETH) price from Binance API 
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
    }
    
}
package com.tum.bank;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;

public class App {

// private static final String BINANCE_API = "https://api.binance.com/api/v3/ticker/price?symbol=ETHUSDT";

    public static void main(String[] args) throws Exception {
 
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
   

    }
}
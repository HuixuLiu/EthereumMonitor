# EthereumMonitor


This is a Java program that monitors a blockchain address on Ethereum,retrieve the balance from the target account and show the assert in USDT and fetches historical transactions.

 the code is under src/main/java/com/tum/bank/App.java


to use it, clone to  the local repo and go to the directory "EthereumMonitor/"

 and use "mvn clean install " then "  mvn clean compile exec:java -Dexec.mainClass="com.tum.bank.App"

 this will fetch the balance of the wallet"0xc280b28bE8592e3d8806E9dddE8e3E59F7Db3a4c" with infura.io api , calculate the balance in USD and display the historical transaction data. 





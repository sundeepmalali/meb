# Transaction Analyser

This project has the implementation logic of transaction analyser used to calculate account balance.

The source of transactions is provided as a csv file

**Pre-requisites**
- Maven 3
- Java 1.8

**Code details**
- The main class of the simulator is - **com.meb.TransactionAnalyser.java**

**Test details**
- There are unit tests for both positive and negative scenarios
- The main test class is **com.meb.TransactionAnalyserTest.java**

**Test data**
- The test data is listed in **src/test/resources/transactions\*.csv**

**Running the analyser**
* Download the code
    * git clone https://github.com/sundeepmalali/meb.git
* Execute the following commands:
    * cd meb
        * copy the transactions.csv file to meb folder
    * mvn clean install
    * java -jar target/fintran-0.0.1-SNAPSHOT.jar transactions.csv

**Sample execution**

```
> java -jar target/fintran-0.0.1-SNAPSHOT.jar transactions.csv
Enter the account id: 
ACC334455
Enter the from date (dd/MM/yyyy HH:mm:ss): 
20/10/2018 12:00:00
Enter the to date (dd/MM/yyyy HH:mm:ss): 
21/10/2019 12:00:00

Relative balance for the period is: $5.75
Number of transactions included is: 3
>
```

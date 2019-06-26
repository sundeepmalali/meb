package com.meb;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import com.meb.util.InputIngester;
import com.meb.util.ValidatorUtil;
import com.meb.vo.TransactionRecord;
import com.meb.vo.TransactionRecord.TransactionType;

/**
 * The main transaction analyser class used to find the relative balance of a given account
 * The transaction records are sourced from a csv file
 * 
 * @author sundeep
 *
 */
public class TransactionAnalyser {

    /**
     * Main method
     * 
     */
    public static void main(String[] args) {
        if (args.length == 0 || args[0].isEmpty()) {
            System.out.println("Input transaction file name not provided. Aborting...");
            return;
        }

        // get the inputs
        String accountId, fromDate, toDate;
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter the account id: ");
            accountId = scanner.nextLine();
            System.out.println("Enter the from date (dd/MM/yyyy HH:mm:ss): ");
            fromDate = scanner.nextLine();
            System.out.println("Enter the to date (dd/MM/yyyy HH:mm:ss): ");
            toDate = scanner.nextLine();
        }

        String fileName = args[0];
        new TransactionAnalyser().analyseTransactions(accountId, fromDate, toDate, fileName);
    }

    /**
     * The main method which triggers validation and initiates processing
     * 
     * @param accountId - Account id for which relative balance should be calculated
     * @param fromDate - start date of the transaction range
     * @param toDate - end date of the transaction range
     * @param fileName - file which contains the transaction records
     * 
     */
    public void analyseTransactions(String accountId, String fromDate, String toDate, String fileName) {
        // validate the inputs
        if (!ValidatorUtil.validateInputFile(fileName)) {
            System.out.println("Either transaction file does not exist or is not in CSV format. Aborting...");
            return;
        }
        if (!ValidatorUtil.validateInputs(accountId, fromDate, toDate)) {
            System.out.println("Aborting...");
            return;
        }
        
        // calculate the account balance
        calculateAccountBalance(accountId, fromDate, toDate, fileName);
    }

    /**
     * Reads the transaction records and calculates the account balance
     * 
     * @param accountId - Account id for which relative balance should be calculated
     * @param fromDate - start date of the transaction range
     * @param toDate - end date of the transaction range
     * @param fileName - file which contains the transaction records
     * 
     */
    private void calculateAccountBalance(String accountId, String fromDate, String toDate, String fileName) {
        // read the transactions file 
        List<TransactionRecord> txns = InputIngester.readTransactions(fileName);
        if (txns.isEmpty()) {
            System.out.println("No records in the input file. Aborting...");
            return;
        }

        // set inputs
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime from = LocalDateTime.parse(fromDate, df);
        LocalDateTime to = LocalDateTime.parse(toDate, df);

        // calculate incoming and outgoing balances
        BalanceTuple outBalance = calculateOutgoingBalance(accountId, from, to, txns);
        BalanceTuple inBalance = calculateIncomingBalance(accountId, from, to, txns);
        
        // calculate the relative balance and the transactions count
        Double relativeBalance = inBalance.getBalance() - outBalance.getBalance();
        int txnsSize = inBalance.getTxnsSize() + outBalance.getTxnsSize();

        String sign = relativeBalance < 0 ? "-" : "";
        System.out.println(String.format("\nRelative balance for the period is: " + sign + "$%.2f", Math.abs(relativeBalance)));
        System.out.println("Number of transactions included is: " + txnsSize);
    }

    /**
     * Calculates the outgoing balance of the account
     * 
     * @param accountId - Account id for which relative balance should be calculated
     * @param from - start date of the transaction range
     * @param to - end date of the transaction range
     * @param txns - list of transactions read from the input transaction file
     * @return - object containing the balance and the no of transactions considered
     * 
     */
    private BalanceTuple calculateOutgoingBalance(String accountId, LocalDateTime from, 
                                                         LocalDateTime to, List<TransactionRecord> txns) {
        // get the list of reversal transactions for the given account and store their original transactions
        Set<String> origTxns = txns.stream()
                                   .filter(rec -> rec.getFromAccountId().toUpperCase().equals(accountId.toUpperCase()))
                                   .filter(rec -> rec.getOrigTransactionid() != null)
                                   .map(TransactionRecord::getOrigTransactionid)
                                   .collect(Collectors.toSet());

        // filter out the reversal transactions and transactions outside the date range
        List<TransactionRecord> finalTxns = txns.stream()
                                                .filter(rec -> rec.getFromAccountId().toUpperCase().equals(accountId.toUpperCase()))
                                                .filter(rec -> !origTxns.contains(rec.getTransactionId().toUpperCase()))
                                                .filter(rec -> rec.getCreatedAt().isAfter(from) && rec.getCreatedAt().isBefore(to) &&
                                                               TransactionType.PAYMENT.equals(rec.getTxnType()))
                                                .collect(Collectors.toList());

        Double outBalance = finalTxns.stream()
                                     .mapToDouble(rec -> rec.getAmount())
                                     .sum();

        return new BalanceTuple(outBalance, finalTxns.size());
    }

    /**
     * Calculates the incoming balance of the account
     * 
     * @param accountId - Account id for which relative balance should be calculated
     * @param from - start date of the transaction range
     * @param to - end date of the transaction range
     * @param txns - list of transactions read from the input transaction file
     * @return - object containing the balance and the no of transactions considered
     * 
     */
    private BalanceTuple calculateIncomingBalance(String accountId, LocalDateTime from,
                                                         LocalDateTime to, List<TransactionRecord> txns) {
        // filter out the transactions and transactions outside the date range
        List<TransactionRecord> finalTxns = txns.stream()
                                                .filter(rec -> rec.getToAccountId().toUpperCase().equals(accountId.toUpperCase()))
                                                .filter(rec -> rec.getCreatedAt().isAfter(from) && rec.getCreatedAt().isBefore(to) &&
                                                               TransactionType.PAYMENT.equals(rec.getTxnType()))
                                                .collect(Collectors.toList());

        Double inBalance = finalTxns.stream()
                                    .mapToDouble(rec -> rec.getAmount())
                                    .sum();
        
        return new BalanceTuple(inBalance, finalTxns.size());
    }

    
    /**
     * Inner class which is used to hold the result of balance calculation
     * 
     * @author sundeep
     *
     */
    private class BalanceTuple {
        private Double balance;
        private int txnsSize;
        
        private BalanceTuple(Double balance, int txnSize) {
            this.balance = balance;
            this.txnsSize = txnSize;
        }
        
        private Double getBalance() {
            return balance;
        }
        
        private int getTxnsSize() {
            return txnsSize;
        }
    }
}

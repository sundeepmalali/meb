package com.meb.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.meb.vo.TransactionRecord;
import com.meb.vo.TransactionRecord.TransactionType;

/**
 * Utility class which reads the input transaction file in csv format
 * and converts it into a list of {@link TransactionRecord} objects
 * 
 * @author sundeep
 *
 */
public class InputIngester {
    
    /**
     * Reads the csv file and converts it into a list
     * 
     * @param fileName - the input transaction file
     * @return - List of {@link TransactionRecord}
     */
    public static List<TransactionRecord> readTransactions(String fileName) {
        Function<String, TransactionRecord> mapToTransaction = (line) -> {
            TransactionRecord txnRecord = new TransactionRecord();
            if (!line.trim().isEmpty()) {
                String[] txn = line.trim().split(",");

                txnRecord.setTransactionId(txn[0].trim());
                txnRecord.setFromAccountId(txn[1].trim());
                txnRecord.setToAccountId(txn[2].trim());
                if (!txn[3].isEmpty()) {
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    txnRecord.setCreatedAt(LocalDateTime.parse(txn[3].trim(), df));
                }
                txnRecord.setAmount(Double.valueOf(txn[4].trim()));
                txnRecord.setTxnType(TransactionType.valueOf(txn[5].trim()));
                if (TransactionType.REVERSAL.equals(TransactionType.valueOf(txn[5].trim()))) {
                    txnRecord.setOrigTransactionid(txn[6].trim());
                }
            }            
            return txnRecord;
        };
        
        List<TransactionRecord> inputs = new ArrayList<TransactionRecord>();
        try (Stream<String> lines = Files.lines(Paths.get(fileName), 
                                                Charset.defaultCharset())) {
            inputs = lines.skip(1)     // skip header row
                          .filter(line -> !line.trim().isEmpty())      // skip empty line
                          .map(mapToTransaction)
                          .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Error while reading input file : " + e.getMessage());
            e.printStackTrace();
        }
        
        return inputs;
    }
}

package com.meb.util;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class used to perform validation on the provided inputs
 * 
 * @author sundeep
 *
 */
public class ValidatorUtil {

    /**
     * Validates user provided inputs for null and empty check
     * 
     */
    public static boolean validateInputs(String accountId, String fromDate, String toDate) {
        if (accountId == null || accountId.isEmpty()) {
            System.out.println("Account Id is empty");
            return false;
        }
        if (fromDate == null || fromDate.isEmpty()) { 
            System.out.println("From date is empty");
            return false;
        }
        if (toDate == null || toDate.isEmpty()) {
            System.out.println("To date is empty");
            return false;
        }
        
        if (!validateDate(fromDate, "dd/MM/yyyy HH:mm:ss") ||
                !validateDate(toDate, "dd/MM/yyyy HH:mm:ss")) {
            System.out.println("Either from date or to date is invalid");
            return false;
        }

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime from = LocalDateTime.parse(fromDate, df);
        LocalDateTime to = LocalDateTime.parse(toDate, df);
        if (!isValidDateRange(from, to)) {
            System.out.println("From-date is later than To-date");
            return false;
        }
        
        return true;
    }
    
    /**
     * Validates the provided transaction file
     * 
     */
    public static boolean validateInputFile(String fileName) {
        return doesTransactionFileExist(fileName) &&
               isTransactionFileCSV(fileName);
    }

    /**
     * Checks if the provided transaction file exists
     * 
     */
    public static boolean doesTransactionFileExist(String fileName) {
        return (new File(fileName).exists());
    }
    
    /**
     * Check if the provided transaction file is a csv file
     * 
     */
    public static boolean isTransactionFileCSV(String fileName) {
        int lastIndex = fileName.lastIndexOf('.');
        if (lastIndex == -1 || !"csv".equals(fileName.substring(lastIndex + 1).toLowerCase())) {
            return false;
        }
        return true;
    }

    /**
     * Validates if a given date string is parseable in the given pattern
     * 
     * @param date - date to be parsed
     * @param pattern - the pattern to which the date should be parsed
     * 
     */
    @SuppressWarnings("unused")
    public static boolean validateDate(String date, String pattern) {
        try {
            DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime dt = LocalDateTime.parse(date, df);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    /**
     * Validates if start date of a date range falls before the end date 
     * 
     */
    public static boolean isValidDateRange(LocalDateTime from, LocalDateTime to) {
        return to.isAfter(from);
    }

}

package com.meb;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link TransactionAnalyser}
 *  
 * @author sundeep
 *
 */
public class TransactionAnalyserTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final TransactionAnalyser analyser = new TransactionAnalyser();
    
    @Before
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() throws Exception {
        System.setOut(null);
    }

    @Test
    public final void testNegativeBalance() {
        analyser.analyseTransactions("ACC334455", "20/10/2018 12:00:00", "20/10/2018 19:00:00", "src/test/resources/transactions1.csv");
        assertTrue(outContent.toString().trim().contains("Relative balance for the period is: -$25.00"));
        assertTrue(outContent.toString().trim().contains("Number of transactions included is: 1"));
    }

    @Test
    public final void testInvalidDateRange() {
        analyser.analyseTransactions("ACC334455", "20/10/2010 12:00:00", "20/10/2012 19:00:00", "src/test/resources/transactions1.csv");
        assertTrue(outContent.toString().trim().contains("Relative balance for the period is: $0.00"));
        assertTrue(outContent.toString().trim().contains("Number of transactions included is: 0"));
    }

    @Test
    public final void testPositiveBalance() {
        analyser.analyseTransactions("ACC334455", "20/10/2018 12:00:00", "21/10/2018 19:00:00", "src/test/resources/transactions2.csv");
        assertTrue(outContent.toString().trim().contains("Relative balance for the period is: $5.75"));
        assertTrue(outContent.toString().trim().contains("Number of transactions included is: 3"));
    }

    @Test
    public final void testInvalidAccountId() {
        analyser.analyseTransactions("ACC3344551", "20/10/2018 12:00:00", "20/10/2018 19:00:00", "src/test/resources/transactions1.csv");
        assertTrue(outContent.toString().trim().contains("Relative balance for the period is: $0.00"));
        assertTrue(outContent.toString().trim().contains("Number of transactions included is: 0"));
    }

    @Test
    public final void testEmptyAccountId() {
        analyser.analyseTransactions("", "20/10/2018 12:00:00", "20/10/2018 19:00:00", "src/test/resources/transactions1.csv");
        assertTrue(outContent.toString().trim().contains("Account Id is empty"));
    }

    @Test
    public final void testEmptyFromDate() {
        analyser.analyseTransactions("ACC334455", "", "20/10/2018 19:00:00", "src/test/resources/transactions1.csv");
        assertTrue(outContent.toString().trim().contains("From date is empty"));
    }

    @Test
    public final void testNullToDate() {
        analyser.analyseTransactions("ACC334455", "20/10/2018 19:00:00", null, "src/test/resources/transactions1.csv");
        assertTrue(outContent.toString().trim().contains("To date is empty"));
    }

    @Test
    public final void testInvalidFromDate() {
        analyser.analyseTransactions("ACC334455", "2018-10-01", "20/10/2018 19:00:00", "src/test/resources/transactions1.csv");
        assertTrue(outContent.toString().trim().contains("Either from date or to date is invalid"));
    }

    @Test
    public final void testInvalidToDate() {
        analyser.analyseTransactions("ACC334455", "20/10/2018 19:00:00", "2018-10-01", "src/test/resources/transactions1.csv");
        assertTrue(outContent.toString().trim().contains("Either from date or to date is invalid"));
    }

    @Test
    public final void testFromDateAfterToDate() {
        analyser.analyseTransactions("ACC334455", "20/10/2019 12:00:00", "20/10/2018 19:00:00", "src/test/resources/transactions1.csv");
        assertTrue(outContent.toString().trim().contains("From-date is later than To-date"));
    }

    @Test   
    public final void testInvalidTransactionFile() {
        analyser.analyseTransactions("ACC334455", "20/10/2018 12:00:00", "20/10/2018 19:00:00", "src/test/resources/transactions11.csv");
        assertTrue(outContent.toString().trim().contains("Either transaction file does not exist or is not in CSV format. Aborting..."));
    }

    @Test   
    public final void testNotCSVTransactionFile() {
        analyser.analyseTransactions("ACC334455", "20/10/2018 12:00:00", "20/10/2018 19:00:00", "src/test/resources/transactions11.xls");
        assertTrue(outContent.toString().trim().contains("Either transaction file does not exist or is not in CSV format. Aborting..."));
    }

}

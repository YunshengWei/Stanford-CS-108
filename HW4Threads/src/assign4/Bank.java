package assign4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Bank {

    public static final int NUM_ACCOUNTS = 20;
    public static final int INIT_BALANCE = 1000;

    private Account[] accounts;
    private BlockingQueue<Transaction> transactionQueue = new ArrayBlockingQueue<>(
            100);
    private CountDownLatch latch;

    /**
     * A Worker processes transactions. Worker communicates with Bank via
     * BlockingQueue.
     * 
     * @author WEIYUNSHENG
     *
     */
    class Worker implements Runnable {
        @Override
        public void run() {
            try {
                Transaction transaction;
                while ((transaction = transactionQueue.take()) != Transaction.nullTransaction) {
                    int from = transaction.from;
                    int to = transaction.to;
                    int amount = transaction.amount;
                    accounts[from].cutBalance(amount);
                    accounts[to].addBalance(amount);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            latch.countDown();
        }
    }

    public void start(String transactionFile, int numWorkers) {
        latch = new CountDownLatch(numWorkers);

        accounts = new Account[Bank.NUM_ACCOUNTS];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(i, Bank.INIT_BALANCE);
        }

        for (int i = 0; i < numWorkers; i++) {
            new Thread(new Worker()).start();
        }

        try (Scanner in = new Scanner(new BufferedReader(new FileReader(
                new File(transactionFile))))) {
            while (in.hasNext()) {
                int from = in.nextInt();
                int to = in.nextInt();
                int amount = in.nextInt();
                try {
                    transactionQueue.put(new Transaction(from, to, amount));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < accounts.length; i++) {
                try {
                    transactionQueue.put(Transaction.nullTransaction);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            latch.await();
            for (Account acc : accounts) {
                System.out.println(acc);
            }
        } catch (InterruptedException e) {
        }
    }

    public static void main(String[] args) {
        String transactionFile = args[0];
        int numWorkers = Integer.parseInt(args[1]);

        Bank bank = new Bank();
        bank.start(transactionFile, numWorkers);
    }

}

/**
 * An Account represents a bank account.
 * 
 * @author WEIYUNSHENG
 *
 */
class Account {
    private final int id;
    private int balance;
    private int numTransactions;

    public Account(int id, int balance) {
        this.id = id;
        this.balance = balance;
        numTransactions = 0;
    }

    public synchronized int getBalance() {
        return balance;
    }

    public synchronized void addBalance(int amount) {
        balance += amount;
        numTransactions++;
    }
    
    public synchronized void cutBalance(int amount) {
        balance -= amount;
        numTransactions++;
    }
    
    // getId() doesn't need to be synchronized, because id is never changed, so it will not expose so-called middle state.
    public int getId() {
        return id;
    }

    public synchronized int getNumTransactions() {
        return numTransactions;
    }

    @Override
    public synchronized String toString() {
        return String.format("acct:%s bal:%s trans:%s", id, balance,
                numTransactions);
    }
}

/**
 * A Transaction is an immutable class that stores information about a
 * transaction.
 * 
 * @author WEIYUNSHENG
 *
 */
class Transaction {
    public final int from;
    public final int to;
    public final int amount;
    
    public static final Transaction nullTransaction = new Transaction(-1, 0, 0);

    public Transaction(int from, int to, int amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }
}

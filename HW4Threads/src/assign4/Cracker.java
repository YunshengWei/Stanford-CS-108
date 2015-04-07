package assign4;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CountDownLatch;

public class Cracker {
    // Array of chars used to produce strings
    public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!"
            .toCharArray();

    /*
     * Given a byte[] array, produces a hex String, such as "234a6f". with 2
     * chars for each byte in the array. (provided code)
     */
    public static String hexToString(byte[] bytes) {
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            int val = bytes[i];
            val = val & 0xff; // remove higher bits, sign
            if (val < 16)
                buff.append('0'); // leading 0
            buff.append(Integer.toString(val, 16));
        }
        return buff.toString();
    }

    /*
     * Given a string of hex byte values such as "24a26f", creates a byte[]
     * array of those values, one byte value -128..127 for each 2 chars.
     * (provided code)
     */
    public static byte[] hexToArray(String hex) {
        byte[] result = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            result[i / 2] = (byte) Integer
                    .parseInt(hex.substring(i, i + 2), 16);
        }
        return result;
    }

    // possible test values:
    // a 86f7e437faa5a7fce15d1ddcb9eaeaea377667b8
    // fm adeb6f2a18fe33af368d91b09587b68e3abcb9a7
    // a! 34800e15707fae815d7c90d49de44aca97e2d759
    // xyz 66b27417d37e024c46526c2f6d358a754fc552f3
    
    // generateDigest() is threadsafe
    public static String generateDigest(String passwd) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            byte[] digest = md.digest(passwd.getBytes());
            return Cracker.hexToString(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    private static class CrackWorker implements Runnable {
        // index into CHARS, [from .. to]
        private int from, to, maxLength;
        private String goalValue;
        private CountDownLatch latch;
        
        public CrackWorker(int from, int to, int maxLength, String goalValue, CountDownLatch latch) {
            this.from = from;
            this.to = to;
            this.maxLength = maxLength;
            this.goalValue = goalValue;
            this.latch = latch;
        }
        
        @Override
        public void run() {
            for (int i = from; i <= to; i++) {
                crackRec(String.valueOf(CHARS[i]), maxLength - 1);
            }
            latch.countDown();
        }
        
        // Recursively do the cracking.
        // Output any valid password beginning with prefix, and suffixed with at most maxLengthRemain bytes.
        private void crackRec(String prefix, int maxLengthRemain) {
            if (Cracker.generateDigest(prefix).equals(goalValue)) {
                // System.out is threadsafe
                System.out.println(prefix);
            }
            if (maxLengthRemain > 0) {
                for (int i = 0; i < CHARS.length; i++) {
                    crackRec(prefix + CHARS[i], maxLengthRemain - 1);
                }
            }
        }
    }
    
    // Assume numThreads <= 40
    public static void crack(String hashValue, int maxLength, int numThreads) {
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        for (int i = 0; i < numThreads; i++) {
            int numPart = (int) Math.ceil(((double) CHARS.length) / numThreads);
            int from = i * numPart;
            int to = Math.min(from + numPart - 1, CHARS.length - 1);
            new Thread(new CrackWorker(from, to, maxLength, hashValue, latch)).start();
        }
        
        try {
            latch.await();
            System.out.println("All done.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        if (1 == args.length) {
            String passwd = args[0];
            System.out.println(Cracker.generateDigest(passwd));
        } else if (3 == args.length) {
            String hashValue = args[0];
            int maxLength = Integer.parseInt(args[1]);
            int numThreads = Integer.parseInt(args[2]);
            Cracker.crack(hashValue, maxLength, numThreads);
        } else {
            System.err.println("Wrong length of arguments");
        }
    }
}

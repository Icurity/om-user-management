package za.co.icurity.usermanagement.util;

import java.util.Random;

public class RandomPasswordGenerator {
    public RandomPasswordGenerator() {
        super();
    }
    private final String ALPHA = "abcdefghijklmnopqrstuvwxyz";
    private final String NUM = "0123456789";
    private final String SPL_CHARS = "~!@%^&*()/:<>?";

    public char[] generatePassword() {
        Random random = new Random();
        char[] password = new char[8];
        int index = 0;
        for (int i = 0; i < 2; i++) {
            index = getNextIndex(random, password);
            password[index] = ALPHA.toUpperCase().charAt(random.nextInt(ALPHA.length()));
        }
        for (int i = 0; i < 2; i++) {
            index = getNextIndex(random, password);
            password[index] = NUM.charAt(random.nextInt(NUM.length()));
        }
        for (int i = 0; i < 1; i++) {
            index = getNextIndex(random, password);
            password[index] = SPL_CHARS.charAt(random.nextInt(SPL_CHARS.length()));
        }
        for (int i = 0; i < 8; i++) {
            if (password[i] == 0) {
                password[i] = ALPHA.charAt(random.nextInt(ALPHA.length()));
            }
        }
        return password;
    }

    private int getNextIndex(Random rnd, char[] password) {
        int index = rnd.nextInt(8);
        while (password[index = rnd.nextInt(8)] != 0)
            ;
        return index;
    }

}

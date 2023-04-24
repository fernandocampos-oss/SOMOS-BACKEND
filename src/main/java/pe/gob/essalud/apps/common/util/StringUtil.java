package pe.gob.essalud.apps.common.util;

import java.util.Random;

public class StringUtil {

    public static String capitalize(final String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') {
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public static boolean isNullOrEmpty(String text) {
        return (text == null || text.isEmpty() || " ".equals(text));
    }

    public static String getRandomNumber(int digits) {
        Random rand = new Random();
        return String.format("%0" + digits + "d", rand.nextInt(10000));
    }
}
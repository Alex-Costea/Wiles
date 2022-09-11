package in.costea;

public class Utils {
    public static boolean isAlphanumeric(char c)
    {
        return isAlphabetic(c) || Character.isLetterOrDigit(c);
    }

    public static boolean isAlphabetic(char c)
    {
        return Character.isAlphabetic(c) || c=='_';
    }

    public static boolean isDigit(char c)
    {
        return Character.isDigit(c);
    }
}

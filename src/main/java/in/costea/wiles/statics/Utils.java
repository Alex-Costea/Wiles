package in.costea.wiles.statics;

import static in.costea.wiles.statics.Constants.DIGIT_SEPARATOR;

public class Utils
{
    private Utils()
    {
    }

    public static boolean isAlphanumeric(char c)
    {
        return isAlphabetic(c) || isDigit(c);
    }

    public static boolean isAlphabetic(char c)
    {
        return Character.isAlphabetic(c) || c == DIGIT_SEPARATOR;
    }

    public static boolean isDigit(char c)
    {
        return Character.isDigit(c);
    }
}

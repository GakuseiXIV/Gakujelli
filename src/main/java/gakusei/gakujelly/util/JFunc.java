package gakusei.gakujelly.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class JFunc
{
    public static List<String> StringToArray(String string)
    {
        return Arrays.stream(string.split(", ")).toList();
    }
    public static String ArrayToString(List<String> strings)
    {
        String f = "";
        for (String s : strings)
        {
            if (Objects.equals(f, "")) f = s;
            else f += ", " + s;
        }
        return f;
    }
}
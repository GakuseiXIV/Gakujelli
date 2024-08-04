package gakusei.gakujelli.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class JFunc
{
    public static List<String> StringToArray(String string)
    {
        if (Objects.equals(string, "") || string == null) return new ArrayList<>();
        return Arrays.stream(string.split(", ")).toList();
    }
    public static String ArrayToString(List<String> strings)
    {
        if (strings == null) return "";
        String f = "";
        for (String s : strings)
        {
            if (Objects.equals(f, "")) f = s;
            else f += ", " + s;
        }
        return f;
    }

    public static String RemoveFromFalseArray(String falseArray, String remove)
    {
        var e = StringToArray(falseArray);
        e.remove(remove);
        return JFunc.ArrayToString(e);
    }

    public static String AddToFalseArray(String falseArray, String add)
    {
        var e = StringToArray(falseArray);
        e.add(add);
        return JFunc.ArrayToString(e);
    }
}
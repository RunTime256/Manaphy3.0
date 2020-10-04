package bot.util;

import java.util.List;

public class CombineContent
{
    private CombineContent()
    {
    }

    public static String combine(List<String> vars)
    {
        StringBuilder builder = new StringBuilder();
        for (String content: vars)
        {
            String add = content + " ";
            builder.append(add);
        }

        if (builder.length() > 0)
            builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }
}

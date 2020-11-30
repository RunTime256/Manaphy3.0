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

    public static String combineQuotes(List<String> vars)
    {
        StringBuilder builder = new StringBuilder();

        if (vars.isEmpty())
            return null;

        if (!vars.get(0).startsWith("\""))
            return vars.remove(0);

        boolean end = false;

        int size = vars.size();
        for (int i = 0; i < size && !end; i++)
        {
            String content = vars.remove(0);
            if (i == 0 && content.startsWith("\""))
                content = content.substring(1);
            else if (content.endsWith("\""))
            {
                content = content.substring(0, content.length() - 1);
                end = true;
            }

            String add = content + " ";
            builder.append(add);
        }

        if (builder.length() > 0)
            builder.deleteCharAt(builder.length() - 1);

        if (end)
            return builder.toString();
        else
            return null;
    }
}

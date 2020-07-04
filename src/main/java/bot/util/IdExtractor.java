package bot.util;

public class IdExtractor
{
    private static final String START = "<";
    private static final String USER = "<@";
    private static final String NICKNAME = "<@!";
    private static final String ROLE = "<@&";
    private static final String CHANNEL = "<#";
    private static final String END = ">";

    public static long getId(String id)
    {
        if (id.endsWith(END))
        {
            id = id.substring(0, id.length() - END.length());

            if (id.startsWith(START))
            {
                if (id.startsWith(USER))
                    id = id.substring(USER.length());
                else if (id.startsWith(NICKNAME))
                    id = id.substring(NICKNAME.length());
                else if (id.startsWith(ROLE))
                    id = id.substring(ROLE.length());
                else if (id.startsWith(CHANNEL))
                    id = id.substring(CHANNEL.length());
            }
        }

        try
        {
            return Long.parseLong(id);
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }
}

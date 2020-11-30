package bot.command;

import bot.command.executor.ReactionExecutor;
import bot.discord.information.ReactionReceivedInformation;
import org.javacord.api.DiscordApi;
import sql.Session;

public class ReactionCommand
{
    public static final String YES = "\u2705";
    public static final String NO = "\u274C";
    public static final String NEXT = "\u25B6";
    public static final String PREV = "\u25C0";
    public static final String STOP = "\u23F9";


    private final ReactionExecutor executor;
    private final boolean[] completed;
    private final Object o;

    public ReactionCommand(ReactionExecutor executor, boolean[] completed, Object o)
    {
        this.executor = executor;
        this.completed = completed;
        this.o = o;
    }

    public void execute(DiscordApi api, ReactionReceivedInformation info, Session session)
    {
        if (!completed[0])
        {
            executor.runCommand(api, info, session, o);
            completed[0] = true;
        }
    }
}

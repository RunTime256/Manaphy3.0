package bot.command;

import bot.command.executor.ReactionExecutor;
import bot.discord.information.ReactionReceivedInformation;
import org.javacord.api.DiscordApi;
import sql.Session;

public class ReactionCommand
{
    public static final String YES = "\u2705";
    public static final String NO = "\u274C";
    private final ReactionExecutor executor;
    private final boolean[] completed;

    public ReactionCommand(ReactionExecutor executor, boolean[] completed)
    {
        this.executor = executor;
        this.completed = completed;
    }

    public void execute(DiscordApi api, ReactionReceivedInformation info, Session session)
    {
        if (!completed[0])
        {
            executor.runCommand(api, info, session);
            completed[0] = true;
        }
    }
}

package bot.command;

import bot.command.executor.ReactionExecutor;
import bot.discord.information.ReactionReceivedInformation;
import org.javacord.api.DiscordApi;
import sql.Session;

public class ReactionCommand
{
    private final ReactionExecutor executor;

    public ReactionCommand(ReactionExecutor executor)
    {
        this.executor = executor;
    }

    public void execute(DiscordApi api, ReactionReceivedInformation info, Session session)
    {
        executor.runCommand(api, info, session);
    }
}

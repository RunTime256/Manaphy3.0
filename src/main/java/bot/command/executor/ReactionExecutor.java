package bot.command.executor;

import bot.discord.information.ReactionReceivedInformation;
import org.javacord.api.DiscordApi;
import sql.Session;

public interface ReactionExecutor
{
    void runCommand(DiscordApi api, ReactionReceivedInformation info, Session session, Object o);
}

package bot.command.executor;

import bot.discord.information.MessageReceivedInformation;
import org.javacord.api.DiscordApi;
import sql.Session;

import java.util.List;

public interface CommandExecutor
{
    void runCommand(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session);
}

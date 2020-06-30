package bot.command.executor;

import bot.command.parser.MessageCommandParser;
import bot.discord.information.MessageReceivedInformation;
import org.javacord.api.DiscordApi;
import sql.Session;

import java.util.List;

public interface HelpCommandExecutor
{
    void runCommand(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session, MessageCommandParser parser);
}

package bot.command;

import bot.command.executor.HelpCommandExecutor;
import bot.command.parser.MessageCommandParser;
import bot.discord.information.MessageReceivedInformation;
import org.javacord.api.DiscordApi;
import sql.Session;

import java.util.List;

public class HelpMessageCommand extends MessageCommand
{
    private final HelpCommandExecutor executor;

    public HelpMessageCommand(String name, String description, HelpCommandExecutor executor)
    {
        super(name, description, null, null, null, null);
        this.executor = executor;
    }

    public void execute(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session, MessageCommandParser parser)
    {
        executor.runCommand(api, info, vars, session, parser);
    }
}

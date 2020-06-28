package bot.command.executor;

import bot.discord.listener.MessageReceivedInformation;
import sql.Session;

import java.util.List;

public interface CommandExecutor
{
    void runCommand(MessageReceivedInformation info, List<String> vars, Session session);
}

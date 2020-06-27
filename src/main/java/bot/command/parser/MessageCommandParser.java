package bot.command.parser;

import bot.command.MessageCommand;

import java.util.HashMap;
import java.util.Map;

public class MessageCommandParser
{
    private Map<String, MessageCommand> commands;

    public MessageCommandParser()
    {
        commands = new HashMap<>();
    }

    public void addCommand(MessageCommand command)
    {
        commands.put(command.getName(), command);
    }

    public MessageCommand getCommand(String commandString)
    {
        return null;
    }
}

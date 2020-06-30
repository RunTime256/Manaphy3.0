package bot.command.parser;

import bot.command.MessageCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageCommandParser
{
    private final Map<String, MessageCommand> commands;

    public MessageCommandParser()
    {
        commands = new HashMap<>();
    }

    public void addCommand(MessageCommand command)
    {
        commands.put(command.getName(), command);
    }

    public MessageCommand getCommand(List<String> vars)
    {
        MessageCommand command = null;
        Map<String, MessageCommand> subCommands = commands;
        while (!vars.isEmpty())
        {
            MessageCommand sub = subCommands.get(vars.get(0));
            if (sub == null)
                break;

            command = sub;
            vars.remove(0);
            subCommands = command.getSubCommands();
            if (subCommands == null)
                break;
        }

        return command;
    }

    public List<MessageCommand> getCommands()
    {
        List<MessageCommand> comms = new ArrayList<>(commands.values());
        comms.sort(Comparator.comparing(MessageCommand::getName));
        return comms;
    }
}

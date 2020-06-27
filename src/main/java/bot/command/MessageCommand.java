package bot.command;

import bot.command.executor.CommandExecutor;
import sql.Session;

import java.util.List;
import java.util.Map;

public class MessageCommand
{
    private final String name;
    private final String description;
    private final String syntax;
    private final Map<String, MessageCommand> subCommands;
    private final MessageCommand parent;
    private final CommandExecutor executor;

    private MessageCommand(String name, String description, String syntax,
                           Map<String, MessageCommand> subCommands, MessageCommand parent, CommandExecutor executor)
    {
        this.name = name;
        this.description = description;
        this.syntax = syntax;
        this.subCommands = subCommands;
        this.parent = parent;
        this.executor = executor;
    }

    public void execute(List<String> vars, Session session)
    {
        executor.runCommand(vars, session);
    }

    public String getName()
    {
        return name;
    }

    public Map<String, MessageCommand> getSubCommands()
    {
        return subCommands;
    }

    public static class MessageCommandBuilder
    {
        private final String name;
        private String description;
        private String syntax;
        private Map<String, MessageCommand> subCommands;
        private MessageCommand parent;
        private CommandExecutor executor;

        public MessageCommandBuilder(String name)
        {
            this.name = name;
        }

        public MessageCommandBuilder description(String description)
        {
            this.description = description;
            return this;
        }

        public MessageCommandBuilder syntax(String syntax)
        {
            this.syntax = syntax;
            return this;
        }

        public MessageCommandBuilder subCommands(Map<String, MessageCommand> subCommands)
        {
            this.subCommands = subCommands;
            return this;
        }

        public MessageCommandBuilder parent(MessageCommand parent)
        {
            this.parent = parent;
            return this;
        }

        public MessageCommandBuilder executor(CommandExecutor executor)
        {
            this.executor = executor;
            return this;
        }

        public MessageCommand build()
        {
            return new MessageCommand(name, description, syntax, subCommands, parent, executor);
        }
    }
}

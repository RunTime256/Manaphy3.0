package bot.command;

import java.util.Map;

public class MessageCommand
{
    private final String name;
    private final String description;
    private final String syntax;
    private final Map<String, MessageCommand> subCommands;
    private final MessageCommand parent;

    private MessageCommand(String name, String description, String syntax,
                           Map<String, MessageCommand> subCommands, MessageCommand parent)
    {
        this.name = name;
        this.description = description;
        this.syntax = syntax;
        this.subCommands = subCommands;
        this.parent = parent;
    }

    public String getName()
    {
        return name;
    }

    public static class MessageCommandBuilder
    {
        private final String name;
        private String description;
        private String syntax;
        private Map<String, MessageCommand> subCommands;
        private MessageCommand parent;

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

        public MessageCommand build()
        {
            return new MessageCommand(name, description, syntax, subCommands, parent);
        }
    }
}

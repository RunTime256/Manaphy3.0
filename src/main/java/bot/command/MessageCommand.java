package bot.command;

import bot.command.executor.CommandExecutor;
import bot.command.verification.RoleRequirement;
import bot.discord.listener.MessageReceivedInformation;
import sql.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageCommand
{
    private final String name;
    private final String description;
    private final String syntax;
    private final RoleRequirement requirement;
    private final Map<String, MessageCommand> subCommands;
    private final CommandExecutor executor;

    private MessageCommand(String name, String description, String syntax, RoleRequirement requirement,
                           Map<String, MessageCommand> subCommands, CommandExecutor executor)
    {
        this.name = name;
        this.description = description;
        this.syntax = syntax;
        this.requirement = requirement;
        this.subCommands = subCommands;
        this.executor = executor;
    }

    public void execute(MessageReceivedInformation info, List<String> vars, Session session)
    {
        executor.runCommand(info, vars, session);
    }

    public String getName()
    {
        return name;
    }

    public RoleRequirement getRequirement()
    {
        return requirement;
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
        private RoleRequirement requirement;
        private Map<String, MessageCommand> subCommands;
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

        public MessageCommandBuilder requirement(RoleRequirement requirement)
        {
            this.requirement = requirement;
            return this;
        }

        public MessageCommandBuilder subCommands(Map<String, MessageCommand> subCommands)
        {
            this.subCommands = subCommands;
            return this;
        }

        public MessageCommandBuilder subCommands(List<MessageCommand> subCommands)
        {
            this.subCommands = new HashMap<>();
            for(MessageCommand command: subCommands)
            {
                this.subCommands.put(command.getName(), command);
            }
            return this;
        }

        public MessageCommandBuilder executor(CommandExecutor executor)
        {
            this.executor = executor;
            return this;
        }

        public MessageCommand build()
        {
            return new MessageCommand(name, description, syntax, requirement, subCommands,executor);
        }
    }
}

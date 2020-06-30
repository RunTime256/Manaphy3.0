package bot.command.definition.help;

import bot.command.HelpMessageCommand;
import bot.command.MessageCommand;
import bot.command.parser.MessageCommandParser;
import bot.command.verification.RoleCheck;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import sql.Session;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HelpCommand
{
    private static final String NAME = "help";
    private static final String DESCRIPTION = "Get some help";
    private static String prefix;
    private static String botName;

    private HelpCommand()
    {
    }

    public static HelpMessageCommand createCommand(String p, String name)
    {
        prefix = p;
        botName = name;
        return new HelpMessageCommand(NAME, DESCRIPTION, HelpCommand::function);
    }

    public static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session, MessageCommandParser parser)
    {
        HelpFunctionality functionality = new HelpFunctionality(api, info, vars, session, parser);
        functionality.execute();
    }

    private static class HelpFunctionality
    {
        final DiscordApi api;
        final MessageReceivedInformation info;
        final Session session;
        final MessageCommandParser parser;
        final MessageCommand command;

        HelpFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session, MessageCommandParser parser)
        {
            this.api = api;
            this.info = info;
            this.session = session;
            this.parser = parser;
            command = parser.getCommand(vars);
        }

        void execute()
        {
            if (command != null && !RoleCheck.hasPermission(session, api, info.getUser(), command.getRequirement()))
                return;

            EmbedBuilder builder = new EmbedBuilder();
            String author = "Help with " + botName;
            Color color = new Color(97, 185, 221);
            builder.setAuthor(author).setColor(color)
                    .setDescription(command == null ? getAllCommandsDescription() : getCommandDescription());

            DMessage.sendMessage(info.getChannel(), builder);
        }

        String getAllCommandsDescription()
        {
            StringBuilder combined = new StringBuilder();
            combined.append("__**Commands:**__\n");

            for (MessageCommand messageCommand: parser.getCommands())
            {
                if (!RoleCheck.hasPermission(session, api, info.getUser(), messageCommand.getRequirement()))
                    continue;

                String desc = messageCommand.getDescription();

                String singleCommand;
                if (desc == null)
                    singleCommand = "**" + prefix + messageCommand.getName() + "**" + "\n\n";
                else
                    singleCommand =  "**" + prefix + messageCommand.getName() + "** " + desc + "\n\n";
                combined.append(singleCommand);
            }

            return combined.toString();
        }

        String getCommandDescription()
        {
            StringBuilder combined = new StringBuilder();
            String title = "`" + prefix + command.getSyntax() + "`\n";
            combined.append(title);

            String desc = command.getDescription();
            if (desc != null)
                desc += "\n";
            combined.append(desc);

            if (command.getSubCommands() != null && command.getSubCommands().size() > 0)
            {
                combined.append("\n__**Sub-commands:**__\n");
                List<MessageCommand> commands = new ArrayList<>(command.getSubCommands().values());
                commands.sort(Comparator.comparing(MessageCommand::getName));
                for (MessageCommand sub: commands)
                {
                    if (!RoleCheck.hasPermission(session, api, info.getUser(), sub.getRequirement()))
                        continue;

                    desc = sub.getDescription();

                    String subString;
                    if (desc == null)
                        subString = "**" + sub.getName() + "**\n";
                    else
                        subString = "**" + sub.getName() + "** " + sub.getDescription() + "\n";

                    combined.append(subString);
                }
                combined.deleteCharAt(combined.length() - 1);
            }

            return combined.toString();
        }
    }
}

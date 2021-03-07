package bot.command.definition.get;

import bot.command.MessageCommand;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.discord.role.DRole;
import exception.bot.argument.MissingArgumentException;
import bot.util.CombineContent;
import bot.util.IdExtractor;
import exception.bot.command.InvalidCommandException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import sql.Session;

import java.awt.Color;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GetRoleCommand
{
    private static final String NAME = "role";
    private static final String DESCRIPTION = "Get role information";
    private static final String SYNTAX = "<role>";

    private GetRoleCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .executor(GetRoleCommand::function).build();
    }

    public static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (vars.isEmpty())
            throw new MissingArgumentException("role");
        GetRoleFunctionality functionality = new GetRoleFunctionality(api, info, vars);
        functionality.execute();
    }

    public static Role getRole(MessageReceivedInformation info, long id, String name)
    {
        return DRole.getRole(info.getServer(), id, name);
    }

    private static class GetRoleFunctionality
    {
        final DiscordApi api;
        final MessageReceivedInformation info;
        final String name;
        final Long id;

        GetRoleFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars)
        {
            this.api = api;
            this.info = info;
            this.name = CombineContent.combine(vars);
            if (vars.size() == 1)
                id = IdExtractor.getId(name);
            else
                id = 0L;
        }

        void execute()
        {
            Role role = getRole(info, id, name);

            if (role == null)
                throw new InvalidCommandException("Role `" + name + "` could not be found.");

            EmbedBuilder builder = roleEmbed(role);
            DMessage.sendMessage(info.getChannel(), builder);
        }

        private EmbedBuilder roleEmbed(Role role)
        {
            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle("Role: @" + role.getName())
                    .addField("ID:", role.getIdAsString(), true)
                    .addField("Server:", role.getServer().getName(), true)
                    .addField("Creation Date:", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"))
                            .format(role.getCreationTimestamp()) + " UTC", true)
                    .addField("Is Mentionable:", role.isMentionable() ? "Yes" : "No", true)
                    .addField("Member Count:", Integer.toString(role.getUsers().size()), true);

            role.getColor().ifPresentOrElse(builder::setColor, () -> builder.setColor(Color.WHITE));

            return builder;
        }
    }
}

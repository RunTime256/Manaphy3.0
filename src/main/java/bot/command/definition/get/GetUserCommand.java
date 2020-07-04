package bot.command.definition.get;

import bot.command.MessageCommand;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.discord.user.DUser;
import bot.util.CombineContent;
import bot.util.IdExtractor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import sql.Session;

import java.awt.Color;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GetUserCommand
{
    private static final String NAME = "user";
    private static final String DESCRIPTION = "Get user information";
    private static final String SYNTAX = "<user>";

    private GetUserCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .executor(GetUserCommand::function).build();
    }

    public static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        GetUserFunctionality functionality = new GetUserFunctionality(api, info, vars);
        functionality.execute();
    }

    private static class GetUserFunctionality
    {
        final DiscordApi api;
        final MessageReceivedInformation info;
        final String name;
        final Long id;

        GetUserFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars)
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
            User user;
            if (name.isBlank())
                user = info.getUser();
            else
                user = DUser.getUser(info.getServer(), id, name);

            if (user != null)
            {
                EmbedBuilder builder = userEmbed(user);
                DMessage.sendMessage(info.getChannel(), builder);
            }
            else
            {
                DMessage.sendMessage(info.getChannel(), "User `" + name + "` could not be found.");
            }
        }

        private EmbedBuilder userEmbed(User user)
        {
            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle("User: @" + user.getDiscriminatedName())
                    .addField("ID:", user.getIdAsString(), true);

            user.getNickname(info.getServer()).ifPresent(nickname -> builder.addField("Nickname:", nickname, true));

            builder.addField("Account Created:", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"))
                            .format(user.getCreationTimestamp()) + " UTC", true);

            user.getJoinedAtTimestamp(info.getServer()).ifPresent(timestamp ->
                    builder.addField("Joined Server:", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"))
                            .format(timestamp) + " UTC", true));

            int roles = user.getRoles(info.getServer()).size();
            if (roles > 0)
                builder.addField("Role Count:", Integer.toString(roles), true);

            user.getRoleColor(info.getServer()).ifPresentOrElse(builder::setColor, () -> builder.setColor(Color.WHITE));

            if (user.isBotOwner())
                builder.setDescription("[Bot Owner]");

            if (user.isBot())
            {
                String desc = "[Bot]";
                if (user.isYourself())
                    desc += " (Me!)";
                builder.setDescription(desc);
            }

            builder.setThumbnail(user.getAvatar().getUrl().toString());

            return builder;
        }
    }
}

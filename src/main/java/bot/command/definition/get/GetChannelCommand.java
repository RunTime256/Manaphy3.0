package bot.command.definition.get;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.channel.DChannel;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.exception.argument.MissingArgumentException;
import bot.util.CombineContent;
import bot.util.IdExtractor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Categorizable;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import sql.Session;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GetChannelCommand
{
    private static final String NAME = "text_channel";
    private static final String DESCRIPTION = "Get text channel information";
    private static final String SYNTAX = "<channel>";

    private GetChannelCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .requirement(RoleRequirement.OWNER).executor(GetChannelCommand::function).build();
    }

    public static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (vars.isEmpty())
            throw new MissingArgumentException("channel");
        GetChannelFunctionality functionality = new GetChannelFunctionality(api, info, vars);
        functionality.execute();
    }

    private static class GetChannelFunctionality
    {
        final DiscordApi api;
        final MessageReceivedInformation info;
        final String name;
        final Long id;

        GetChannelFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars)
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
            ServerChannel channel = DChannel.getServerChannel(info.getServer(), id, name);
            if (channel != null)
            {
                EmbedBuilder builder = serverChannelEmbed(channel);
                DMessage.sendMessage(info.getChannel(), builder);
            }
            else
            {
                DMessage.sendMessage(info.getChannel(), "Server channel " + name + " could not be found.");
            }
        }

        private EmbedBuilder serverChannelEmbed(ServerChannel channel)
        {
            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle("Text Channel: #" + channel.getName())
                    .addField("ID:", channel.getIdAsString(), true)
                    .addField("Server:", channel.getServer().getName(), true)
                    .addField("Creation Date:", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"))
                            .format(channel.getCreationTimestamp()) + " UTC", true);

            channel.asCategorizable().flatMap(Categorizable::getCategory)
                    .ifPresent(category -> builder.addField("Category:", category.getName(), true));

            return builder;
        }
    }
}

package bot.command.definition.get;

import bot.command.MessageCommand;
import bot.discord.channel.DChannel;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.util.CombineContent;
import bot.util.IdExtractor;
import exception.bot.command.InvalidCommandException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Categorizable;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import sql.Session;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GetTextChannelCommand
{
    private static final String NAME = "text_channel";
    private static final String DESCRIPTION = "Get text channel information";
    private static final String SYNTAX = "<channel>";

    private GetTextChannelCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .executor(GetTextChannelCommand::function).build();
    }

    public static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        GetTextChannelFunctionality functionality = new GetTextChannelFunctionality(api, info, vars);
        functionality.execute();
    }

    public static ServerTextChannel getTextChannel(MessageReceivedInformation info, Long id, String name)
    {
        ServerTextChannel channel;
        if (name.isBlank())
            channel = info.getChannel().asServerTextChannel().orElse(null);
        else
            channel = DChannel.getServerChannel(info.getServer(), id, name);
        return channel;
    }

    private static class GetTextChannelFunctionality
    {
        final DiscordApi api;
        final MessageReceivedInformation info;
        final String name;
        final Long id;

        GetTextChannelFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars)
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
            ServerTextChannel channel = getTextChannel(info, id, name);

            if (channel == null)
                throw new InvalidCommandException("Server channel `" + name + "` could not be found.");

            EmbedBuilder builder = serverChannelEmbed(channel);
            DMessage.sendMessage(info.getChannel(), builder);
        }

        private EmbedBuilder serverChannelEmbed(ServerTextChannel channel)
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

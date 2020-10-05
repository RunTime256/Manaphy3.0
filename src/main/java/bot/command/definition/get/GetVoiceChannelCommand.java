package bot.command.definition.get;

import bot.command.MessageCommand;
import bot.discord.channel.DChannel;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import exception.bot.argument.MissingArgumentException;
import bot.util.CombineContent;
import bot.util.IdExtractor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Categorizable;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import sql.Session;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GetVoiceChannelCommand
{
    private static final String NAME = "voice_channel";
    private static final String DESCRIPTION = "Get voice channel information";
    private static final String SYNTAX = "<channel>";

    private GetVoiceChannelCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .executor(GetVoiceChannelCommand::function).build();
    }

    public static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (vars.isEmpty())
            throw new MissingArgumentException("channel");
        GetVoiceChannelFunctionality functionality = new GetVoiceChannelFunctionality(api, info, vars);
        functionality.execute();
    }

    public static ServerVoiceChannel getVoiceChannel(MessageReceivedInformation info, Long id, String name)
    {
        return DChannel.getVoiceChannel(info.getServer(), id, name);
    }

    private static class GetVoiceChannelFunctionality
    {
        final DiscordApi api;
        final MessageReceivedInformation info;
        final String name;
        final Long id;

        GetVoiceChannelFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars)
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
            ServerVoiceChannel channel = getVoiceChannel(info, id, name);

            if (channel != null)
            {
                EmbedBuilder builder = voiceChannelEmbed(channel);
                DMessage.sendMessage(info.getChannel(), builder);
            }
            else
            {
                DMessage.sendMessage(info.getChannel(), "Server channel `" + name + "` could not be found.");
            }
        }

        private EmbedBuilder voiceChannelEmbed(ServerVoiceChannel voiceChannel)
        {
            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle("Voice Channel: " + voiceChannel.getName())
                    .addField("ID:", voiceChannel.getIdAsString(), true)
                    .addField("Server:", voiceChannel.getServer().getName(), true)
                    .addField("Creation Date:", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("UTC"))
                            .format(voiceChannel.getCreationTimestamp()) + " UTC", true);

            voiceChannel.asCategorizable().flatMap(Categorizable::getCategory)
                    .ifPresent(category -> builder.addField("Category:", category.getName(), true));

            int users = voiceChannel.getConnectedUsers().size();
            if (users > 0)
                builder.addField("Current User Count:", Integer.toString(users));
            voiceChannel.getUserLimit().ifPresent(limit -> builder.addField("User Limit", Integer.toString(limit)));

            builder.addField("Bitrate:", Integer.toString(voiceChannel.getBitrate()), true);

            return builder;
        }
    }
}

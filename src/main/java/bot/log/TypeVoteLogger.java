package bot.log;

import bot.discord.channel.DChannel;
import bot.discord.message.DMessage;
import bot.discord.user.DUser;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import war.typevote.TypeVoteSelection;

import java.awt.Color;

public class TypeVoteLogger
{
    private final DiscordApi api;
    private final long channelId;

    public TypeVoteLogger(DiscordApi api, long channelId)
    {
        this.api = api;
        this.channelId = channelId;
    }

    public void log(TypeVoteSelection selection)
    {
        TextChannel channel = DChannel.getChannel(api, channelId);
        DMessage.sendMessage(channel, typeVoteEmbed(selection));
    }

    private EmbedBuilder typeVoteEmbed(TypeVoteSelection selection)
    {
        EmbedBuilder builder = new EmbedBuilder();
        User user = DUser.getUser(api, selection.getUserId());

        builder.setAuthor(user.getDiscriminatedName(), null, user.getAvatar()).setDescription("New type vote")
                .addField("Type", selection.getType())
                .addField("User ID", String.valueOf(selection.getUserId()))
                .setColor(new Color(254, 254, 254));

        return builder;
    }
}

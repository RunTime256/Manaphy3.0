package bot.discord.message;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.Color;

public class DMessage
{
    private DMessage()
    {
    }

    public static void sendMessage(TextChannel channel, String message)
    {
        channel.sendMessage(message);
    }

    public static void sendMessage(TextChannel channel, Exception e)
    {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Exception occurred: " + e.getClass().getName()).setColor(Color.RED);
        channel.sendMessage(builder);
    }
}

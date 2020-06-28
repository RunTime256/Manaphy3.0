package bot.discord.message;

import org.javacord.api.entity.channel.TextChannel;

public class DMessage
{
    public static void sendMessage(TextChannel channel, String message)
    {
        channel.sendMessage(message);
    }
}

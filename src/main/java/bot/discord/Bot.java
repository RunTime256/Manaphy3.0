package bot.discord;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.listener.message.MessageCreateListener;

public class Bot
{
    private String token;
    private String prefix;
    private DiscordApi api;

    public Bot(String token, String prefix)
    {
        this.token = token;
        this.prefix = prefix;
    }

    public void addListener(MessageCreateListener listener)
    {
        api.addMessageCreateListener(listener);
    }

    public void start()
    {
        api = new DiscordApiBuilder().setToken(token).login().join();
    }

    public void stop()
    {
        api.disconnect();
    }

    public String getPrefix()
    {
        return prefix;
    }
}

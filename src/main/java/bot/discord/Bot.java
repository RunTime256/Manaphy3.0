package bot.discord;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.listener.message.MessageCreateListener;

public class Bot
{
    private final String name;
    private final String token;
    private final String prefix;
    private final String botPrefix = "=";
    private DiscordApi api;

    public Bot(String name, String token, String prefix)
    {
        this.name = name;
        this.token = token;
        this.prefix = prefix;
    }

    public void addListener(MessageCreateListener listener)
    {
        api.addMessageCreateListener(listener);
    }

    public DiscordApi start()
    {
        api = new DiscordApiBuilder().setToken(token).login().join();
        return api;
    }

    public void stop()
    {
        api.disconnect();
    }

    public String getPrefix()
    {
        return prefix;
    }

    public String getBotPrefix()
    {
        return botPrefix;
    }

    public String getName()
    {
        return name;
    }
}

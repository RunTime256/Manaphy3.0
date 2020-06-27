package bot.discord;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

public class Bot
{
    private String token;
    private String prefix;
    private DiscordApi api;

    public Bot(String token, String prefix)
    {
        this.token = token;
    }

    public void start()
    {
        api = new DiscordApiBuilder().setToken(token).login().join();
    }

    public void stop()
    {
        api.disconnect();
    }
}

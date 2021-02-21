package exception.discord;

import exception.BotException;

import java.awt.Color;

public class DiscordException extends BotException
{
    public DiscordException(Color color)
    {
        super(color);
    }

    public DiscordException(Color color, String message)
    {
        super(color, message);
    }
}

package exception.discord.user;

import exception.discord.DiscordException;

import java.awt.Color;

public class UserException extends DiscordException
{
    public UserException(Color color)
    {
        super(color);
    }

    public UserException(Color color, String message)
    {
        super(color, message);
    }
}

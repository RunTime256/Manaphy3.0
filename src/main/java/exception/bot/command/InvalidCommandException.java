package exception.bot.command;

import exception.BotException;

import java.awt.*;

public class InvalidCommandException extends BotException
{
    public InvalidCommandException(String message)
    {
        super(Color.YELLOW, message);
    }
}

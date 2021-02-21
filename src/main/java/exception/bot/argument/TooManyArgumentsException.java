package exception.bot.argument;

import exception.BotException;

import java.awt.Color;

public class TooManyArgumentsException extends BotException
{

    public TooManyArgumentsException()
    {
        super(Color.YELLOW, "Too many arguments were provided");
    }
}

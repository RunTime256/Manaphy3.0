package exception.bot.argument;

import exception.BotException;

import java.awt.Color;

public class NoExecutorException extends BotException
{
    public NoExecutorException()
    {
        super(Color.YELLOW, "Command does not have an executor");
    }
}

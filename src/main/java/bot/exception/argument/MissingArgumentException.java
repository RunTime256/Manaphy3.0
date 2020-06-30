package bot.exception.argument;

import bot.exception.BotException;

import java.awt.Color;

public class MissingArgumentException extends BotException
{
    private final String argument;

    public MissingArgumentException(String argument)
    {
        super(Color.YELLOW, argument + " is a required argument");
        this.argument = argument;
    }

    public String getArgument()
    {
        return argument;
    }
}

package bot.exception.argument;

import bot.exception.BotException;

import java.awt.Color;

public class InvalidArgumentException extends BotException
{
    private final String argument;

    public InvalidArgumentException(String argument)
    {
        super(Color.YELLOW, argument + " could not be parsed correctly");
        this.argument = argument;
    }

    public String getArgument()
    {
        return argument;
    }
}

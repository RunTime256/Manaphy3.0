package exception.war;

import exception.BotException;

import java.awt.Color;

public class WarException extends BotException
{
    private final Color color;

    public WarException(Color color)
    {
        this(color, "Generic war exception occurred");
    }

    public WarException(Color color, String message)
    {
        super(color, message);
        this.color = color;
    }

    public Color getColor()
    {
        return color;
    }
}

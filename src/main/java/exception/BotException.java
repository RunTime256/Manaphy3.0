package exception;

import java.awt.Color;

public class BotException extends RuntimeException
{
    private final Color color;

    public BotException(Color color)
    {
        this(color, "Generic exception occurred");
    }

    public BotException(Color color, String message)
    {
        super(message);
        this.color = color;
    }

    public Color getColor()
    {
        return color;
    }
}

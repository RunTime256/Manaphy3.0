package exception.war.achievement;

import exception.BotException;

import java.awt.Color;

public class AchievementException extends BotException
{
    public AchievementException(Color color)
    {
        super(color);
    }

    public AchievementException(Color color, String message)
    {
        super(color, message);
    }
}

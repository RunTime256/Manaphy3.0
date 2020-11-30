package exception.war.achievement;

import java.awt.Color;

public class AchievementAlreadyExistsException extends AchievementException
{
    public AchievementAlreadyExistsException(String name)
    {
        super(Color.YELLOW, "The achievement `" + name + "` already exists");
    }
}

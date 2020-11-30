package exception.war.achievement;

import java.awt.Color;

public class NotAnAchievementException extends AchievementException
{
    public NotAnAchievementException(String name)
    {
        super(Color.YELLOW, "The achievement `" + name + "` does not exist");
    }
}

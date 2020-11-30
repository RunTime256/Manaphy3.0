package exception.war.achievement;

import java.awt.Color;

public class NotAnAchievementCategoryException extends AchievementException
{
    public NotAnAchievementCategoryException(String name)
    {
        super(Color.YELLOW, "The category `" + name + "` does not exist");
    }
}

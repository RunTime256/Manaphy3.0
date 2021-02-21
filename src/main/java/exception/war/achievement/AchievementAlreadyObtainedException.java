package exception.war.achievement;

import java.awt.Color;

public class AchievementAlreadyObtainedException extends AchievementException
{
    public AchievementAlreadyObtainedException(long userId, String name)
    {
        super(Color.YELLOW, "The achievement `" + name + "` has already been achieved by `" + userId + "`");
    }
}

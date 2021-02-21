package exception.war.contest;

import java.awt.Color;

public class NotAContestException extends ContestException
{
    public NotAContestException(String name)
    {
        super(Color.YELLOW, "The contest `" + name + "` does not exist");
    }
}

package exception.war.contest;

import java.awt.Color;

public class NotAContestPlaceException extends ContestException
{
    public NotAContestPlaceException(String name, int place)
    {
        super(Color.YELLOW, "The place `" + place + "` for contest `" + name + "` does not exist");
    }
}

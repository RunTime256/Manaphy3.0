package exception.war.team;

import java.awt.Color;

public class SameTeamException extends TeamException
{
    public SameTeamException(long user1, long user2)
    {
        super(Color.YELLOW, "`" + user1 + "` and `" + user2 + "` are on the same team");
    }
}

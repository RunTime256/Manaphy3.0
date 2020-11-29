package exception.war.team;

import java.awt.Color;

public class NotATeamMemberException extends TeamException
{
    public NotATeamMemberException(long userId)
    {
        super(Color.YELLOW, userId + " is not a member of the war");
    }
}

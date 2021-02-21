package exception.war.team;

import java.awt.Color;

public class BannedMemberException extends TeamException
{
    public BannedMemberException(long userId)
    {
        super(Color.YELLOW, userId + " is banned from the war");
    }
}

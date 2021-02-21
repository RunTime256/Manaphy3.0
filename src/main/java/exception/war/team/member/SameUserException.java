package exception.war.team.member;

import java.awt.Color;

public class SameUserException extends MemberException
{
    public SameUserException(long user1, long user2)
    {
        super(Color.YELLOW, "`" + user1 + "` and `" + user2 + "` are the same");
    }
}

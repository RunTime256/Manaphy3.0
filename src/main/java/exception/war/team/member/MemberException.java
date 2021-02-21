package exception.war.team.member;

import exception.war.team.TeamException;

import java.awt.Color;

public class MemberException extends TeamException
{
    public MemberException(Color color)
    {
        super(color);
    }

    public MemberException(Color color, String message)
    {
        super(color, message);
    }
}

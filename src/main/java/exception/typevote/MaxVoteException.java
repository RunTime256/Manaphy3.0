package exception.typevote;

import exception.war.WarException;

import java.awt.Color;

public class MaxVoteException extends WarException
{
    public MaxVoteException()
    {
        super(Color.YELLOW, "You have reached your max type vote count");
    }
}

package exception.typevote;

import java.awt.Color;

public class MaxVoteException extends TypeVoteException
{
    public MaxVoteException()
    {
        super(Color.YELLOW, "You have reached your max type vote count");
    }
}

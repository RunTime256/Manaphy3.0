package exception.typevote;

import java.awt.Color;

public class InvalidTypeException extends TypeVoteException
{
    public InvalidTypeException(String type)
    {
        super(Color.YELLOW, "`" + type + "` is not a valid type");
    }
}

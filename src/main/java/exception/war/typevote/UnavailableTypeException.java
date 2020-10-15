package exception.war.typevote;

import java.awt.Color;

public class UnavailableTypeException extends TypeVoteException
{
    public UnavailableTypeException(String type)
    {
        super(Color.YELLOW, "`" + type + "` is not an available type");
    }
}

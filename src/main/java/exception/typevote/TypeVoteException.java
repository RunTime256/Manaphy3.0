package exception.typevote;

import exception.war.WarException;

import java.awt.Color;

public class TypeVoteException extends WarException
{
    public TypeVoteException(Color color, String message)
    {
        super(color, message);
    }
}

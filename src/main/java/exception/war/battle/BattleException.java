package exception.war.battle;

import exception.war.WarException;

import java.awt.Color;

public class BattleException extends WarException
{
    public BattleException(Color color)
    {
        super(color);
    }

    public BattleException(Color color, String message)
    {
        super(color, message);
    }
}

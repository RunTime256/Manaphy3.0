package exception.war.battle;

import java.awt.Color;

public class BannedBattleFormatException extends BattleException
{
    public BannedBattleFormatException(String format)
    {
        super(Color.YELLOW, "The format `" + format + "` is a banned format");
    }
}

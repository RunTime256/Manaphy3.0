package exception.war.battle;

import java.awt.Color;

public class InvalidBattleUrlException extends BattleException
{
    public InvalidBattleUrlException(String url)
    {
        super(Color.YELLOW, "The url `" + url + "` is not valid");
    }
}

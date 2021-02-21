package exception.war.battle;

import java.awt.Color;

public class DuplicateBattleUrlException extends BattleException
{
    public DuplicateBattleUrlException(String url)
    {
        super(Color.YELLOW, "The url `" + url + "` has already been used");
    }
}

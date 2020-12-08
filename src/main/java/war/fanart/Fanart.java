package war.fanart;

import sql.Session;

public class Fanart
{
    public static void addParticipant(long userId, Session session)
    {
        session.getMapper(FanartMapper.class).addParticipant(userId);
    }

    public static void addBonus(long userId, Session session)
    {
        session.getMapper(FanartMapper.class).addBonus(userId);
    }
}

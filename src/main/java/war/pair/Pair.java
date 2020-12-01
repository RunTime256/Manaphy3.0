package war.pair;

import sql.Session;

public class Pair
{
    public static String getValue(String key, Session session)
    {
        return session.getMapper(PairMapper.class).getValue(key);
    }
}

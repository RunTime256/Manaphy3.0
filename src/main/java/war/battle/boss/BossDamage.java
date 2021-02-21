package war.battle.boss;

import sql.Session;

public class BossDamage
{
    private BossDamage()
    {
    }

    public static BossHealth getHealth(String bossName, Session session)
    {
        return session.getMapper(BossDamageMapper.class).getHealth(bossName);
    }

    public static String getBossImage(String bossName, Session session)
    {
        return session.getMapper(BossDamageMapper.class).getBossImage(bossName);
    }

    public static BossMessage getBossMessage(Session session)
    {
        return session.getMapper(BossDamageMapper.class).getBossMessage();
    }

    public static void addDamage(String bossName, int damage, String battleUrl, Session session)
    {
        session.getMapper(BossDamageMapper.class).addDamage(bossName, damage, battleUrl);
    }
}

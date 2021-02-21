package war.battle.boss;

public class WarBoss
{
    private final String name;
    private final String image;
    private final int damage;
    private final BossHealth health;

    public WarBoss(String name, String image, BossHealth health, int damage)
    {
        this.name = name;
        this.image = image;
        this.health = health;
        this.damage = damage;
    }

    public String getName()
    {
        return name;
    }

    public String getImage()
    {
        return image;
    }

    public int getCurrentHealth()
    {
        return health.getCurrentHealth() - damage;
    }

    public int getTotalHealth()
    {
        return health.getTotalHealth();
    }

    public int getDamage()
    {
        return damage;
    }

    public String getHealthEmojis()
    {
        int max = 10;
        int current = getCurrentHealth();
        int dec = current * max / getTotalHealth();

        String life = "\uD83D\uDFE9";
        String lost = "\uD83D\uDFE5";
        StringBuilder healthEmojis = new StringBuilder();

        for (int i = 0; i < max; i++)
        {
            String add;
            if (i > dec || current <= 0)
                add = lost;
            else
                add = life;
            healthEmojis.append(add);
        }

        return healthEmojis.toString();
    }
}

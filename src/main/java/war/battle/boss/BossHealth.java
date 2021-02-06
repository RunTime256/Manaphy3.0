package war.battle.boss;

public class BossHealth
{
    private final int currentHealth;
    private final int totalHealth;

    public BossHealth(int currentHealth, int totalHealth)
    {
        this.currentHealth = currentHealth;
        this.totalHealth = totalHealth;
    }

    public int getCurrentHealth()
    {
        return currentHealth;
    }

    public int getTotalHealth()
    {
        return totalHealth;
    }
}

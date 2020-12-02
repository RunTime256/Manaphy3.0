package war.battle;

import java.time.Duration;
import java.time.Instant;

public class PreviousBattleMultiplier
{
    private final Instant timestamp;
    private final int multiplier;
    private final int multiplierCount;
    private static final int[] times = {1, 3, 6};
    private static final int[] multipliers = {4, 7, 10};

    public PreviousBattleMultiplier(int multiplier, int multiplierCount)
    {
        timestamp = null;
        this.multiplier = multiplier;
        this.multiplierCount = multiplierCount;
    }

    public PreviousBattleMultiplier(Instant timestamp, int multiplier, int multiplierCount)
    {
        this.timestamp = timestamp;
        this.multiplier = multiplier;
        this.multiplierCount = multiplierCount;
    }

    public Instant getTimestamp()
    {
        return timestamp;
    }

    public int getNewMultiplier(Instant currentTime)
    {
        if (timestamp == null)
            return multiplier;

        long hours = Duration.between(timestamp, currentTime).toHours();
        if (multiplierCount >= 5)
        {
            for (int i = times.length - 1; i >= 0; i--)
            {
                if (hours >= times[i])
                    return multipliers[i];
            }
        }
        else
        {
            for (int i = multipliers.length - 1; i >= 0; i--)
            {
                if (hours >= times[i] && multiplier < multipliers[i] || multiplier == multipliers[i])
                    return multipliers[i];
            }
        }
        return 1;
    }

    public int getNewMultiplierCount(Instant currentTime)
    {
        if (timestamp == null)
            return multiplierCount + 1;

        long hours = Duration.between(timestamp, currentTime).toHours();
        if (multiplierCount >= 5)
        {
            if (multiplier != 1)
                return 1;
        }
        else
        {
            for (int i = times.length - 1; i >= 0; i--)
            {
                if (hours >= times[i])
                    return 1;
            }
        }
        return multiplierCount + 1;
    }
}

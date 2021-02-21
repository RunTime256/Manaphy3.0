package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates a weighted random number for team distribution that favors teams with less members more
 */
public class WeightedRandomNumber
{
    private final Random random;
    private final List<Integer> distribution;
    private List<Double> weights;
    private double totalWeight;

    private static final int RANDOM_BASE = 10000;

    /**
     * Constructs with a base to divide from and a distribution of team counts
     * @param distribution distribution of team counts
     */
    public WeightedRandomNumber(List<Integer> distribution)
    {
        this.distribution = distribution;

        totalWeight = 0;
        weights = new ArrayList<>();
        random = new Random();
    }

    /**
     * Calculate a weighted random number as an integer index
     * @return weighted random number index
     */
    public int calculateNewMemberLocation()
    {
        double weight = calculateWeights(3);
        return randomlySelect(weight);
    }

    public int calculateComplexMemberLocation(int value)
    {
        double weight = calculateComplexWeights(value);
        return randomlySelect(weight);
    }

    /**
     * Determines a weighted random index based on individual weights
     * @param max total weight
     * @return weighted random number index
     */
    private int randomlySelect(double max)
    {
        double rand = random.nextDouble() * max;

        double total = 0;

        for (int i = 0; i < weights.size(); i++)
        {
            double weight = weights.get(i);
            // Check random number is in the range of the weight
            if (rand < total + weight)
                return i;
            else
                total += weight;
        }

        return weights.size() - 1;
    }

    /**
     * Calculates individual rates and total weight
     * @return total weight
     */
    private double calculateWeights(int power)
    {
        weights = new ArrayList<>();
        totalWeight = 0;

        for (double count : distribution)
        {
            // Add 1 to not divide by 0
            double weight = RANDOM_BASE / (count + 1);
            weight = Math.pow(weight, power);
            totalWeight += weight;

            weights.add(weight);
        }

        return totalWeight;
    }

    private double calculateComplexWeights(int value)
    {
        weights = new ArrayList<>();
        totalWeight = 0;

        double newAverage = value;
        for (int integer : distribution)
        {
            newAverage += integer;
        }
        newAverage /= distribution.size();

        for (int dist: distribution)
        {
            int newValue = (int)Math.pow(1.0 * dist + value, 2);

            double weight = RANDOM_BASE / Math.pow(newValue - newAverage, 2);
            totalWeight += weight;

            weights.add(weight);
        }

        return totalWeight;
    }
}
package bot.command.definition.achievements;

public class Achievement {
    private String identifier;
    private String name;
    private AchievementCategory category;
    private String description;
    private String unlockMethod;
    private Boolean singleTime;

    public String getIdentifier() {
        return identifier;
    }

    public String getName()
    {
        return name;
    }

    public AchievementCategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getUnlockMethod() {
        return unlockMethod;
    }

    public String getCategoryEmoji() {
        return getCategoryEmoji(category);
    }

    public static String getCategoryEmoji(AchievementCategory category){
        switch (category) {
            case WARRIOR: return "\u1F5E1\uFE0F";
            case ARTISAN: return "\u1F58C\uFE0F";
            case ORACLE: return "\u1F52E";
            case SECRET: return "\u2728";
        }
        return null;
    }

    public Boolean isSingleTime() {
        return singleTime;
    }
}

package war.achievement;

public class WarAchievement
{
    private String identifier;
    private String name;
    private String category;
    private String description;
    private String unlockMethod;

    public String getIdentifier() {
        return identifier;
    }

    public String getName()
    {
        return name;
    }

    public String getCategory() {
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

    public static String getCategoryEmoji(String category){
        switch (category) {
            case "warrior": return "\u1F5E1\uFE0F";
            case "artisan": return "\u1F58C\uFE0F";
            case "oracle": return "\u1F52E";
            case "secret": return "\u2728";
        }
        return null;
    }
}

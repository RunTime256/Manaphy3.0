package war.achievement;

public class WarAchievement
{
    private final String name;
    private final String fullName;
    private final String description;
    private final String category;
    private final String image;
    private final String unlockMethod;
    private final int value;

    public WarAchievement(String name, String fullName, String description, String category, String image, String unlockMethod, int value)
    {
        this.name = name;
        this.fullName = fullName;
        this.description = description;
        this.category = category;
        this.image = image;
        this.unlockMethod = unlockMethod;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public String getFullName()
    {
        return fullName;
    }

    public String getDescription()
    {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getImage() {
        return image;
    }

    public String getUnlockMethod() {
        return unlockMethod;
    }

    public String getCategoryEmoji() {
        return getCategoryEmoji(category);
    }

    public int getValue()
    {
        return value;
    }

    public static String getCategoryEmoji(String category) {
        switch (category) {
            case "warrior": return "\uD83D\uDDE1";
            case "artisan": return "\uD83C\uDFA8";
            case "oracle": return "\uD83D\uDD2E";
            case "secret": return "\u2728";
        }
        return null;
    }
}

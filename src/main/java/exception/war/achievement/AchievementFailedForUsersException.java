package exception.war.achievement;

import java.awt.Color;
import java.util.List;

public class AchievementFailedForUsersException extends AchievementException
{
    public AchievementFailedForUsersException(List<Long> bannedMembers, List<Long> alreadyObtained, List<Long> notUsers)
    {
        super(Color.YELLOW, messageString(bannedMembers, alreadyObtained, notUsers));
    }

    private static String messageString(List<Long> bannedMembers, List<Long> alreadyObtained, List<Long> notUsers)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Achievement failed for multiple users\n\n");
        if (!bannedMembers.isEmpty())
        {
            builder.append("__Banned__:\n");
            for (long userId: bannedMembers)
            {
                String temp = userId + "\n";
                builder.append(temp);
            }
        }
        if (!alreadyObtained.isEmpty())
        {
            builder.append("__Already Obtained__:\n");
            for (long userId : alreadyObtained)
            {
                String temp = userId + "\n";
                builder.append(temp);
            }
        }
        if (!notUsers.isEmpty())
        {
            builder.append("__Not Users__:\n");
            for (long userId : notUsers)
            {
                String temp = userId + "\n";
                builder.append(temp);
            }
        }

        return builder.toString();
    }
}

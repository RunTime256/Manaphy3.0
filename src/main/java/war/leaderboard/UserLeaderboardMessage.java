package war.leaderboard;

public class UserLeaderboardMessage
{
    private final String teamName;
    private final long channelId;
    private final long messageId;
    private final String category;

    public UserLeaderboardMessage(String teamName, long channelId, long messageId, String category)
    {
        this.teamName = teamName;
        this.channelId = channelId;
        this.messageId = messageId;
        this.category = category;
    }

    public String getTeamName()
    {
        return teamName;
    }

    public long getChannelId()
    {
        return channelId;
    }

    public long getMessageId()
    {
        return messageId;
    }

    public String getCategory()
    {
        return category;
    }
}

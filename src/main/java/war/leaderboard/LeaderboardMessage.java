package war.leaderboard;

public class LeaderboardMessage
{
    private final long channelId;
    private final long messageId;
    private final String category;

    public LeaderboardMessage(long channelId, long messageId, String category)
    {
        this.channelId = channelId;
        this.messageId = messageId;
        this.category = category;
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

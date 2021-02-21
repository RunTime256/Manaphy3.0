package war.battle.boss;

public class BossMessage
{
    private final long channelId;
    private final long messageId;

    public BossMessage(long channelId, long messageId)
    {
        this.channelId = channelId;
        this.messageId = messageId;
    }

    public long getChannelId()
    {
        return channelId;
    }

    public long getMessageId()
    {
        return messageId;
    }
}

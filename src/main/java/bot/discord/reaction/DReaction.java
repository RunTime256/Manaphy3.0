package bot.discord.reaction;

import org.javacord.api.entity.message.Message;

import java.util.concurrent.CompletableFuture;

public class DReaction
{
    private DReaction()
    {
    }

    public static CompletableFuture<Void> addReaction(Message message, String reactionUnicode)
    {
        return message.addReaction(reactionUnicode);
    }
}

package bot.log;

import bot.discord.channel.DChannel;
import bot.discord.message.DMessage;
import bot.discord.user.DUser;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import war.puzzle.PuzzleGuess;

import java.awt.Color;

public class PuzzleLogger
{
    private final DiscordApi api;
    private final long channelId;

    public PuzzleLogger(DiscordApi api, long channelId)
    {
        this.api = api;
        this.channelId = channelId;
    }

    public void log(PuzzleGuess guess, boolean correct)
    {
        TextChannel channel = DChannel.getChannel(api, channelId);
        DMessage.sendMessage(channel, puzzleEmbed(guess, correct));
    }

    private EmbedBuilder puzzleEmbed(PuzzleGuess guess, boolean correct)
    {
        EmbedBuilder builder = new EmbedBuilder();

        builder.setTitle(DUser.getUser(api, guess.getUserId()).getDiscriminatedName()).setDescription("New puzzle guess")
                .addField("Puzzle", guess.getName())
                .addField("User ID", String.valueOf(guess.getUserId()))
                .addField("Guess", guess.getGuess());

        if (correct)
            builder.setColor(Color.GREEN);
        else
            builder.setColor(Color.RED);

        return builder;
    }
}

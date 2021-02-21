package bot.log;

import bot.discord.channel.DChannel;
import bot.discord.message.DMessage;
import bot.discord.user.DUser;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
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

    public void logMissingRequirement(PuzzleGuess guess)
    {
        TextChannel channel = DChannel.getChannel(api, channelId);
        DMessage.sendMessage(channel, missingRequirementPuzzleEmbed(guess));
    }

    private EmbedBuilder puzzleEmbed(PuzzleGuess guess, boolean correct)
    {
        EmbedBuilder builder = new EmbedBuilder();
        User user = DUser.getUser(api, guess.getUserId());

        builder.setAuthor(user.getDiscriminatedName(), null, user.getAvatar()).setDescription("New puzzle guess")
                .addField("Puzzle", guess.getName())
                .addField("User ID", String.valueOf(guess.getUserId()))
                .addField("Guess", guess.getGuess());

        if (correct)
            builder.setColor(Color.GREEN);
        else
            builder.setColor(Color.RED);

        return builder;
    }

    private EmbedBuilder missingRequirementPuzzleEmbed(PuzzleGuess guess)
    {
        EmbedBuilder builder = new EmbedBuilder();
        User user = DUser.getUser(api, guess.getUserId());

        builder.setAuthor(user.getDiscriminatedName(), null, user.getAvatar()).setDescription("Guess with missing requirement")
                .addField("Puzzle", guess.getName())
                .addField("User ID", String.valueOf(guess.getUserId()))
                .addField("Guess", guess.getGuess())
                .setColor(Color.YELLOW);

        return builder;
    }
}

package bot.log;

import bot.discord.channel.DChannel;
import bot.discord.message.DMessage;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import war.achievement.WarAchievement;
import war.team.WarTeam;

public class AchievementLogger
{
    private final DiscordApi api;
    private final long channelId;

    public AchievementLogger(DiscordApi api, long channelId)
    {
        this.api = api;
        this.channelId = channelId;
    }

    public void log(User user, WarAchievement achievement, WarTeam team)
    {
        TextChannel channel = DChannel.getChannel(api, channelId);
        DMessage.sendMessage(channel, achievementEmbed(user, achievement, team));
    }

    public void logSecret(User user, WarTeam team, String image)
    {
        TextChannel channel = DChannel.getChannel(api, channelId);
        DMessage.sendMessage(channel, secretAchievementEmbed(user, team, image));
    }

    private EmbedBuilder achievementEmbed(User user, WarAchievement achievement, WarTeam team)
    {
        EmbedBuilder builder = new EmbedBuilder();

        String title = String.format("%s - %s", achievement.getCategoryEmoji(), achievement.getFullName());
        builder.setTitle(title).setThumbnail(achievement.getImage());

        String authorName = String.format("%s earned a stamp", user.getName());
        builder.setAuthor(authorName, null, user.getAvatar());
        if (team != null)
            builder.setColor(team.getColor());

        return builder;
    }

    private EmbedBuilder secretAchievementEmbed(User user, WarTeam team, String image)
    {
        EmbedBuilder builder = new EmbedBuilder();
        String authorName = String.format("%s earned a secret stamp", user.getName());

        builder.setThumbnail(image).setAuthor(authorName, null, user.getAvatar());
        if (team != null)
            builder.setColor(team.getColor());

        return builder;
    }
}

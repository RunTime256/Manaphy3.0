package bot.command.definition.war.leaderboard;

import bot.command.MessageCommand;
import bot.command.definition.war.puzzle.list.PuzzleListPrewarCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.channel.DChannel;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import sql.Session;
import war.leaderboard.Leaderboard;
import war.leaderboard.LeaderboardMessage;
import war.leaderboard.WarLeaderboard;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class LeaderboardUpdateCommand
{
    private static final String NAME = "update";
    private static final String DESCRIPTION = "Update the leaderboards for the day";

    private LeaderboardUpdateCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                PuzzleListPrewarCommand.createCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.VERIFIED)
                .subCommands(subCommands).executor(LeaderboardUpdateCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        LeaderboardUpdateFunctionality functionality = new LeaderboardUpdateFunctionality(api, info, session);
        functionality.execute();
    }

    private static class LeaderboardUpdateFunctionality
    {
        private final DiscordApi api;
        private final MessageReceivedInformation info;
        private final Session session;

        LeaderboardUpdateFunctionality(DiscordApi api, MessageReceivedInformation info, Session session)
        {
            this.api = api;
            this.info = info;
            this.session = session;
        }

        void execute()
        {
            List<LeaderboardMessage> leaderboardMessages = Leaderboard.getLeaderboardMessages(session);
            for (LeaderboardMessage message: leaderboardMessages)
            {
                String category = message.getCategory();
                List<WarLeaderboard> leaderboard;
                switch (category)
                {
                    case "battle":
                        leaderboard = Leaderboard.getBattleLeaderboard(session);
                        break;
                    case "puzzle":
                        leaderboard = Leaderboard.getPuzzleLeaderboard(session);
                        break;
                    case "art":
                        leaderboard = Leaderboard.getArtLeaderboard(session);
                        break;
                    case "game":
                        leaderboard = Leaderboard.getGameLeaderboard(session);
                        break;
                    case "bonus":
                        leaderboard = Leaderboard.getBonusLeaderboard(session);
                        break;
                    default:
                        continue;
                }

                EmbedBuilder builder = createEmbed(category, leaderboard);
                api.getMessageById(message.getMessageId(), DChannel.getChannel(api, message.getChannelId()))
                        .thenAccept(pinnedMessage -> pinnedMessage.edit(builder));
            }

            DMessage.sendMessage(info.getChannel(), "Leaderboards have been updated.");
        }

        private EmbedBuilder createEmbed(String category, List<WarLeaderboard> leaderboard)
        {
            EmbedBuilder builder = new EmbedBuilder();
            String title = category.substring(0, 1).toUpperCase() + category.substring(1) + " Leaderboard";
            int max = leaderboard.get(0).getTokens();
            int tied = 0;
            int color = 0;

            for (int i = 0; i < leaderboard.size(); i++)
            {
                WarLeaderboard teamLeaderboard = leaderboard.get(i);

                builder.addField((i + 1) + ". " + teamLeaderboard.getTeamName(), "Tokens: " + teamLeaderboard.getTokens());
                if (teamLeaderboard.getTokens() == max)
                    tied++;
                color += teamLeaderboard.getColorValue();
            }

            if (tied != 0)
                color /= tied;
            builder.setTitle(title).setColor(new Color(color));

            return builder;
        }
    }
}

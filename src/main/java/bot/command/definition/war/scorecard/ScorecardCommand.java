package bot.command.definition.war.scorecard;

import bot.command.MessageCommand;
import bot.command.definition.war.WarCommandFunctionality;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.discord.user.DUser;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import sql.Session;
import war.scorecard.Scorecard;
import war.scorecard.WarScorecard;
import war.team.Team;
import war.team.WarTeam;

import java.util.Arrays;
import java.util.List;

public class ScorecardCommand
{
    private static final String NAME = "scorecard";
    private static final String DESCRIPTION = "View your scorecard";

    private ScorecardCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
             ScorecardViewCommand.createCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION)
                .subCommands(subCommands).executor(ScorecardCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        ScorecardFunctionality functionality = new ScorecardFunctionality(api, info, session);
        functionality.execute();
    }

    private static class ScorecardFunctionality extends WarCommandFunctionality
    {
        private final DiscordApi api;
        private final MessageReceivedInformation info;
        private final Session session;

        ScorecardFunctionality(DiscordApi api, MessageReceivedInformation info, Session session)
        {
            this.api = api;
            this.info = info;
            this.session = session;
        }

        void execute()
        {
            checkPrerequisites(info.getUser().getId(), session);
            long userId = info.getUser().getId();
            WarScorecard scorecard = Scorecard.getScorecard(userId, session);
            WarTeam team = Team.getTeam(userId, session);
            DMessage.sendMessage(info.getChannel(), scorecardEmbed(scorecard, team));
        }

        private EmbedBuilder scorecardEmbed(WarScorecard scorecard, WarTeam team)
        {
            EmbedBuilder builder = new EmbedBuilder();
            User user = DUser.getUser(api, scorecard.getUserId());

            String description = "**Team:** " + team.getFullName();
            String tokens = "**Battle Tokens:** " + scorecard.getBattleTokens() +
                    "\n\n**Puzzle Tokens:** " + scorecard.getPuzzleTokens() +
                    "\n\n**Art Tokens:** " + scorecard.getArtTokens() +
                    "\n\n**Game Tokens:** " + scorecard.getGameTokens() +
                    "\n\n**Bonus Tokens:** " + scorecard.getBonusTokens() +
                    "\n--------------------\n" +
                    "\n**Total:** " + scorecard.getTotalTokens();

            builder.setAuthor(user.getDiscriminatedName(), null, user.getAvatar()).setTitle("Contorted Chronicles")
                    .setDescription(description).setColor(team.getColor())
                    .addField("Tokens", tokens);

            return builder;
        }
    }
}

package bot.command.definition.war.scorecard;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.discord.user.DUser;
import exception.bot.argument.InvalidArgumentException;
import exception.bot.argument.MissingArgumentException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import sql.Session;
import war.scorecard.Scorecard;
import war.scorecard.WarScorecard;
import war.team.Team;
import war.team.WarTeam;

import java.util.List;

public class ScorecardViewCommand
{
    private static final String NAME = "view";
    private static final String DESCRIPTION = "View a user's scorecard";
    private static final String SYNTAX = "<user id>";

    private ScorecardViewCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .requirement(RoleRequirement.MOD).executor(ScorecardViewCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (vars.isEmpty())
            throw new MissingArgumentException("user id");

        ScorecardFunctionality functionality = new ScorecardFunctionality(api, info, vars, session);
        functionality.execute();
    }

    private static class ScorecardFunctionality
    {
        private final DiscordApi api;
        private final MessageReceivedInformation info;
        private final Session session;
        private final long userId;

        ScorecardFunctionality(DiscordApi api, MessageReceivedInformation info,  List<String> vars, Session session)
        {
            this.api = api;
            this.info = info;
            this.session = session;
            try
            {
                userId = Long.parseLong(vars.get(0));
            }
            catch (NumberFormatException e)
            {
                throw new InvalidArgumentException("user id");
            }
        }

        void execute()
        {
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

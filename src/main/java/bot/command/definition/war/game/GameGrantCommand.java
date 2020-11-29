package bot.command.definition.war.game;

import bot.command.MessageCommand;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.discord.user.DUser;
import exception.bot.argument.InvalidArgumentException;
import exception.bot.argument.MissingArgumentException;
import exception.war.game.GameException;
import exception.war.game.IncorrectGameChannelException;
import exception.war.game.NotAGameException;
import exception.war.team.BannedMemberException;
import exception.war.team.NotATeamMemberException;
import exception.war.team.TeamException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import sql.Session;
import war.game.Game;
import war.team.Team;
import war.team.WarTeam;

import java.util.List;

public class GameGrantCommand
{
    private static final String NAME = "grant";
    private static final String DESCRIPTION = "Grant game tokens to a user";

    private GameGrantCommand()
    {
    }

    public static MessageCommand createBotCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION)
                .executor(GameGrantCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {

        GameGrantFunctionality functionality = new GameGrantFunctionality(api, info, vars, session);
        functionality.execute();
    }

    private static class GameGrantFunctionality
    {
        private final DiscordApi api;
        private final MessageReceivedInformation info;
        private final Session session;
        private final String gameName;
        private final Long userId;
        private final int score;

        GameGrantFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
        {
            this.api = api;
            this.info = info;
            this.session = session;

            if (vars.isEmpty())
                throw new MissingArgumentException("game name");
            else if (vars.size() == 1)
                throw new MissingArgumentException("user id");

            gameName = vars.get(0);
            try
            {
                userId = Long.parseLong(vars.get(1));
            }
            catch (NumberFormatException ignored)
            {
                throw new InvalidArgumentException("user id");
            }
            try
            {
                score = Integer.parseInt(vars.get(2));
            }
            catch (NumberFormatException ignored)
            {
                throw new InvalidArgumentException("score");
            }
        }

        void execute()
        {
            try
            {
                grantTokens();
            }
            catch (TeamException | GameException e)
            {
                DMessage.sendMessage(info.getChannel(), e.getMessage());
            }
        }

        private void grantTokens()
        {
            if (!Team.isTeamMember(userId, session))
                throw new NotATeamMemberException(userId);
            else if (Team.isBanned(userId, session))
                throw new BannedMemberException(userId);
            else if (!Game.exists(gameName, session))
                throw new NotAGameException(gameName);
            else if (!Game.correctChannel(gameName, info.getChannel().getId(), session))
                throw new IncorrectGameChannelException(gameName);

            int tokens = Game.getTokens(gameName, score, session);
            Game.addTokens(gameName, userId, score, tokens, info.getTime(), session);
            String fullName = Game.getFullName(gameName, session);
            WarTeam team = Team.getTeam(userId, session);

            DMessage.sendMessage(info.getChannel(), tokens + " tokens for Game `" + gameName + "` added for `" + userId + "`");
            DMessage.sendPrivateMessage(api, userId, gameEmbed(team, fullName, tokens));
        }

        private EmbedBuilder gameEmbed(WarTeam team, String fullName, int tokens)
        {
            EmbedBuilder builder = new EmbedBuilder();
            User user = DUser.getUser(api, userId);

            builder.setAuthor(user.getDiscriminatedName(), null, user.getAvatar()).setTitle("Game Tokens Earned: " + tokens)
                    .setDescription(fullName).setColor(team.getColor()).setThumbnail(team.getTokenImage());

            return builder;
        }
    }
}

package bot.command.definition.war.join;

import bot.command.MessageCommand;
import bot.command.ReactionCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.information.ReactionReceivedInformation;
import bot.discord.listener.ReactionCommandListener;
import bot.discord.message.DMessage;
import bot.discord.reaction.DReaction;
import bot.discord.role.DRole;
import bot.discord.server.DServer;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import sql.Session;
import war.team.Team;
import war.team.WarTeam;
import war.team.member.Member;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class JoinCommand
{
    private static final String NAME = "join";
    private static final String DESCRIPTION = "Participate in the war";

    private JoinCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.VERIFIED)
                .executor(JoinCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        JoinFunctionality functionality = new JoinFunctionality(api, info, session);
        functionality.execute();
    }

    private static class JoinFunctionality
    {
        private static final String BATTLER = "\uD83D\uDDE1";
        private static final String ARTIST = "\uD83C\uDFA8";
        private static final String ORACLE = "\uD83D\uDD0D";
        private final DiscordApi api;
        private final MessageReceivedInformation info;
        private final Session session;

        JoinFunctionality(DiscordApi api, MessageReceivedInformation info, Session session)
        {
            this.api = api;
            this.info = info;
            this.session = session;
        }

        void execute()
        {
            if (info.getServer() != null)
            {
                DMessage.sendMessage(info.getChannel(), "Please perform this command in DMs only.");
                info.delete();
                return;
            }

            long userId = info.getUser().getId();
            int tokens = Member.getPrewarTokens(userId, session);

            if (Team.isTeamMember(userId, session))
            {
                if (Team.isBanned(userId, session))
                    DMessage.sendMessage(info.getChannel(), "You are banned from the event.");
                else
                    DMessage.sendMessage(info.getChannel(), "You have already joined a team.");
                return;
            }

            if (tokens == 0)
            {
                classSelect();
            }
            else
            {
                WarTeam team = Team.joinPrewarTeam(userId, tokens, info.getTime(), session);
                DMessage.sendMessage(info.getChannel(), teamEmbed(team));
                DRole.addRole(DServer.getServer(api, "pokemon", session), team.getRoleId(), userId);
            }
        }

        private void classSelect()
        {
            String classSelect = "Please select a role to get sorted onto your team!\n" +
                    "Each role is focused around your skills, so pick the one that suits you the most!\n\n" +
                    BATTLER + " **Battler:** For those that enjoy battling and team building.\n" +
                    ARTIST + " **Writer/Artist:** For the creative types that enjoy drawing or writing.\n" +
                    ORACLE + " **Puzzle Solver/Mini-Gamer:** For those that enjoy solving challenging puzzles or competing in mini-games.";

            CompletableFuture<Message> message = DMessage.sendMessage(info.getChannel(), classSelect);
            message.thenAccept(completedMessage ->
            {
                CompletableFuture<Void> battlerFuture = DReaction.addReaction(completedMessage, BATTLER);
                CompletableFuture<Void> artistFuture = DReaction.addReaction(completedMessage, ARTIST);
                CompletableFuture<Void> oracleFuture = DReaction.addReaction(completedMessage, ORACLE);

                boolean[] completed = {false};
                battlerFuture.thenAccept(aVoid -> api.addReactionAddListener(
                        new ReactionCommandListener(
                                info.getUser().getId(), info.getChannel().getId(), completedMessage.getId(), BATTLER, api,
                                new ReactionCommand(JoinFunctionality::battlerFunction, completed, info.getMessage().getCreationTimestamp()))).removeAfter(30, TimeUnit.SECONDS));

                artistFuture.thenAccept(aVoid -> api.addReactionAddListener(
                        new ReactionCommandListener(
                                info.getUser().getId(), info.getChannel().getId(), completedMessage.getId(), ARTIST, api,
                                new ReactionCommand(JoinFunctionality::artistFunction, completed, info.getMessage().getCreationTimestamp()))).removeAfter(30, TimeUnit.SECONDS));

                oracleFuture.thenAccept(aVoid -> api.addReactionAddListener(
                        new ReactionCommandListener(
                                info.getUser().getId(), info.getChannel().getId(), completedMessage.getId(), ORACLE, api,
                                new ReactionCommand(JoinFunctionality::oracleFunction, completed, info.getMessage().getCreationTimestamp()))).removeAfter(30, TimeUnit.SECONDS).addRemoveHandler(() ->
                {
                    if (!completed[0])
                    {
                        DMessage.sendMessage(info.getChannel(), "Took too long to respond...");
                    }
                }));
            });
        }

        static void battlerFunction(DiscordApi api, ReactionReceivedInformation info, Session session, Object o)
        {
            joinFunction(api, info, (Instant)o, session, "battler");
        }

        static void artistFunction(DiscordApi api, ReactionReceivedInformation info, Session session, Object o)
        {
            joinFunction(api, info, (Instant)o, session, "artist");
        }

        static void oracleFunction(DiscordApi api, ReactionReceivedInformation info, Session session, Object o)
        {
            joinFunction(api, info, (Instant)o, session, "oracle");
        }

        static void joinFunction(DiscordApi api, ReactionReceivedInformation info, Instant timestamp, Session session, String selectedClass)
        {
            long userId = info.getUser().getId();
            WarTeam team = Team.joinTeam(userId, selectedClass, timestamp, session);
            DMessage.sendMessage(info.getChannel(), teamEmbed(team));
            DRole.addRole(DServer.getServer(api, "pokemon", session), team.getRoleId(), userId);
        }

        static EmbedBuilder teamEmbed(WarTeam team)
        {
            String title = "Welcome to the " + team.getShortName() + " team!";
            EmbedBuilder builder = new EmbedBuilder();

            builder.setTitle(title).setDescription(team.getWelcomeText()).setThumbnail(team.getLeaderImage())
                    .setColor(team.getColor());

            return builder;
        }
    }
}

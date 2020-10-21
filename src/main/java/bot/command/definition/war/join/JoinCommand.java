package bot.command.definition.war.join;

import bot.command.MessageCommand;
import bot.command.ReactionCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.information.ReactionReceivedInformation;
import bot.discord.listener.ReactionCommandListener;
import bot.discord.message.DMessage;
import bot.discord.reaction.DReaction;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import sql.Session;

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
        JoinFunctionality functionality = new JoinFunctionality(api, info);
        functionality.execute();
    }

    private static class JoinFunctionality
    {
        private static final String BATTLER = "\uD83D\uDDE1";
        private static final String ARTIST = "\uD83C\uDFA8";
        private static final String ORACLE = "\uD83D\uDD0D";
        private final DiscordApi api;
        private final MessageReceivedInformation info;

        JoinFunctionality(DiscordApi api, MessageReceivedInformation info)
        {
            this.api = api;
            this.info = info;
        }

        void execute()
        {
            CompletableFuture<Message> message = DMessage.sendMessage(info.getChannel(), "Select a class");
            message.thenAccept(completedMessage ->
            {
                CompletableFuture<Void> battlerFuture = DReaction.addReaction(completedMessage, BATTLER);
                CompletableFuture<Void> artistFuture = DReaction.addReaction(completedMessage, ARTIST);
                CompletableFuture<Void> oracleFuture = DReaction.addReaction(completedMessage, ORACLE);

                boolean[] completed = {false};
                battlerFuture.thenAccept(aVoid -> api.addReactionAddListener(
                        new ReactionCommandListener(
                                info.getUser().getId(), info.getChannel().getId(), completedMessage.getId(), BATTLER, api,
                                new ReactionCommand(JoinFunctionality::battlerFunction, completed, null))).removeAfter(30, TimeUnit.SECONDS));

                artistFuture.thenAccept(aVoid -> api.addReactionAddListener(
                        new ReactionCommandListener(
                                info.getUser().getId(), info.getChannel().getId(), completedMessage.getId(), ARTIST, api,
                                new ReactionCommand(JoinFunctionality::artistFunction, completed, null))).removeAfter(30, TimeUnit.SECONDS));

                oracleFuture.thenAccept(aVoid -> api.addReactionAddListener(
                        new ReactionCommandListener(
                                info.getUser().getId(), info.getChannel().getId(), completedMessage.getId(), ORACLE, api,
                                new ReactionCommand(JoinFunctionality::oracleFunction, completed, null))).removeAfter(30, TimeUnit.SECONDS).addRemoveHandler(() ->
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
            joinFunction(info, session, "BATTLER");
        }

        static void artistFunction(DiscordApi api, ReactionReceivedInformation info, Session session, Object o)
        {
            joinFunction(info, session, "ARTIST");
        }

        static void oracleFunction(DiscordApi api, ReactionReceivedInformation info, Session session, Object o)
        {
            joinFunction(info, session, "ORACLE");
        }

        static void joinFunction(ReactionReceivedInformation info, Session session, String selectedClass)
        {
            DMessage.sendMessage(info.getChannel(), "You selected " + selectedClass);
        }
    }
}

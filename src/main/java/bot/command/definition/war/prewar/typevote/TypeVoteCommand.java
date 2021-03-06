package bot.command.definition.war.prewar.typevote;

import bot.command.MessageCommand;
import bot.command.ReactionCommand;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.information.ReactionReceivedInformation;
import bot.discord.listener.ReactionCommandListener;
import bot.discord.message.DMessage;
import bot.discord.reaction.DReaction;
import bot.discord.role.DRole;
import bot.discord.server.DServer;
import bot.log.TypeVoteLogger;
import exception.bot.argument.MissingArgumentException;
import exception.bot.argument.TooManyArgumentsException;
import exception.bot.command.InvalidCommandException;
import exception.war.typevote.InvalidTypeException;
import exception.war.typevote.MaxVoteException;
import exception.war.typevote.UnavailableTypeException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import sql.Session;
import war.typevote.TypeVote;
import war.typevote.TypeVoteSelection;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TypeVoteCommand
{
    private static final String NAME = "vote";
    private static final String DESCRIPTION = "Vote on Pok\u00E9mon types";
    private static final String SYNTAX = "<type>";
    private static TypeVoteLogger typeVoteLogger;

    private TypeVoteCommand()
    {
    }

    public static void setTypeVoteLogger(TypeVoteLogger typeVoteLogger)
    {
        TypeVoteCommand.typeVoteLogger = typeVoteLogger;
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .executor(TypeVoteCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (vars.isEmpty())
            throw new MissingArgumentException("type");
        if (vars.size() > 1)
            throw new TooManyArgumentsException();
        TypeVoteFunctionality functionality = new TypeVoteFunctionality(api, info, vars, session);
        functionality.execute();
    }

    private static class TypeVoteFunctionality
    {
        private final DiscordApi api;
        private final MessageReceivedInformation info;
        private final String type;
        private final Session session;

        TypeVoteFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
        {
            this.api = api;
            this.info = info;
            type = vars.get(0).toUpperCase();
            this.session = session;
        }

        void execute()
        {
            try
            {
                int total = TypeVote.TOTAL_VOTES;
                long userId = info.getUser().getId();
                int count = TypeVote.getUsedVoteCount(userId, session);
                if (count >= total)
                    throw new MaxVoteException();

                if (!TypeVote.exists(type, session))
                    throw new InvalidTypeException(type);

                if (!TypeVote.canVote(type, userId, session))
                    throw new UnavailableTypeException(type);

                TypeVoteSelection selection = new TypeVoteSelection(type, count, userId, info.getTime());

                CompletableFuture<Message> message = DMessage.sendMessage(info.getChannel(),
                        "Your vote for a type is `" + type + "`. Is that correct?");
                message.thenAccept(completedMessage ->
                {
                    CompletableFuture<Void> yesFuture = DReaction.addReaction(completedMessage, ReactionCommand.YES);
                    CompletableFuture<Void> noFuture = DReaction.addReaction(completedMessage, ReactionCommand.NO);

                    boolean[] completed = {false};
                    yesFuture.thenAccept(aVoid -> api.addReactionAddListener(
                            new ReactionCommandListener(
                                    info.getUser().getId(), info.getChannel().getId(), completedMessage.getId(), ReactionCommand.YES, api,
                                    new ReactionCommand(TypeVoteFunctionality::yesFunction, completed, selection))).removeAfter(30, TimeUnit.SECONDS));

                    noFuture.thenAccept(aVoid -> api.addReactionAddListener(
                            new ReactionCommandListener(
                                    info.getUser().getId(), info.getChannel().getId(), completedMessage.getId(), ReactionCommand.NO, api,
                                    new ReactionCommand(TypeVoteFunctionality::noFunction, completed, null))).removeAfter(30, TimeUnit.SECONDS).addRemoveHandler(() ->
                    {
                        if (!completed[0])
                        {
                            throw new InvalidCommandException("Took too long to respond...");
                        }
                    }));
                });
            }
            catch (MaxVoteException | InvalidTypeException | UnavailableTypeException e)
            {
                DMessage.sendMessage(info.getChannel(), e.getMessage());
            }
        }

        private static void log(TypeVoteSelection selection)
        {
            if (typeVoteLogger != null)
                typeVoteLogger.log(selection);
        }

        private static void yesFunction(DiscordApi api, ReactionReceivedInformation info, Session session, Object o)
        {
            TypeVoteSelection selection = (TypeVoteSelection)o;

            int total = TypeVote.TOTAL_VOTES;

            try
            {
                TypeVote.addTypeVote(selection.getType(), selection.getUserId(), selection.getTime(), session);
                int count = selection.getCount() + 1;
                log(selection);

                CompletableFuture<Void> roleFuture = null;
                if (count == total)
                {
                    roleFuture = DRole.addRole(DServer.getServer(api, "pokemon", session), "pokemon", "pledge", info.getUser().getId(), session);
                }

                CompletableFuture<Message> futureMessage = DMessage.sendMessage(info.getChannel(), "Vote successful. Thank you for supporting democracy! You have " + (total - count) + "/" + total + " votes remaining.");
                CompletableFuture<Void> finalRoleFuture = roleFuture;
                futureMessage.thenAccept(message -> {

                    if (finalRoleFuture != null)
                    {
                        finalRoleFuture.thenAccept(aVoid -> DMessage.sendMessage(info.getChannel(), "With your final vote, you have earned the Pledge of Allegiance role. Congratulations!"));
                    }
                });
            }
            catch (MaxVoteException | InvalidTypeException | UnavailableTypeException e)
            {
                DMessage.sendMessage(info.getChannel(), e.getMessage());
            }
        }

        private static void noFunction(DiscordApi api, ReactionReceivedInformation info, Session session, Object o)
        {
            DMessage.sendMessage(info.getChannel(), "Type vote cancelled.");
        }
    }
}

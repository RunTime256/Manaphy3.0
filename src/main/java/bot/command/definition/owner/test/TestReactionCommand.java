package bot.command.definition.owner.test;

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

public class TestReactionCommand
{
    private static final String NAME = "reaction";
    private static final String DESCRIPTION = "Test reaction functionality";

    private TestReactionCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.OWNER)
                .executor(TestReactionCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        TestReactionFunctionality functionality = new TestReactionFunctionality(api, info);
        functionality.execute();
    }

    private static class TestReactionFunctionality
    {
        private final DiscordApi api;
        private final MessageReceivedInformation info;

        TestReactionFunctionality(DiscordApi api, MessageReceivedInformation info)
        {
            this.api = api;
            this.info = info;
        }

        void execute()
        {
            CompletableFuture<Message> message = DMessage.sendMessage(info.getChannel(), "Yes or no?");
            message.thenAccept(completedMessage -> {
                CompletableFuture<Void> yesFuture = DReaction.addReaction(completedMessage, ReactionCommand.YES);
                CompletableFuture<Void> noFuture = DReaction.addReaction(completedMessage, ReactionCommand.NO);

                boolean[] completed = {false};
                yesFuture.thenAccept(aVoid -> api.addReactionAddListener(
                        new ReactionCommandListener(
                                info.getUser(), info.getChannel(), ReactionCommand.YES, api,
                                new ReactionCommand(TestReactionFunctionality::yesFunction, completed))).removeAfter(10, TimeUnit.SECONDS));

                noFuture.thenAccept(aVoid -> api.addReactionAddListener(
                        new ReactionCommandListener(
                                info.getUser(), info.getChannel(), ReactionCommand.NO, api,
                                new ReactionCommand(TestReactionFunctionality::noFunction, completed))).removeAfter(10, TimeUnit.SECONDS).addRemoveHandler(() -> {
                    if (!completed[0])
                    {
                        DMessage.sendMessage(info.getChannel(), "Took too long to respond...");
                    }
                }));
            });
        }

        private static void yesFunction(DiscordApi api, ReactionReceivedInformation info, Session session)
        {
            DMessage.sendMessage(info.getChannel(), "You selected yes!");
        }

        private static void noFunction(DiscordApi api, ReactionReceivedInformation info, Session session)
        {
            DMessage.sendMessage(info.getChannel(), "You selected no.");
        }
    }
}

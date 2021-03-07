package bot.command.definition.war.ban;

import bot.command.MessageCommand;
import bot.command.ReactionCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.information.ReactionReceivedInformation;
import bot.discord.listener.ReactionCommandListener;
import bot.discord.message.DMessage;
import bot.discord.reaction.DReaction;
import exception.bot.argument.InvalidArgumentException;
import exception.bot.command.InvalidCommandException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import sql.Session;
import war.team.member.Member;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class BanCommand
{
    private static final String NAME = "ban";
    private static final String DESCRIPTION = "Ban a member from the event";
    private static final String SYNTAX = "<user id>";

    private BanCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.MOD)
                .syntax(SYNTAX).executor(BanCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        BanFunctionality functionality = new BanFunctionality(api, info, vars);
        functionality.execute();
    }

    private static class BanFunctionality
    {
        private final DiscordApi api;
        private final MessageReceivedInformation info;
        private final long userId;

        BanFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars)
        {
            this.api = api;
            this.info = info;
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
            CompletableFuture<Message> message = DMessage.sendMessage(info.getChannel(), "Are you sure you want to ban the user `" + userId + "` ?");
            message.thenAccept(completedMessage -> {
                CompletableFuture<Void> yesFuture = DReaction.addReaction(completedMessage, ReactionCommand.YES);
                CompletableFuture<Void> noFuture = DReaction.addReaction(completedMessage, ReactionCommand.NO);

                boolean[] completed = {false};
                yesFuture.thenAccept(aVoid -> api.addReactionAddListener(
                        new ReactionCommandListener(
                                info.getUser().getId(), info.getChannel().getId(), completedMessage.getId(), ReactionCommand.YES, api,
                                new ReactionCommand(BanFunctionality::yesFunction, completed, userId))).removeAfter(10, TimeUnit.SECONDS));

                noFuture.thenAccept(aVoid -> api.addReactionAddListener(
                        new ReactionCommandListener(
                                info.getUser().getId(), info.getChannel().getId(), completedMessage.getId(), ReactionCommand.NO, api,
                                new ReactionCommand(BanFunctionality::noFunction, completed, userId))).removeAfter(10, TimeUnit.SECONDS).addRemoveHandler(() -> {
                    if (!completed[0])
                    {
                        throw new InvalidCommandException("Took too long to respond...");
                    }
                }));
            });
        }

        private static void yesFunction(DiscordApi api, ReactionReceivedInformation info, Session session, Object o)
        {
            int updated = Member.updateBanStatus((long)o, true, session);
            if (updated > 0)
                DMessage.sendMessage(info.getChannel(), "User banned from the war.");
            else
                DMessage.sendMessage(info.getChannel(), "There was an issue banning the user.");
        }

        private static void noFunction(DiscordApi api, ReactionReceivedInformation info, Session session, Object o)
        {
            DMessage.sendMessage(info.getChannel(), "Ban cancelled.");
        }
    }
}

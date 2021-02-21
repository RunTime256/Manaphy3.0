package bot.command.definition.war.contest;

import bot.command.MessageCommand;
import bot.command.ReactionCommand;
import bot.command.verification.RoleCheck;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import exception.bot.argument.MissingArgumentException;
import exception.war.contest.NotAContestException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.user.User;
import sql.Session;
import war.contest.Contest;
import war.contest.ContestUser;
import war.team.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class ContestAddCommand
{
    private static final String NAME = "add";
    private static final String DESCRIPTION = "Add contest participants from current channel.\nUse " + ReactionCommand.YES +
            " to mark a valid entry.";
    private static final String SYNTAX = "<contest name>";

    private ContestAddCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .requirement(RoleRequirement.MOD).executor(ContestAddCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (vars.isEmpty())
            throw new MissingArgumentException("contest name");

        ContestAddFunctionality functionality = new ContestAddFunctionality(api, info, vars, session);
        functionality.execute();
    }

    private static class ContestAddFunctionality
    {
        private final DiscordApi api;
        private final MessageReceivedInformation info;
        private final Session session;
        private final String contestName;

        ContestAddFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
        {
            this.api = api;
            this.info = info;
            this.session = session;
            contestName = vars.get(0);
        }

        void execute()
        {
            if (!Contest.isContest(contestName, session))
                throw new NotAContestException(contestName);

            Stream<Message> messages = info.getChannel().getMessagesAsStream();
            Map<Long, ContestUser> participationMap = new HashMap<>();

            AtomicLong count = new AtomicLong();
            AtomicLong current = new AtomicLong();
            messages.forEach(message -> {
                List<Reaction> reactions = message.getReactions();
                for (Reaction reaction: reactions)
                {
                    count.getAndIncrement();
                    evaluateReactions(participationMap, message, reaction, current);
                }
            });

            DMessage.sendMessage(info.getChannel(), "Processing participants...");
            // TODO fix this sad implementation
            while (count.get() > current.get());
            DMessage.sendMessage(info.getChannel(), "Contest participants added.");
        }

        private void evaluateReactions(Map<Long, ContestUser> participationMap, Message message, Reaction reaction, AtomicLong current)
        {
            Optional<String> optional = reaction.getEmoji().asUnicodeEmoji();
            if (optional.isPresent())
            {
                String reactionString = optional.get();
                boolean check = reactionString.equals(ReactionCommand.YES);
                if (check)
                {
                    participation(participationMap, message, reaction, current);
                }
                else
                {
                    current.getAndIncrement();
                }
            }
            else
            {
                current.getAndIncrement();
            }
        }

        private void participation(Map<Long, ContestUser> participationMap, Message message, Reaction reaction, AtomicLong current)
        {
            reaction.getUsers().thenAccept(users -> {
                if (hasMod(users))
                {
                    Optional<User> user = message.getUserAuthor();
                    user.ifPresent(value -> {
                        long userId = value.getId();
                        if (!Team.isTeamMember(userId, session) || Team.isBanned(userId, session))
                            return;
                        addParticipation(participationMap, userId);
                        if (participationMap.get(userId).getParticipation() <= 1)
                        {
                            Contest.addParticipant(contestName, userId, session);
                        }
                    });
                }
                current.getAndIncrement();
            });
        }

        private boolean hasMod(List<User> users)
        {
            for (User user: users)
            {
                if (RoleCheck.hasPermission(session, api, user, RoleRequirement.MOD))
                    return true;
            }
            return false;
        }

        private void addParticipation(Map<Long, ContestUser> participationMap, long userId)
        {
            if (!participationMap.containsKey(userId))
            {
                participationMap.put(userId, new ContestUser(userId));
            }

            participationMap.get(userId).increaseParticipation();
        }
    }
}

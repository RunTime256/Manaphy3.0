package bot.command.definition.war.fanart;

import bot.command.MessageCommand;
import bot.command.ReactionCommand;
import bot.command.verification.RoleCheck;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.Reaction;
import org.javacord.api.entity.user.User;
import sql.Session;
import war.fanart.Fanart;
import war.fanart.FanartUser;
import war.team.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class FanartAddCommand
{
    public static final String ART = "\uD83C\uDFA8";
    private static final String NAME = "add";
    private static final String DESCRIPTION = "Add fanart from current channel.\nUse " + ReactionCommand.YES +
            " to mark a valid entry, and " + ART + " to mark a bonus entry.";

    private FanartAddCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION)
                .requirement(RoleRequirement.MOD).executor(FanartAddCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        FanartAddFunctionality functionality = new FanartAddFunctionality(api, info, session);
        functionality.execute();
    }

    private static class FanartAddFunctionality
    {
        private final DiscordApi api;
        private final MessageReceivedInformation info;
        private final Session session;

        FanartAddFunctionality(DiscordApi api, MessageReceivedInformation info, Session session)
        {
            this.api = api;
            this.info = info;
            this.session = session;
        }

        void execute()
        {
            Stream<Message> messages = info.getChannel().getMessagesAsStream();
            Map<Long, FanartUser> participationMap = new HashMap<>();

            AtomicLong count = new AtomicLong();
            long[] current = {0};
            messages.forEach(message -> {
                List<Reaction> reactions = message.getReactions();
                for (Reaction reaction: reactions)
                {
                    evaluateReactions(participationMap, message, reaction, current);
                    count.getAndIncrement();
                }
            });

            // TODO fix this sad implementation
            while (count.get() > current[0]);
            DMessage.sendMessage(info.getChannel(), "Fanart added.");
        }

        private void evaluateReactions(Map<Long, FanartUser> participationMap, Message message, Reaction reaction, long[] current)
        {
            Optional<String> optional = reaction.getEmoji().asUnicodeEmoji();
            if (optional.isPresent())
            {
                String reactionString = optional.get();
                boolean check = reactionString.equals(ReactionCommand.YES);
                boolean bonus = reactionString.equals(ART);
                if (check)
                {
                    participation(participationMap, message, reaction, current);
                }
                if (bonus)
                {
                    bonus(participationMap, message, reaction, current);
                }
                if (!check && !bonus)
                {
                    current[0]++;
                }
            }
            else
            {
                current[0]++;
            }
        }

        private void participation(Map<Long, FanartUser> participationMap, Message message, Reaction reaction, long[] current)
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
                        if (participationMap.get(userId).getParticipation() <= 2)
                        {
                            Fanart.addParticipant(userId, session);
                        }
                    });
                }
                current[0]++;
            });
        }

        private void bonus(Map<Long, FanartUser> participationMap, Message message, Reaction reaction, long[] current)
        {
            reaction.getUsers().thenAccept(users -> {
                if (hasMod(users))
                {
                    Optional<User> user = message.getUserAuthor();
                    user.ifPresent(value -> {
                        long userId = value.getId();
                        if (!Team.isTeamMember(userId, session) || Team.isBanned(userId, session))
                            return;
                        addBonus(participationMap, userId);
                        if (participationMap.get(userId).getBonus() <= 2)
                        {
                            Fanart.addBonus(userId, session);
                        }
                    });
                }
                current[0]++;
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

        private void addParticipation(Map<Long, FanartUser> participationMap, long userId)
        {
            if (!participationMap.containsKey(userId))
            {
                participationMap.put(userId, new FanartUser(userId));
            }

            participationMap.get(userId).increaseParticipation();
        }

        private void addBonus(Map<Long, FanartUser> participationMap, long userId)
        {
            if (!participationMap.containsKey(userId))
            {
                participationMap.put(userId, new FanartUser(userId));
            }

            participationMap.get(userId).increaseBonus();
        }
    }
}

package bot.command.definition.achievements;

import bot.command.MessageCommand;
import bot.command.ReactionCommand;
import bot.command.definition.get.GetUserCommand;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.information.ReactionReceivedInformation;
import bot.discord.listener.ReactionCommandListener;
import bot.discord.message.DMessage;
import bot.discord.reaction.DReaction;
import bot.util.IdExtractor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import sql.Session;

import java.util.List;

public class AchievementsCommand
{
    private static final String NAME = "achievements";
    private static final String DESCRIPTION = "List your war achievements.";
    private static final String SYNTAX = "<user> <summary>?";

    private static final String ZWSP = "\u200B";

    private AchievementsCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .executor(AchievementsCommand::function).build();
    }

    public static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        AchievementsFunctionality functionality = new AchievementsFunctionality(api, info, vars, session);
        functionality.execute();
    }

    private static class AchievementsFunctionality
    {
        final DiscordApi api;
        final MessageReceivedInformation info;
        final Session session;

        final Boolean isSummary;
        final String userName;
        final Long userId;

        AchievementsFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
        {
            this.api = api;
            this.info = info;
            this.session = session;

            // Check if summary view.
            if (vars.size() < 2) {
                isSummary = false;
            } else {
                isSummary = vars.get(1) == "summary";
            }

            // Check for passed user ID
            if (vars.size() < 1) {
                userName = "";
                userId = 0L;
            } else {
                userName = vars.get(0);
                userId = IdExtractor.getId(userName);
            }


        }

        void execute()
        {
            // Fetch the User
            User user = GetUserCommand.getUser(info, userId, userName);
            if (user == null) {
                DMessage.sendMessage(info.getChannel(), "User `" + userName + "` could not be found.");
                return;
            }

            // Retrieve Users Achievements
            UserAchievementMapper mapper = session.getMapper(UserAchievementMapper.class);
            List<UserAchievement> userAchievements = mapper.getUserAchievements(user.getId());

            // Generate the Embeds
            List<EmbedBuilder>  embeds = List.of();
            for (UserAchievement userAchievement : userAchievements)
            {
                embeds.add(userAchievementEmbed(user, userAchievement));
            }

            // Error out if user has no achievements.
            if (embeds.size() == 0) {
                EmbedBuilder builder = new EmbedBuilder();

                builder.setTitle("You currently have no achievements");
                DMessage.sendMessage(info.getChannel(), builder);
                return;
            }

            // Show summary view if requested.
            if (isSummary) {
                EmbedBuilder builder = new EmbedBuilder();

                String authorName = String.format("%s's Achievements", user.getName());
                builder.setAuthor(authorName, null, user.getAvatar());

                builder.addField("Total Achievements:", String.format("%d", userAchievements.size()), false);


                StringBuilder categoryBuilder = new StringBuilder();
                for (AchievementCategory category : AchievementCategory.values()){
                    Integer count = 0;
                    for (UserAchievement userAchievement : userAchievements) {

                    }

                    categoryBuilder.append(String.format("%s: %d\n", Achievement.getCategoryEmoji(category), count));
                }
                builder.addField("Achievements:", categoryBuilder.toString());

                // TODO: ADD MORE INFORMATION ?

                DMessage.sendMessage(info.getChannel(), builder);
                return;
            }

            // Skip menu if user has only one achievement.
            if (embeds.size() == 1) {
                DMessage.sendMessage(info.getChannel(), embeds.get(0));
                return;
            }

            // Start the menu session
            new AchievementMenu(embeds).startMenu(api, info);
        }

        private EmbedBuilder userAchievementEmbed(User user, UserAchievement userAchievement) {
            Achievement achievement = userAchievement.getAchievement();
            EmbedBuilder builder = new EmbedBuilder();

            // Create embed.
            builder.setTitle(String.format("%s - %s", achievement.getCategoryEmoji(),achievement.getName()));
            builder.setDescription(achievement.getDescription());

            String authorName = String.format("%s's Achievements", user.getName());
            builder.setAuthor(authorName, null, user.getAvatar());

            builder.addField(ZWSP, achievement.getUnlockMethod());

            builder.setFooter("Achieved");
            builder.setTimestamp(userAchievement.getAttainedAt());

            // TODO: ADD MORE INFORMATION ?

            return builder;
        }

        private class AchievementMenu {

            final List<EmbedBuilder> embeds;
            private Message message;
            private int page = 0;
            private List<ReactionCommandListener> listeners = List.of();

            public AchievementMenu(List<EmbedBuilder> embeds) {
                this.embeds = embeds;
            }

            public void startMenu(DiscordApi api, MessageReceivedInformation info) {
                DMessage.sendMessage(info.getChannel(), embeds.get(page)).thenAccept(completedMessage -> {
                    message = completedMessage;

                    boolean[] completed = {false};

                    DReaction.addReaction(message, ReactionCommand.NEXT).thenAccept(aVoid -> {
                        ReactionCommandListener listener = new ReactionCommandListener(
                                info.getUser(), info.getChannel(), ReactionCommand.NEXT, api,
                                new ReactionCommand(this::nextFunction, completed)
                        );

                        listeners.add(listener);
                    });

                    DReaction.addReaction(message, ReactionCommand.PREV).thenAccept(aVoid -> {
                        ReactionCommandListener listener = new ReactionCommandListener(
                                info.getUser(), info.getChannel(), ReactionCommand.PREV, api,
                                new ReactionCommand(this::prevFunction, completed)
                        );

                        listeners.add(listener);
                    });

                    DReaction.addReaction(message, ReactionCommand.STOP).thenAccept(aVoid -> {
                        ReactionCommandListener listener = new ReactionCommandListener(
                                info.getUser(), info.getChannel(), ReactionCommand.STOP, api,
                                new ReactionCommand(this::stopFunction, completed)
                        );


                        listeners.add(listener);
                    });
                });
            }

            private void UpdateMenu(String lastReaction) {
                message.edit(embeds.get(page));
                try {
                    message.removeReactionByEmoji(info.getUser(), lastReaction);
                } finally { }
            }

            private void nextFunction(DiscordApi api, ReactionReceivedInformation info, Session session) {
                page += 1;
                if (page == embeds.size()) {
                    page = 0;
                }
                UpdateMenu(ReactionCommand.NEXT);
            }

            private void prevFunction(DiscordApi api, ReactionReceivedInformation info, Session session) {
                page -= 1;
                if (page < 0) {
                    page = embeds.size() - 1;
                }
                UpdateMenu(ReactionCommand.PREV);
            }

            private void stopFunction(DiscordApi api, ReactionReceivedInformation info, Session session) {
                for (ReactionCommandListener listener : listeners) {
                    api.removeListener(listener);
                }

                message.removeOwnReactionByEmoji(ReactionCommand.NEXT);
                message.removeOwnReactionByEmoji(ReactionCommand.PREV);
                message.removeOwnReactionByEmoji(ReactionCommand.STOP);
                // message.delete();
            }

        }
    }
}


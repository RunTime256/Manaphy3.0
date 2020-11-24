package bot.command.definition.achievements;

import bot.command.MessageCommand;
import bot.command.ReactionCommand;
import bot.command.definition.get.GetUserCommand;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.information.ReactionReceivedInformation;
import bot.discord.listener.ReactionCommandListener;
import bot.discord.message.DMessage;
import bot.discord.reaction.DReaction;
import bot.util.CombineContent;
import bot.util.IdExtractor;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import sql.Session;

import java.util.List;

public class AchievementsCommand
{
    private static final String NAME = "war medals";
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

        final Boolean isCategory;
        final Boolean isSummary;
        final AchievementCategory achievementCategory;
        final String achievementName;

        AchievementsFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
        {
            this.api = api;
            this.info = info;
            this.session = session;

            // Check if using summary mode
            if (vars.size() == 1 && vars.get(0).toLowerCase() == "summary") {
                isSummary = true;
                isCategory = false;
                achievementCategory = null;
                achievementName = null;
                return;
            }
            isSummary = false;

            // Check for valid argument length
            if (vars.size() == 0) {
                isCategory = false;
                achievementCategory = null;
                achievementName = null;
                return;
            }

            // Check for category
            AchievementCategory category = null;

            try {
                category = AchievementCategory.valueOf(vars.get(0).toUpperCase());
            } catch (IllegalArgumentException exc) {
                category = null;
            }

            if (category != null) {
                achievementCategory = category;
                if (vars.size() == 1){
                    isCategory = true;
                    achievementName = null;
                    return;
                }
            } else {
                achievementCategory = null;
            }

            isCategory = false;

            // Check for command name
            if (vars.size() > 1) {
                achievementName = CombineContent.combine(vars.subList(1, vars.size() - 1));
            } else {
                achievementName = null;
            }
        }

        void execute()
        {
            // Retrieve Users Achievements
            UserAchievementMapper mapper = session.getMapper(UserAchievementMapper.class);
            List<UserAchievement> userAchievements = mapper.getUserAchievements(info.getUser().getId());

            EmbedBuilder start = null;

            // Generate the Embeds
            List<EmbedBuilder>  embeds = List.of();
            for (UserAchievement userAchievement : userAchievements)
            {
                EmbedBuilder embed = userAchievementEmbed(info.getUser(), userAchievement);

                if (achievementName != null) {
                    if (achievementName == userAchievement.getAchievement().getName()) {
                        start = embed;
                    }
                }

                embeds.add(embed);
            }

            // Error out if user has no achievements.
            if (embeds.size() == 0) {
                EmbedBuilder builder = new EmbedBuilder();

                builder.setTitle("You currently have no medals.");
                DMessage.sendMessage(info.getChannel(), builder);
                return;
            }

            // Show summary view if requested.
            if (isSummary) {
                EmbedBuilder builder = new EmbedBuilder();

                String authorName = String.format("%s's Medals", info.getUser().getName());
                builder.setAuthor(authorName, null, info.getUser().getAvatar());

                builder.addField("Total Medals:", String.format("%d", userAchievements.size()), false);


                StringBuilder categoryBuilder = new StringBuilder();
                for (AchievementCategory category : AchievementCategory.values()){
                    Integer count = 0;
                    for (UserAchievement userAchievement : userAchievements) {

                    }

                    categoryBuilder.append(String.format("%s: %d\n", Achievement.getCategoryEmoji(category), count));
                }
                builder.addField("Medals:", categoryBuilder.toString());

                // TODO: ADD MORE INFORMATION ?

                DMessage.sendMessage(info.getChannel(), builder);
                return;
            }

            // Show category view if requested.
            if (isCategory) {
                EmbedBuilder builder = new EmbedBuilder();

                String authorName = String.format("%s's Medals", info.getUser().getName());
                builder.setAuthor(authorName, null, info.getUser().getAvatar());

                // TODO: ADD MORE INFORMATION ?

                DMessage.sendMessage(info.getChannel(), builder);
                return;
            }

            // Show error if required.
            if (achievementName != null && start == null) {
                EmbedBuilder builder = new EmbedBuilder();

                builder.setTitle("Invalid medal name passed.");
                DMessage.sendMessage(info.getChannel(), builder);
                return;
            }

            // Skip menu if user has only one achievement.
            if (embeds.size() == 1) {
                DMessage.sendMessage(info.getChannel(), embeds.get(0));
                return;
            }

            // Start the menu session
            AchievementMenu menu = new AchievementMenu(embeds);
            if (start != null){
                menu.startMenu(api, info, start);
            } else {
                menu.startMenu(api, info);
            }
        }

        private EmbedBuilder userAchievementEmbed(User user, UserAchievement userAchievement) {
            Achievement achievement = userAchievement.getAchievement();
            EmbedBuilder builder = new EmbedBuilder();

            // Create embed.
            builder.setTitle(String.format("%s - %s", achievement.getCategoryEmoji(),achievement.getName()));
            builder.setDescription(achievement.getDescription());

            String authorName = String.format("%s's Medals", user.getName());
            builder.setAuthor(authorName, null, user.getAvatar());

            builder.addField(ZWSP, achievement.getUnlockMethod());

            builder.setFooter("Obtained");
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

            public void startMenu(DiscordApi api, MessageReceivedInformation info, EmbedBuilder embed) {
                for (int i = 0; i < embeds.size(); i++) {
                    if (embeds.get(i).hashCode() == embed.hashCode()) {
                        page = i;
                        break;
                    }
                }
                startMenu(api, info);
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


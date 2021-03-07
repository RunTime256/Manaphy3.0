package bot.command.definition.war.achievements;

import bot.command.MessageCommand;
import bot.command.ReactionCommand;
import bot.command.definition.war.WarCommandFunctionality;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.information.ReactionReceivedInformation;
import bot.discord.listener.ReactionCommandListener;
import bot.discord.message.DMessage;
import bot.discord.reaction.DReaction;
import bot.util.CombineContent;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import sql.Session;
import war.achievement.Achievement;
import war.achievement.UserAchievement;
import war.achievement.WarAchievement;
import war.team.Team;
import war.team.WarTeam;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class AchievementCommand
{
    private static final String NAME = "medals";
    private static final String DESCRIPTION = "List your war medals." +
            "\nUse the variable \"summary\" to view a summary of your medals." +
            "\nUse the medal name to view that specific medal.";
    private static final String SYNTAX = "[summary/name]";

    private AchievementCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                AchievementGrantCommand.createCommand(),
                AchievementCreateCommand.createCommand(),
                AchievementUpdateCommand.createCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .subCommands(subCommands).executor(AchievementCommand::function).build();
    }

    public static MessageCommand createBotCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                AchievementGrantCommand.createBotCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).subCommands(subCommands).build();
    }

    public static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        AchievementsShowFunctionality functionality = new AchievementsShowFunctionality(api, info, vars, session);
        functionality.execute();
    }

    private static class AchievementsShowFunctionality extends WarCommandFunctionality
    {
        final DiscordApi api;
        final MessageReceivedInformation info;
        final Session session;

        final boolean isCategory;
        final boolean isSummary;
        final String achievementCategory;
        final String achievementName;

        AchievementsShowFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
        {
            this.api = api;
            this.info = info;
            this.session = session;

            // Check if using summary mode
            if (vars.size() == 1 && vars.get(0).equalsIgnoreCase("summary"))
            {
                isSummary = true;
                isCategory = false;
                achievementCategory = null;
                achievementName = null;
                return;
            }
            isSummary = false;

            // Check for valid argument length
            if (vars.isEmpty())
            {
                isCategory = false;
                achievementCategory = null;
                achievementName = null;
                return;
            }

            // Check for category
            String category = vars.get(0).toLowerCase();
            if (!Achievement.isCategory(category, session))
                category = null;

            if (category != null)
            {
                achievementCategory = category;
                if (vars.size() == 1)
                {
                    isCategory = true;
                    achievementName = null;
                    return;
                }
            }
            else
            {
                achievementCategory = null;
            }

            isCategory = false;

            // Check for command name
            if (!vars.isEmpty())
                achievementName = CombineContent.combine(vars);
            else
                achievementName = null;
        }

        void execute()
        {
            checkPrerequisites(info.getUser().getId(), session);
            createMenu();
        }

        private void createMenu()
        {
            // Retrieve Users Achievements
            List<UserAchievement> userAchievements = Achievement.getUserAchievements(info.getUser().getId(), session);

            EmbedBuilder start = null;

            // Generate the Embeds
            List<EmbedBuilder> embeds = new ArrayList<>();
            WarTeam team = Team.getTeam(info.getUser().getId(), session);
            for (UserAchievement userAchievement : userAchievements)
            {
                WarAchievement achievement = Achievement.getAchievement(userAchievement.getAchievement(), session);
                EmbedBuilder embed = userAchievementEmbed(info.getUser(), achievement, userAchievement.getAttainedAt(), team);

                if (achievementName != null && achievementName.equalsIgnoreCase(achievement.getFullName()))
                    start = embed;

                embeds.add(embed);
            }

            // Error out if user has no achievements.
            if (embeds.isEmpty())
            {
                EmbedBuilder builder = new EmbedBuilder();

                builder.setTitle("You currently have no medals.");
                DMessage.sendMessage(info.getChannel(), builder);
                return;
            }

            // Show summary view if requested.
            if (isSummary)
            {
                EmbedBuilder builder = new EmbedBuilder();

                String authorName = String.format("%s's Medals", info.getUser().getName());
                builder.setAuthor(authorName, null, info.getUser().getAvatar());

                builder.addField("Total Medals:", String.format("%d", userAchievements.size()), false);

                StringBuilder categoryBuilder = new StringBuilder();
                String[] categories = {"warrior", "artisan", "oracle", "secret"};
                for (String category : categories)
                {
                    int count = 0;
                    for (UserAchievement userAchievement : userAchievements)
                    {
                        if (Achievement.getAchievement(userAchievement.getAchievement(), session).getCategory().equalsIgnoreCase(category))
                            count++;
                    }

                    categoryBuilder.append(String.format("%s: %d\n", WarAchievement.getCategoryEmoji(category), count));
                }
                builder.addField("Medals:", categoryBuilder.toString());

                DMessage.sendMessage(info.getChannel(), builder);
                return;
            }

            // Show category view if requested.
            if (isCategory)
            {
                EmbedBuilder builder = new EmbedBuilder();

                String authorName = String.format("%s's Medals", info.getUser().getName());
                builder.setAuthor(authorName, null, info.getUser().getAvatar());

                // TODO: ADD MORE INFORMATION ?

                DMessage.sendMessage(info.getChannel(), builder);
                return;
            }

            // Show error if required.
            if (achievementName != null && start == null)
            {
                EmbedBuilder builder = new EmbedBuilder();

                builder.setTitle("Invalid medal name passed.");
                DMessage.sendMessage(info.getChannel(), builder);
                return;
            }

            // Skip menu if user has only one achievement.
            if (embeds.size() == 1)
            {
                DMessage.sendMessage(info.getChannel(), embeds.get(0));
                return;
            }

            // Start the menu session
            AchievementMenu menu = new AchievementMenu(embeds);
            if (start != null)
                menu.startMenu(api, info, start);
            else
                menu.startMenu(api, info);
        }

        private EmbedBuilder userAchievementEmbed(User user, WarAchievement achievement, Instant timestamp, WarTeam team)
        {
            EmbedBuilder builder = new EmbedBuilder();

            // Create embed.
            String title = String.format("%s - %s", achievement.getCategoryEmoji(),achievement.getFullName());
            String description = "*" + achievement.getDescription() + "*\n`Unlock: " + achievement.getUnlockMethod() + "`" +
                    "\nValue: " + achievement.getValue();
            builder.setTitle(title).setDescription(description).setThumbnail(achievement.getImage());

            String authorName = String.format("%s's Medals", user.getName());
            builder.setAuthor(authorName, null, user.getAvatar()).setColor(team.getColor());

            builder.setFooter("Obtained");
            builder.setTimestamp(timestamp);

            return builder;
        }

        private class AchievementMenu
        {
            final List<EmbedBuilder> embeds;
            private Message message;
            private int page = 0;
            private List<ReactionCommandListener> listeners = new ArrayList<>();

            public AchievementMenu(List<EmbedBuilder> embeds)
            {
                this.embeds = embeds;
            }

            public void startMenu(DiscordApi api, MessageReceivedInformation info)
            {
                DMessage.sendMessage(info.getChannel(), embeds.get(page)).thenAccept(completedMessage -> {
                    boolean[] completed = {false};
                    message = completedMessage;

                    CompletableFuture<Void> prevFuture = DReaction.addReaction(completedMessage, ReactionCommand.PREV);
                    CompletableFuture<Void> stopFuture = DReaction.addReaction(completedMessage, ReactionCommand.STOP);
                    CompletableFuture<Void> nextFuture = DReaction.addReaction(completedMessage, ReactionCommand.NEXT);

                    nextFuture.thenAccept(aVoid -> {
                        ReactionCommandListener listener = new ReactionCommandListener(
                                info.getUser().getId(), info.getChannel().getId(), completedMessage.getId(), ReactionCommand.NEXT, api,
                                new ReactionCommand(this::nextFunction, completed, true, null)
                        );
                        api.addReactionAddListener(listener).removeAfter(5, TimeUnit.MINUTES);

                        listeners.add(listener);
                    });

                    prevFuture.thenAccept(aVoid -> {
                        ReactionCommandListener listener = new ReactionCommandListener(
                                info.getUser().getId(), info.getChannel().getId(), completedMessage.getId(), ReactionCommand.PREV, api,
                                new ReactionCommand(this::prevFunction, completed, true, null)
                        );
                        api.addReactionAddListener(listener).removeAfter(5, TimeUnit.MINUTES);

                        listeners.add(listener);
                    });

                    stopFuture.thenAccept(aVoid -> {
                        ReactionCommandListener listener = new ReactionCommandListener(
                                info.getUser().getId(), info.getChannel().getId(), completedMessage.getId(), ReactionCommand.STOP, api,
                                new ReactionCommand(this::stopFunction, completed, true, null)
                        );
                        api.addReactionAddListener(listener).removeAfter(5, TimeUnit.MINUTES);

                        listeners.add(listener);
                    });
                });
            }

            public void startMenu(DiscordApi api, MessageReceivedInformation info, EmbedBuilder embed)
            {
                for (int i = 0; i < embeds.size(); i++)
                {
                    if (embeds.get(i).hashCode() == embed.hashCode())
                    {
                        page = i;
                        break;
                    }
                }
                startMenu(api, info);
            }

            private void updateMenu(String lastReaction)
            {
                message.edit(embeds.get(page));
                message.removeReactionByEmoji(info.getUser(), lastReaction);
            }

            private void nextFunction(DiscordApi api, ReactionReceivedInformation info, Session session, Object o)
            {
                page += 1;
                if (page == embeds.size())
                    page = 0;

                updateMenu(ReactionCommand.NEXT);
            }

            private void prevFunction(DiscordApi api, ReactionReceivedInformation info, Session session, Object o)
            {
                page -= 1;
                if (page < 0)
                    page = embeds.size() - 1;

                updateMenu(ReactionCommand.PREV);
            }

            private void stopFunction(DiscordApi api, ReactionReceivedInformation info, Session session, Object o)
            {
                for (ReactionCommandListener listener : listeners)
                    api.removeListener(listener);

                message.removeOwnReactionByEmoji(ReactionCommand.NEXT);
                message.removeOwnReactionByEmoji(ReactionCommand.PREV);
                message.removeOwnReactionByEmoji(ReactionCommand.STOP);
            }
        }
    }
}

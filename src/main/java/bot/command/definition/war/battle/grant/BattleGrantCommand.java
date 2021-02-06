package bot.command.definition.war.battle.grant;

import bot.command.MessageCommand;
import bot.command.definition.war.achievements.AchievementGrantCommand;
import bot.discord.channel.BotChannel;
import bot.discord.channel.DChannel;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.discord.user.DUser;
import bot.util.IdExtractor;
import bot.util.ShowdownUrlEvaluator;
import exception.bot.argument.MissingArgumentException;
import exception.war.battle.BannedBattleFormatException;
import exception.war.battle.BattleException;
import exception.war.battle.DuplicateBattleUrlException;
import exception.war.battle.InvalidBattleUrlException;
import exception.war.team.BannedMemberException;
import exception.war.team.NotATeamMemberException;
import exception.war.team.SameTeamException;
import exception.war.team.member.SameUserException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import sql.Session;
import war.battle.Battle;
import war.battle.PreviousBattleMultiplier;
import war.battle.boss.BossDamage;
import war.battle.boss.BossHealth;
import war.battle.boss.BossMessage;
import war.battle.boss.WarBoss;
import war.team.Team;
import war.team.WarTeam;

import java.awt.*;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BattleGrantCommand
{
    private static final String NAME = "grant";
    private static final String DESCRIPTION = "Loser of a battle grants tokens to the winner";
    private static final String SYNTAX = "<user> <showdown replay>";

    private BattleGrantCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                BattleGrantForceCommand.createCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION)
                .syntax(SYNTAX).subCommands(subCommands).executor(BattleGrantCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (info.getChannel().getId() != BotChannel.getChannelId("pokemon", "battle", session))
            return;

        if (vars.isEmpty())
            throw new MissingArgumentException("user");
        else if (vars.size() == 1)
            throw new MissingArgumentException("showdown replay");

        BattleGrantFunctionality functionality = new BattleGrantFunctionality(api, info, vars, session);
        functionality.execute();
    }

    private static class BattleGrantFunctionality
    {
        private final DiscordApi api;
        private final MessageReceivedInformation info;
        private final Session session;

        private final long winner;
        private final long loser;
        private final String url;

        BattleGrantFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
        {
            this.api = api;
            this.info = info;
            this.session = session;

            winner = IdExtractor.getId(vars.get(0));
            loser = info.getUser().getId();
            url = ShowdownUrlEvaluator.convertUrl(vars.get(1));
        }

        void execute()
        {
            try
            {
                grantBattle();
            }
            catch (SameUserException e)
            {
                DMessage.sendMessage(info.getChannel(), "You cannot grant tokens to yourself!");
            }
            catch (SameTeamException e)
            {
                DMessage.sendMessage(info.getChannel(), "You are on the same team as the recipient!");
            }
            catch (NotATeamMemberException | BannedMemberException | BattleException e)
            {
                DMessage.sendMessage(info.getChannel(), e.getMessage());
            }
        }

        void grantBattle()
        {
            if (winner == loser)
                throw new SameUserException(winner, loser);
            if (!Team.isTeamMember(winner, session))
                throw new NotATeamMemberException(winner);
            if (!Team.isTeamMember(loser, session))
                throw new NotATeamMemberException(loser);
            if (Team.isBanned(winner, session))
                throw new BannedMemberException(winner);
            if (Team.isBanned(loser, session))
                throw new BannedMemberException(loser);
            if (Team.onSameTeam(winner, loser, session))
                throw new SameTeamException(winner, loser);

            if (!ShowdownUrlEvaluator.isValidUrl(url))
                throw new InvalidBattleUrlException(url);
            if (Battle.isBattle(url, session))
                throw new DuplicateBattleUrlException(url);

            String format = ShowdownUrlEvaluator.getFormat(url);
            if (Battle.isBannedFormat(format, session))
                throw new BannedBattleFormatException(format);

            int wins = Battle.getWins(winner, session) + 1;
            int losses = Battle.getLosses(loser, session) + 1;
            int winnerTotal = Battle.getTotalBattles(winner, session);
            int loserTotal = Battle.getTotalBattles(loser, session);
            int winStreak = Battle.getWinStreak(winner, session) + 1;
            int lossStreak = Battle.getLossStreak(loser, session) + 1;

            int winTokens = 5;
            int loseTokens = 2;
            PreviousBattleMultiplier winnerPreviousBattleMultiplier = Battle.getMultiplier(winner, winnerTotal, session);
            PreviousBattleMultiplier loserPreviousBattleMultiplier = Battle.getMultiplier(loser, loserTotal, session);

            int winnerMultiplier = winnerPreviousBattleMultiplier.getNewMultiplier(info.getTime());
            int winnerMultiplierCount = winnerPreviousBattleMultiplier.getNewMultiplierCount(info.getTime(), winnerMultiplier);
            int loserMultiplier = loserPreviousBattleMultiplier.getNewMultiplier(info.getTime());
            int loserMultiplierCount = loserPreviousBattleMultiplier.getNewMultiplierCount(info.getTime(), loserMultiplier);
            int bonusMultiplier = getBonusMultiplier(format);

            Battle.addBattle(winner, loser, url, winStreak, lossStreak, info.getTime(), winTokens, loseTokens,
                    winnerMultiplier, winnerMultiplierCount, bonusMultiplier, loserMultiplier, loserMultiplierCount, session);

            grantAchievements(wins, losses, winnerTotal, loserTotal, winStreak, lossStreak);
            WarBoss boss = bossDamage(winnerMultiplier, url);

            EmbedBuilder builder = tokenEmbed(DUser.getUser(api, winner), info.getUser(), Team.getTeam(winner, session),
                    winTokens, loseTokens, winnerMultiplier, winnerMultiplierCount, bonusMultiplier, url, boss);
            DMessage.sendMessage(info.getChannel(), builder);
            if (boss != null)
            {
                EmbedBuilder bossEmbed = bossEmbed(boss);
                BossMessage message = BossDamage.getBossMessage(session);
                api.getMessageById(message.getMessageId(), DChannel.getChannel(api, message.getChannelId()))
                        .thenAccept(pinnedMessage -> pinnedMessage.edit(bossEmbed));
            }
        }

        private void grantAchievements(int wins, int losses, int winnerTotal, int loserTotal, int winStreak, int lossStreak)
        {
            List<String> winnerAchievements = new ArrayList<>();
            List<String> loserAchievements = new ArrayList<>();
            if (Battle.isAchievement("wins", wins, session))
                winnerAchievements.add(Battle.getAchievement("wins", wins, session));
            if (Battle.isAchievement("losses", losses, session))
                loserAchievements.add(Battle.getAchievement("losses", losses, session));
            if (Battle.isAchievement("win_streak", winStreak, session))
                winnerAchievements.add(Battle.getAchievement("win_streak", winStreak, session));
            if (Battle.isAchievement("loss_streak", lossStreak, session))
                loserAchievements.add(Battle.getAchievement("loss_streak", lossStreak, session));
            if (Battle.isAchievement("plays", winnerTotal, session))
                winnerAchievements.add(Battle.getAchievement("plays", winnerTotal, session));
            if (Battle.isAchievement("plays", loserTotal, session))
                loserAchievements.add(Battle.getAchievement("plays", loserTotal, session));

            addAchievements(winnerAchievements, winner);
            addAchievements(loserAchievements, loser);
        }

        private void addAchievements(List<String> loserAchievements, long loser)
        {
            for (String achievementName: loserAchievements)
            {
                List<String> vars = new ArrayList<>();
                vars.add(String.valueOf(loser));
                vars.add(achievementName);
                vars.add(String.valueOf(false));
                AchievementGrantCommand.function(api, info, vars, session);
            }
        }

        private EmbedBuilder tokenEmbed(User winner, User loser, WarTeam team, int winnerTokens, int loserTokens,
                                        int multiplier, int multiplierCount, int bonusMultiplier, String url, WarBoss boss)
        {
            EmbedBuilder builder = new EmbedBuilder();
            String win = "Earned " + (winnerTokens * (multiplier + (bonusMultiplier - 1))) + " tokens";
            String lose = "Earned " + loserTokens + " tokens";

            if (multiplier > 1)
            {
                if (bonusMultiplier > 1)
                {
                    win += "\n Multiplier: " + (multiplier + (bonusMultiplier - 1)) + " (" + multiplierCount + "/5 battles at current multiplier)";
                }
                else
                {
                    win += "\nMultiplier: " + multiplier + " (" + multiplierCount + "/5 battles at current multiplier)";
                }
            }
            else
            {
                win += "\nBonus Multiplier: " + bonusMultiplier;
            }

            builder.setTitle(team.getFullName()).setColor(team.getColor()).setThumbnail(team.getTokenImage())
                    .addField(winner.getDiscriminatedName(), win).addField(loser.getDiscriminatedName(), lose)
                    .addField("Boss Damage", Integer.toString(boss.getDamage()))
                    .setUrl(url);

            return builder;
        }

        private int getBonusMultiplier(String format)
        {
            int bonus = 1;
            String bonusFormat = Battle.getBonusFormat(session);
            ZonedDateTime dateTime = ZonedDateTime.ofInstant(info.getTime(), ZoneId.of("America/New_York"));
            if (!bonusFormat.isEmpty() && format.equals(bonusFormat))
                bonus += 3;
            if (dateTime.getDayOfWeek() == DayOfWeek.SATURDAY || dateTime.getDayOfWeek() == DayOfWeek.SUNDAY)
                bonus += 2;
            return bonus;
        }

        private WarBoss bossDamage(int multiplier, String url)
        {
            int damage = multiplier + 5;
            String bossName = "Grimmsnarl";

            BossHealth health = BossDamage.getHealth(bossName, session);
            BossDamage.addDamage(bossName, damage, url, session);
            String bossImage = BossDamage.getBossImage(bossName, session);

            if (health.getCurrentHealth() == 0)
                return null;

            return new WarBoss(bossName, bossImage, health, damage);
        }

        private EmbedBuilder bossEmbed(WarBoss boss)
        {
            EmbedBuilder builder = new EmbedBuilder();
            int percent = boss.getCurrentHealth() * 100 / boss.getTotalHealth();
            String desc = "HP: " + boss.getHealthEmojis();

            if (percent >= 50)
                builder.setColor(Color.GREEN);
            else if (boss.getCurrentHealth() > 0)
                builder.setColor(Color.YELLOW);
            else
                builder.setColor(Color.RED);

            builder.setTitle(boss.getName()).setImage(boss.getImage()).setDescription(desc);

            return builder;
        }
    }
}

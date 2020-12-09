package bot.command.definition.war;

import bot.command.MessageCommand;
import bot.command.definition.war.achievements.AchievementCommand;
import bot.command.definition.war.ban.BanCommand;
import bot.command.definition.war.battle.BattleCommand;
import bot.command.definition.war.code.CodeCommand;
import bot.command.definition.war.contest.ContestCommand;
import bot.command.definition.war.fanart.FanartCommand;
import bot.command.definition.war.game.GameCommand;
import bot.command.definition.war.join.JoinCommand;
import bot.command.definition.war.leaderboard.LeaderboardCommand;
import bot.command.definition.war.puzzle.PuzzleCommand;
import bot.command.definition.war.scorecard.ScorecardCommand;
import bot.command.verification.RoleRequirement;

import java.util.Arrays;
import java.util.List;

public class WarCommand
{
    private static final String NAME = "war";
    private static final String DESCRIPTION = "Commands for the server war";

    private WarCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                BattleCommand.createCommand(),
                JoinCommand.createCommand(),
                PuzzleCommand.createCommand(),
//                TypeCommand.createCommand(),
                LeaderboardCommand.createCommand(),
                ScorecardCommand.createCommand(),
                AchievementCommand.createCommand(),
                BanCommand.createCommand(),
                FanartCommand.createCommand(),
                ContestCommand.createCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.VERIFIED)
                .subCommands(subCommands).build();
    }

    public static MessageCommand createBotCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                CodeCommand.createBotCommand(),
                GameCommand.createBotCommand(),
                AchievementCommand.createBotCommand(),
                PuzzleCommand.createBotCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.VERIFIED)
                .subCommands(subCommands).build();
    }
}

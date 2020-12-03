package bot.command.definition.war.puzzle;

import bot.command.MessageCommand;
import bot.command.definition.war.achievements.AchievementGrantCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.log.PuzzleLogger;
import exception.bot.argument.InvalidArgumentException;
import exception.bot.argument.MissingArgumentException;
import exception.war.puzzle.FuturePuzzleException;
import exception.war.puzzle.MissingPuzzleRequirementException;
import exception.war.puzzle.NotAPuzzleException;
import exception.war.puzzle.PuzzleException;
import exception.war.team.BannedMemberException;
import exception.war.team.NotATeamMemberException;
import org.javacord.api.DiscordApi;
import sql.Session;
import war.puzzle.Puzzle;
import war.puzzle.PuzzleGuess;
import war.team.Team;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class PuzzleGrantCommand
{
    private static final String NAME = "grant";
    private static final String DESCRIPTION = "Grant a bot puzzle solution to a user";
    private static final String SYNTAX = "<puzzle name> <user id>";
    private static PuzzleLogger puzzleLogger = null;

    private PuzzleGrantCommand()
    {
    }

    public static void setPuzzleLogger(PuzzleLogger puzzleLogger)
    {
        PuzzleGrantCommand.puzzleLogger = puzzleLogger;
    }

    public static MessageCommand createBotCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .requirement(RoleRequirement.VERIFIED).executor(PuzzleGrantCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (vars.isEmpty())
            throw new MissingArgumentException("puzzle name");
        else if (vars.size() == 1)
            throw new MissingArgumentException("user id");

        PuzzleSolveFunctionality functionality = new PuzzleSolveFunctionality(api, info, vars, session);
        functionality.execute();
    }

    private static class PuzzleSolveFunctionality
    {
        private final DiscordApi api;
        private final MessageReceivedInformation info;
        private final Session session;
        private final long userId;
        private final PuzzleGuess guess;

        PuzzleSolveFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
        {
            this.api = api;
            this.info = info;
            this.session = session;
            String puzzleName = vars.remove(0).toUpperCase();
            try
            {
                userId = Long.parseLong(vars.remove(0));
            }
            catch (NumberFormatException e)
            {
                throw new InvalidArgumentException("user id");
            }
            guess = new PuzzleGuess(puzzleName, "", userId, info.getTime());
        }

        void execute()
        {
            try
            {
                solvePuzzle();
            }
            catch (FuturePuzzleException e)
            {
                DMessage.sendMessage(info.getChannel(), "It seems it is not the right *time* for that yet.");
            }
            catch (MissingPuzzleRequirementException e)
            {
                logMissingRequirement(guess);
                DMessage.sendMessage(info.getChannel(), e.getMessage());
            }
            catch (PuzzleException e)
            {
                if (e.getColor() == Color.YELLOW)
                    DMessage.sendMessage(info.getChannel(), e.getMessage());
                else
                    throw e;
            }
        }

        private void solvePuzzle()
        {
            if (!Team.isTeamMember(userId, session))
                throw new NotATeamMemberException(userId);
            if (Team.isBanned(userId, session))
                throw new BannedMemberException(userId);

            if (!Puzzle.exists(guess.getName(), session))
                throw new NotAPuzzleException(guess.getName());

            boolean correct = Puzzle.guess(guess, api, session);
            log(guess, correct);
            if (correct)
            {
                DMessage.sendMessage(info.getChannel(), "`" + userId + "` solved puzzle `" + guess.getName() + "`");

                if (Puzzle.isAchievementPuzzle(guess.getName(), session))
                {
                    String achievementName = Puzzle.getAchievement(guess.getName(), session);
                    List<String> vars = new ArrayList<>();
                    vars.add(String.valueOf(info.getUser().getId()));
                    vars.add(achievementName);
                    AchievementGrantCommand.function(api, info, vars, session);
                }
            }
            else
            {
                DMessage.sendMessage(info.getChannel(), "`" + userId + "` was incorrect on puzzle `" + guess.getName() + "`");
            }
        }

        private static void log(PuzzleGuess guess, boolean correct)
        {
            if (puzzleLogger != null)
                puzzleLogger.log(guess, correct);
        }

        private static void logMissingRequirement(PuzzleGuess guess)
        {
            if (puzzleLogger != null)
                puzzleLogger.logMissingRequirement(guess);
        }
    }
}

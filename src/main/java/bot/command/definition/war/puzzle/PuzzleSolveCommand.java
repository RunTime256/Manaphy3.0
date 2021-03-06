package bot.command.definition.war.puzzle;

import bot.command.MessageCommand;
import bot.command.ReactionCommand;
import bot.command.definition.war.WarCommandFunctionality;
import bot.command.definition.war.achievements.AchievementGrantCommand;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.information.ReactionReceivedInformation;
import bot.discord.listener.ReactionCommandListener;
import bot.discord.message.DMessage;
import bot.discord.reaction.DReaction;
import bot.log.PuzzleLogger;
import exception.bot.argument.MissingArgumentException;
import bot.util.CombineContent;
import exception.bot.command.InvalidCommandException;
import exception.war.puzzle.FuturePuzzleException;
import exception.war.puzzle.MissingPuzzleRequirementException;
import exception.war.puzzle.NotAPuzzleException;
import exception.war.puzzle.PuzzleException;
import exception.war.team.BannedMemberException;
import exception.war.team.NotATeamMemberException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import sql.Session;
import war.puzzle.Puzzle;
import war.puzzle.PuzzleGuess;
import war.team.Team;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PuzzleSolveCommand
{
    private static final String NAME = "solve";
    private static final String DESCRIPTION = "Solve a puzzle";
    private static final String SYNTAX = "<puzzle name> <guess>";
    private static PuzzleLogger puzzleLogger = null;

    private PuzzleSolveCommand()
    {
    }

    public static void setPuzzleLogger(PuzzleLogger puzzleLogger)
    {
        PuzzleSolveCommand.puzzleLogger = puzzleLogger;
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).syntax(SYNTAX)
                .executor(PuzzleSolveCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        if (vars.isEmpty())
            throw new MissingArgumentException("puzzle name");
        else if (vars.size() == 1)
            throw new MissingArgumentException("guess");

        PuzzleSolveFunctionality functionality = new PuzzleSolveFunctionality(api, info, vars, session);
        functionality.execute();
    }

    private static class PuzzleSolveFunctionality extends WarCommandFunctionality
    {
        private final DiscordApi api;
        private final MessageReceivedInformation info;
        private final Session session;
        private final PuzzleGuess guess;

        PuzzleSolveFunctionality(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
        {
            this.api = api;
            this.info = info;
            this.session = session;
            String puzzleName = vars.remove(0).toUpperCase();
            String puzzleGuess = CombineContent.combine(vars).toUpperCase();
            guess = new PuzzleGuess(puzzleName, puzzleGuess, info.getUser().getId(), info.getTime());
        }

        void execute()
        {
            checkPrerequisites(info.getUser().getId(), session);
            try
            {
                solvePuzzle();
            }
            catch (FuturePuzzleException e)
            {
                if (Puzzle.isAffectedByBigPuzzle(guess.getName(), session))
                {
                    if (Puzzle.hasCompletedBigPuzzle(info.getUser().getId(), session))
                        DMessage.sendMessage(info.getChannel(), "It is *still* not *time* for that yet.");
                    else
                        DMessage.sendMessage(info.getChannel(), "It seems it is not the right *time* for that yet.");
                }
                else
                {
                    DMessage.sendMessage(info.getChannel(), "It seems something or someone essential is missing.");
                }
            }
            catch (MissingPuzzleRequirementException e)
            {
                logMissingRequirement(guess);
                throw new InvalidCommandException(e.getMessage());
            }
            catch (PuzzleException e)
            {
                if (e.getColor() == Color.YELLOW)
                    throw new InvalidCommandException(e.getMessage());
                else
                    throw e;
            }
        }

        private void solvePuzzle()
        {
            long userId = info.getUser().getId();
            if (!Team.isTeamMember(userId, session))
                throw new NotATeamMemberException(userId);
            if (Team.isBanned(userId, session))
                throw new BannedMemberException(userId);

            if (info.getServer() != null)
            {
                info.delete();
                throw new InvalidCommandException("Please make guesses in DMs only.");
            }

            if (!Puzzle.exists(guess.getName(), session))
                throw new NotAPuzzleException(guess.getName());

            if (Puzzle.isInfinite(guess.getName(), session))
            {
                boolean correct = Puzzle.guess(guess, api, session);
                log(guess, correct);
                if (correct)
                {
                    DMessage.sendMessage(info.getChannel(), "You are correct! Thank you for your submission.");

                    if (Puzzle.isAchievementPuzzle(guess.getName(), session))
                    {
                        String achievementName = Puzzle.getAchievement(guess.getName(), session);
                        List<String> vars = new ArrayList<>();
                        vars.add(String.valueOf(info.getUser().getId()));
                        vars.add(achievementName);
                        vars.add(String.valueOf(false));
                        AchievementGrantCommand.function(api, info, vars, session);
                    }

                    if (Puzzle.isMultiAchievementPuzzle(guess.getName(), session) && Puzzle.hasCompletedMultiAchievementPuzzle(guess.getName(), info.getUser().getId(), session))
                    {
                        String achievementName = Puzzle.getMultiAchievement(guess.getName(), session);
                        List<String> vars = new ArrayList<>();
                        vars.add(String.valueOf(info.getUser().getId()));
                        vars.add(achievementName);
                        vars.add(String.valueOf(false));
                        AchievementGrantCommand.function(api, info, vars, session);
                    }

                    if (Puzzle.hasResponse(guess.getName(), session))
                    {
                        DMessage.sendMessage(info.getChannel(), new EmbedBuilder().setImage(Puzzle.getResponse(guess.getName(), session))
                                .setTitle("I found something that might be useful for you!"));
                    }
                }
                else
                {
                    DMessage.sendMessage(info.getChannel(), "You were incorrect. Please try again.");
                }
            }
            else
            {
                CompletableFuture<Message> message = DMessage.sendMessage(info.getChannel(),
                        "Your solution for the puzzle `" + guess.getName() + "` is `" + guess.getGuess() + "`. Is that correct?");
                message.thenAccept(completedMessage ->
                {
                    CompletableFuture<Void> yesFuture = DReaction.addReaction(completedMessage, ReactionCommand.YES);
                    CompletableFuture<Void> noFuture = DReaction.addReaction(completedMessage, ReactionCommand.NO);

                    boolean[] completed = {false};
                    yesFuture.thenAccept(aVoid -> api.addReactionAddListener(
                            new ReactionCommandListener(
                                    info.getUser().getId(), info.getChannel().getId(), completedMessage.getId(), ReactionCommand.YES, api,
                                    new ReactionCommand(PuzzleSolveFunctionality::yesFunction, completed, guess))).removeAfter(30, TimeUnit.SECONDS));

                    noFuture.thenAccept(aVoid -> api.addReactionAddListener(
                            new ReactionCommandListener(
                                    info.getUser().getId(), info.getChannel().getId(), completedMessage.getId(), ReactionCommand.NO, api,
                                    new ReactionCommand(PuzzleSolveFunctionality::noFunction, completed, null))).removeAfter(30, TimeUnit.SECONDS).addRemoveHandler(() ->
                    {
                        if (!completed[0])
                        {
                            throw new InvalidCommandException("Took too long to respond...");
                        }
                    }));
                });
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

        private static void yesFunction(DiscordApi api, ReactionReceivedInformation info, Session session, Object o)
        {
            PuzzleGuess guess = (PuzzleGuess)o;
            try
            {
                boolean correct = Puzzle.guess(guess, api, session);
                log(guess, correct);
                DMessage.sendMessage(info.getChannel(), "Thank you for your guess! Stay tuned for the results of the puzzle!");
            }
            catch (FuturePuzzleException e)
            {
                if (Puzzle.isAffectedByBigPuzzle(guess.getName(), session))
                {
                    if (Puzzle.hasCompletedBigPuzzle(info.getUser().getId(), session))
                        DMessage.sendMessage(info.getChannel(), "It is *still* not *time* for that yet.");
                    else
                        DMessage.sendMessage(info.getChannel(), "It seems it is not the right *time* for that yet.");
                }
                else
                {
                    DMessage.sendMessage(info.getChannel(), "It seems something or someone essential is missing.");
                }
            }
            catch (PuzzleException e)
            {
                if (e.getColor() == Color.YELLOW)
                    throw new InvalidCommandException(e.getMessage());
                else
                    throw e;
            }
        }

        private static void noFunction(DiscordApi api, ReactionReceivedInformation info, Session session, Object o)
        {
            DMessage.sendMessage(info.getChannel(), "Puzzle submission cancelled.");
        }
    }
}

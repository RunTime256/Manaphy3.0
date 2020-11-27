package war.puzzle;

import bot.discord.server.DServer;
import exception.war.puzzle.AlreadyGuessedPuzzleException;
import exception.war.puzzle.AlreadySolvedPuzzleException;
import exception.war.puzzle.InactivePuzzleException;
import exception.war.puzzle.MissingPuzzleRequirementException;
import exception.war.puzzle.NotAPuzzleException;
import exception.war.puzzle.PuzzleAlreadyEndedException;
import exception.war.puzzle.PuzzleAlreadyStartedException;
import exception.war.puzzle.PuzzleNotStartedException;
import org.javacord.api.DiscordApi;
import sql.Session;

import java.time.Instant;
import java.util.List;

public class Puzzle
{
    private Puzzle()
    {
    }

    public static List<String> getSolvedInfinitePuzzles(long userId, Session session)
    {
        List<String> puzzles = session.getMapper(PuzzleMapper.class).getSolvedInfinitePuzzles(userId);
        puzzles.sort(String::compareTo);
        return puzzles;
    }

    public static List<String> getUnsolvedDiscoveredInfinitePuzzles(long userId, Session session)
    {
        List<String> puzzles = session.getMapper(PuzzleMapper.class).getUnsolvedDiscoveredInfinitePuzzles(userId);
        puzzles.sort(String::compareTo);
        return puzzles;
    }

    public static boolean guess(PuzzleGuess guess, DiscordApi api, Session session)
    {
        String puzzleName = guess.getName();
        String puzzleGuess = guess.getGuess();
        Long userId = guess.getUserId();
        Instant time = guess.getTime();

        if (!exists(puzzleName, session))
            throw new NotAPuzzleException(puzzleName);
        if (!isActive(puzzleName, time, session))
            throw new InactivePuzzleException(puzzleName);

        if (isInfinite(puzzleName, session) && alreadySolved(userId, puzzleName, session))
            throw new AlreadySolvedPuzzleException(puzzleName);
        else if (!isInfinite(puzzleName, session) && alreadyGuessed(userId, puzzleName, session))
            throw new AlreadyGuessedPuzzleException(puzzleName);

        if (!(hasRole(userId, puzzleName, api, session) && hasCode(userId, puzzleName, session)))
            throw new MissingPuzzleRequirementException(puzzleName);

        session.getMapper(PuzzleMapper.class).addGuess(puzzleName, puzzleGuess, userId, time);

        return isCorrect(puzzleName, puzzleGuess, session);
    }

    public static boolean exists(String puzzleName, Session session)
    {
        return session.getMapper(PuzzleMapper.class).exists(puzzleName);
    }

    public static boolean isActive(String puzzleName, Instant time, Session session)
    {
        return session.getMapper(PuzzleMapper.class).isActive(puzzleName, time);
    }

    public static boolean hasStarted(String puzzleName, Instant time, Session session)
    {
        return session.getMapper(PuzzleMapper.class).hasStarted(puzzleName, time);
    }

    public static boolean hasEnded(String puzzleName, Instant time, Session session)
    {
        return session.getMapper(PuzzleMapper.class).hasEnded(puzzleName, time);
    }

    public static boolean isInfinite(String puzzleName, Session session)
    {
        return session.getMapper(PuzzleMapper.class).isInfinite(puzzleName);
    }

    public static boolean alreadyGuessed(Long userId, String puzzleName, Session session)
    {
        return session.getMapper(PuzzleMapper.class).hasGuessed(puzzleName, userId);
    }

    public static boolean alreadySolved(Long userId, String puzzleName, Session session)
    {
        return session.getMapper(PuzzleMapper.class).hasSolved(puzzleName, userId);
    }

    public static boolean hasCode(Long userId, String puzzleName, Session session)
    {
        return session.getMapper(PuzzleMapper.class).hasCodeRequirements(puzzleName, userId);
    }

    public static boolean hasRole(Long userId, String puzzleName, DiscordApi api, Session session)
    {
        List<PuzzleRoleRequirement> requirements = session.getMapper(PuzzleMapper.class).roleRequirements(puzzleName);

        for (PuzzleRoleRequirement requirement: requirements)
        {
            if (!DServer.hasRole(api, requirement.getServerId(), requirement.getRoleId(), userId))
                return false;
        }

        return true;
    }

    public static boolean isCorrect(String puzzleName, String puzzleGuess, Session session)
    {
        return session.getMapper(PuzzleMapper.class).correct(puzzleName, puzzleGuess);
    }

    public static void start(String puzzleName, Instant time, Session session)
    {
        if (!exists(puzzleName, session))
            throw new NotAPuzzleException(puzzleName);
        if (hasStarted(puzzleName, time, session))
            throw new PuzzleAlreadyStartedException(puzzleName);
        if (hasEnded(puzzleName, time, session))
            throw new PuzzleAlreadyEndedException(puzzleName);
        session.getMapper(PuzzleMapper.class).start(puzzleName, time);
    }

    public static void end(String puzzleName, Instant time, Session session)
    {
        if (!exists(puzzleName, session))
            throw new NotAPuzzleException(puzzleName);
        if (!hasStarted(puzzleName, time, session))
            throw new PuzzleNotStartedException(puzzleName);
        if (hasEnded(puzzleName, time, session))
            throw new PuzzleAlreadyEndedException(puzzleName);
        session.getMapper(PuzzleMapper.class).end(puzzleName, time);
    }
}

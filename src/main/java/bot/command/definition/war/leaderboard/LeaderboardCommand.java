package bot.command.definition.war.leaderboard;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;

import java.util.Arrays;
import java.util.List;

public class LeaderboardCommand
{
    private static final String NAME = "leaderboard";
    private static final String DESCRIPTION = "Leaderboard commands for the war";

    private LeaderboardCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                LeaderboardUpdateCommand.createCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.ADMIN)
                .subCommands(subCommands).build();
    }
}

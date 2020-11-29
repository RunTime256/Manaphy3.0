package bot.command.definition.war.game;

import bot.command.MessageCommand;

import java.util.Arrays;
import java.util.List;

public class GameCommand
{
    private static final String NAME = "game";
    private static final String DESCRIPTION = "Game commands for the war";

    private GameCommand()
    {
    }

    public static MessageCommand createBotCommand()
    {
        List<MessageCommand> subCommands = Arrays.asList(
                GameGrantCommand.createBotCommand()
        );
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).subCommands(subCommands).build();
    }
}

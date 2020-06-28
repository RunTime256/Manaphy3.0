package bot;

import bot.command.definition.test.TestCommand;
import bot.discord.Bot;
import bot.discord.BotMapper;
import bot.discord.listener.MessageCommandListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sql.Session;
import sql.SessionFactory;

import java.io.IOException;

public class BotRunner
{
    private static final Logger logger = LogManager.getLogger(BotRunner.class);

    public static void main(String[] args)
    {
        String name = getName(args);
        if (name == null)
        {
            logger.error("Provide bot name as single argument");
            return;
        }

        try
        {
            initDatabaseConfig();
        }
        catch (IOException ignored)
        {
            return;
        }

        Bot bot = getBot(name);
        bot.start();
        addUserCommands(bot);
    }

    private static String getName(String[] args)
    {
        if (args.length != 1)
            return null;
        else
            return args[0];
    }

    private static void initDatabaseConfig() throws IOException
    {
        try
        {
            SessionFactory.init("mybatis-config.xml");
        }
        catch (IOException e)
        {
            logger.error("Config file could not be found");
            throw e;
        }
    }

    private static Bot getBot(String name)
    {
        Bot bot;
        try (Session session = SessionFactory.getSession())
        {
            bot = session.getMapper(BotMapper.class).getBot(name);
        }
        return bot;
    }

    private static void addUserCommands(Bot bot)
    {
        MessageCommandListener listener = new MessageCommandListener(bot.getPrefix());
        listener.addCommand(TestCommand.createCommand());
        bot.addListener(listener);
    }
}

package bot;

import bot.command.HelpMessageCommand;
import bot.command.definition.get.GetCommand;
import bot.command.definition.help.HelpCommand;
import bot.command.definition.owner.stop.StopCommand;
import bot.command.definition.owner.test.TestCommand;
import bot.command.definition.war.WarCommand;
import bot.discord.Bot;
import bot.discord.BotMapper;
import bot.discord.channel.ChannelMapper;
import bot.discord.listener.MessageCommandListener;
import bot.discord.listener.ReactionCommandListener;
import bot.log.ErrorLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
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
        DiscordApi api = bot.start();

        Long channelId = getLogChannel();
        ErrorLogger logger = null;
        if (channelId != null)
            logger = new ErrorLogger(api, channelId);
        addUserCommands(bot, api, logger);

        ReactionCommandListener.setLogger(logger);
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

    private static Long getLogChannel()
    {
        Long channelId;
        try (Session session = SessionFactory.getSession())
        {
            channelId = session.getMapper(ChannelMapper.class).getChannel("pokemon", "log");
        }
        return channelId;
    }

    private static void addUserCommands(Bot bot, DiscordApi api, ErrorLogger logger)
    {
        MessageCommandListener listener = new MessageCommandListener(bot.getPrefix(), api, logger);
        listener.addCommand(TestCommand.createCommand());
        listener.addCommand(StopCommand.createCommand());
        listener.addCommand(GetCommand.createCommand());
        listener.addCommand(WarCommand.createCommand());
        listener.addHelpCommand(HelpCommand.createCommand(bot.getPrefix(), bot.getName()));
        bot.addListener(listener);
    }
}

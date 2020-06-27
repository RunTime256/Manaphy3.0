package bot;

import bot.discord.Bot;
import bot.discord.listener.MessageCommandListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BotRunner
{
    private static final Logger logger = LogManager.getLogger(BotRunner.class);

    public static void main(String[] args)
    {
        if (args.length != 2)
        {
            logger.error("Provide bot token and prefix as arguments");
            return;
        }

        Bot bot = new Bot(args[0], args[1]);
        addUserCommands(bot);
        bot.start();
    }

    private static void addUserCommands(Bot bot)
    {
        MessageCommandListener listener = new MessageCommandListener(bot.getPrefix());
        bot.addListener(listener);
    }
}

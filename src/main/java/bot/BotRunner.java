package bot;

import bot.discord.Bot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BotRunner
{
    private static final Logger logger = LogManager.getLogger(BotRunner.class);

    public static void main(String[] args)
    {
        if (args.length < 1)
        {
            logger.error("Provide bot token as argument");
            return;
        }

        Bot bot = new Bot(args[0], "+");
        bot.start();
    }
}

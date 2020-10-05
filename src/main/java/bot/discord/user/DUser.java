package bot.discord.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class DUser
{
    private static final Logger logger = LogManager.getLogger(DUser.class);

    private DUser()
    {
    }

    public static User getUser(Server server, long id, String name)
    {
        User user = server.getMemberByDiscriminatedName(name).orElse(null);
        if (user != null)
            return user;

        if (id != 0L)
        {
            user = server.getMemberById(id).orElse(null);
        }
        else
        {
            Iterator<User> iter = server.getMembersByNickname(name).iterator();
            if (iter.hasNext())
            {
                user = iter.next();
            }
            else
            {
                iter = server.getMembersByName(name).iterator();
                if (iter.hasNext())
                    user = iter.next();
            }
        }
        return user;
    }

    public static User getUser(DiscordApi api, long userId)
    {
        User user;
        try
        {
            user = api.getUserById(userId).get();
        }
        catch (InterruptedException e)
        {
            logger.error("Exception occurred when getting User", e);
            Thread.currentThread().interrupt();
            user = null;
        }
        catch (ExecutionException e)
        {
            logger.error("Exception occurred when getting User", e);
            user = null;
        }
        return user;
    }
}

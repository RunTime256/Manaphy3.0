package bot.discord.user;

import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.Iterator;

public class DUser
{
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
}

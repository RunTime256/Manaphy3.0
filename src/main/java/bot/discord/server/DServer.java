package bot.discord.server;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.Iterator;
import java.util.Optional;

public class DServer
{
    private DServer()
    {
    }

    public static boolean hasRole(DiscordApi api, User user, Long roleId)
    {
        Optional<Role> role = api.getRoleById(roleId);
        return role.map(value -> value.hasUser(user)).orElse(false);
    }

    public static Server getServer(DiscordApi api, long id, String name)
    {
        Server s = null;
        if (id != 0L)
        {
            s = api.getServerById(id).orElse(null);
        }
        else
        {
            Iterator<Server> iter = api.getServersByName(name).iterator();
            if (iter.hasNext())
                s = iter.next();
        }
        return s;
    }
}
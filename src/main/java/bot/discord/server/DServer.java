package bot.discord.server;

import bot.discord.user.DUser;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import sql.Session;

import java.util.Iterator;
import java.util.Optional;

public class DServer
{
    private DServer()
    {
    }

    public static boolean hasRole(DiscordApi api, Long serverId, Long roleId, Long userId)
    {
        Server server = getServer(api, serverId, null);
        if (server == null)
            return false;

        User user = DUser.getUser(server, userId, null);
        if (user == null)
            return false;

        Optional<Role> role = api.getRoleById(roleId);
        return role.map(value -> value.hasUser(user)).orElse(false);
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

    public static Server getServer(DiscordApi api, String name, Session session)
    {
        Long serverId = session.getMapper(ServerMapper.class).getServer(name);
        return api.getServerById(serverId).orElse(null);
    }
}

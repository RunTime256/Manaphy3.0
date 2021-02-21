package bot.discord.role;

import bot.discord.user.DUser;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import sql.Session;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class DRole
{
    private DRole()
    {
    }

    public static boolean hasRole(DiscordApi api, User user, Long roleId)
    {
        Optional<Role> role = api.getRoleById(roleId);
        return role.map(value -> value.hasUser(user)).orElse(false);
    }

    public static Role getRole(Server server, long id, String name)
    {
        Role role = null;

        if (server == null)
            return null;

        if (id != 0L)
        {
            role = server.getRoleById(id).orElse(null);
        }
        else
        {
            List<Role> list = server.getRolesByName(name);
            if (!list.isEmpty())
                role = list.get(0);
        }
        return role;
    }

    public static CompletableFuture<Void> addRole(Server server, String serverName, String roleName, long userId, Session session)
    {
        if (server == null)
            return null;

        long roleId = session.getMapper(RoleMapper.class).getRole(serverName, roleName);
        return addRole(server, roleId, userId);
    }

    public static CompletableFuture<Void> addRole(Server server, long roleId, long userId)
    {
        if (server == null)
            return null;

        Role role = getRole(server, roleId, null);
        User user = DUser.getUser(server, userId, null);

        return user.addRole(role);
    }
}

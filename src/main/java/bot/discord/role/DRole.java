package bot.discord.role;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.ServerChannel;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.List;
import java.util.Optional;

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
}

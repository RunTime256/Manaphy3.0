package bot.discord.role;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;

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
}

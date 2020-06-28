package bot.command.verification;

import bot.discord.role.DRole;
import bot.discord.role.RoleMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.user.User;
import sql.Session;

public class RoleCheck
{
    private static final Logger logger = LogManager.getLogger(RoleCheck.class);

    private RoleCheck()
    {
    }

    public static boolean hasPermission(Session session, DiscordApi api, User user, RoleRequirement requirement)
    {
        if (requirement == RoleRequirement.OWNER)
        {
            return api.getOwnerId() == user.getId();
        }

        String roleName = requirement.name().toLowerCase();
        Long roleId = getRoleId(session, roleName);
        if (roleId == null)
        {
            String error = "Could not find role " + roleName;
            logger.error(error);
            return false;
        }

        return DRole.hasRole(api, user, roleId);
    }

    private static Long getRoleId(Session session, String roleName)
    {
        return session.getMapper(RoleMapper.class).getRole("pokemon", roleName);
    }
}

package war.puzzle;

public class PuzzleRoleRequirement
{
    private final Long serverId;
    private final Long roleId;

    public PuzzleRoleRequirement(Long serverId, Long roleId)
    {
        this.serverId = serverId;
        this.roleId = roleId;
    }

    public Long getServerId()
    {
        return serverId;
    }

    public Long getRoleId()
    {
        return roleId;
    }
}

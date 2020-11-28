package bot.command.definition.achievements;

import java.util.List;

public interface UserAchievementMapper {

    List<UserAchievement> getUserAchievements(Long userId);

    UserAchievement getUserAchievement(String name, Long userId);

}
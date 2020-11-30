package war.achievement;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AchievementMapper {

    @Select("SELECT a.identifier, a.name, ac.name AS category, a.description, a.unlock_method FROM cc4.achievement a " +
            "LEFT JOIN cc4.achievement_category ac ON ac.id = a.category_id " +
            "WHERE a.name = #{name}")
    @Results(value = {
            @Result(property = "identifier", column = "identifier"),
            @Result(property = "name", column = "name"),
            @Result(property = "category", column = "category"),
            @Result(property = "description", column = "description"),
            @Result(property = "unlockMethod", column = "unlock_method")
    })
    WarAchievement getAchievement(@Param("name") String name);

    List<UserAchievement> getUserAchievements(@Param("userId") long userId);

    UserAchievement getUserAchievement(@Param("userId") long userId, @Param("name") String name);

    @Select("SELECT EXISTS (SELECT * FROM cc4.achievement_category WHERE name = #{category})")
    boolean isCategory(@Param("category") String category);
}

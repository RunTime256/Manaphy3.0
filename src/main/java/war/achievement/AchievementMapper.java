package war.achievement;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.time.Instant;
import java.util.List;

public interface AchievementMapper {

    @Select("SELECT a.name, a.full_name, a.description, ac.name AS category, a.image, a.unlock_method FROM cc4.achievement a " +
            "LEFT JOIN cc4.achievement_category ac ON ac.id = a.category_id " +
            "WHERE a.name = #{name}")
    @Results(value = {
            @Result(property = "name", column = "name"),
            @Result(property = "fullName", column = "full_name"),
            @Result(property = "description", column = "description"),
            @Result(property = "category", column = "category"),
            @Result(property = "image", column = "image"),
            @Result(property = "unlockMethod", column = "unlock_method")
    })
    WarAchievement getAchievement(@Param("name") String name);

    @Select("SELECT ua.user_id, a.name, ua.timestamp FROM cc4.user_achievement ua LEFT JOIN cc4.achievement a ON a.id = ua.achievement_id " +
            "WHERE ua.user_id = #{userId}")
    List<UserAchievement> getUserAchievements(@Param("userId") long userId);

    @Select("SELECT EXISTS(SELECT * FROM cc4.achievement_category WHERE name = #{category})")
    boolean isCategory(@Param("category") String category);

    @Select("SELECT EXISTS(SELECT * FROM cc4.achievement WHERE name = #{name})")
    boolean isAchievement(@Param("name") String name);

    @Select("SELECT EXISTS(SELECT * FROM cc4.user_achievement ua LEFT JOIN cc4.achievement a ON a.id = ua.achievement_id " +
            "WHERE ua.user_id = #{userId} AND a.name = #{name})")
    boolean hasAchievement(@Param("userId") long userId, @Param("name") String name);

    @Insert("INSERT INTO cc4.user_achievement (user_id, achievement_id, timestamp) " +
            "VALUES (#{userId}, (SELECT id FROM cc4.achievement WHERE name = #{name}), #{timestamp})")
    void grantAchievement(@Param("userId") long userId, @Param("name") String name, @Param("timestamp") Instant timestamp);

    @Insert("INSERT INTO cc4.achievement (name, full_name, description, category_id, image, unlock_method, difficulty) VALUES " +
            "(#{name}, #{fullName}, #{description}, (SELECT id FROM cc4.achievement_category WHERE name = #{category}), #{image}, #{unlockMethod}, #{difficulty})")
    void createAchievement(@Param("name") String name, @Param("fullName") String fullName, @Param("description") String description,
                           @Param("category") String category, @Param("image") String image, @Param("unlockMethod") String unlockMethod,
                           @Param("difficulty") int difficulty);
}

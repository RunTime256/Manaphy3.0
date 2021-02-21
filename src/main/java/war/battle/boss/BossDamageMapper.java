package war.battle.boss;

import org.apache.ibatis.annotations.*;

public interface BossDamageMapper
{
    @Select("WITH d AS (SELECT COALESCE(y.damage, 0) AS damage FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT SUM(bd.damage) AS damage FROM cc4.boss_damage bd LEFT JOIN cc4.boss b ON b.id = bd.boss_id WHERE name = #{bossName} GROUP BY b.id) y " +
            "ON 1 = 1), " +
            "th AS (SELECT health FROM cc4.boss WHERE name = #{bossName}) " +
            "SELECT th.health - d.damage AS current_health, th.health AS total_health FROM d LEFT JOIN th ON 1 = 1")
    @Results(value = {
            @Result(property = "currentHealth", column = "current_health"),
            @Result(property = "totalHealth", column = "total_health")
    })
    BossHealth getHealth(@Param("bossName") String bossName);

    @Select("WITH ch AS (SELECT COALESCE(y.damage, 0) AS damage FROM " +
            "(SELECT 1 a) x LEFT JOIN " +
            "(SELECT SUM(bd.damage) AS damage FROM cc4.boss_damage bd LEFT JOIN cc4.boss b ON b.id = bd.boss_id WHERE name = #{bossName} GROUP BY b.id) y " +
            "ON 1 = 1), " +
            "th AS (SELECT health FROM cc4.boss WHERE name = #{bossName}), " +
            "p AS (SELECT (th.health - ch.damage) * 100.0 / th.health AS percent FROM ch LEFT JOIN th ON 1 = 1) " +
            "SELECT image FROM cc4.boss_image bi LEFT JOIN cc4.boss b ON b.id = bi.boss_id " +
            "LEFT JOIN p ON 1 = 1 WHERE b.name = #{bossName} AND p.percent <= bi.max_percent ORDER BY bi.max_percent ASC LIMIT 1")
    String getBossImage(@Param("bossName") String bossName);

    @Select("SELECT bp.message_id, bp.channel_id FROM cc4.boss_pin bp")
    @Results(value = {
            @Result(property = "messageId", column = "message_id"),
            @Result(property = "channelId", column = "channel_id")
    })
    BossMessage getBossMessage();

    @Insert("INSERT INTO cc4.boss_damage (boss_id, battle_id, damage) VALUES " +
            "((SELECT id FROM cc4.boss WHERE name = #{bossName}), " +
            "(SELECT id FROM cc4.battle WHERE url = #{battleUrl}), #{damage})")
    void addDamage(@Param("bossName") String bossName, @Param("damage") int damage, @Param("battleUrl") String battleUrl);
}

package war.puzzle;

import org.apache.ibatis.annotations.Param;

import java.time.Instant;
import java.util.List;

public interface PuzzleMapper
{
    boolean exists(@Param("name") String name);

    boolean isActive(@Param("name") String name);

    boolean isInfinite(@Param("name") String name);

    boolean hasGuessed(@Param("name") String name, @Param("userId") Long userId);

    boolean hasSolved(@Param("name") String name, @Param("userId") Long userId);

    boolean hasCodeRequirements(@Param("name") String name, @Param("userId") Long userId);

    List<PuzzleRoleRequirement> roleRequirements(@Param("name") String name);

    boolean correct(@Param("name") String name, @Param("guess") String guess);

    void addGuess(@Param("name") String name, @Param("guess") String guess, @Param("userId") Long userId, @Param("time") Instant time);
}

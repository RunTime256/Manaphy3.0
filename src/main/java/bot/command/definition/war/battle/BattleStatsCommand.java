package bot.command.definition.war.battle;

import bot.command.MessageCommand;
import bot.command.verification.RoleRequirement;
import bot.discord.information.MessageReceivedInformation;
import bot.discord.message.DMessage;
import bot.discord.user.DUser;
import exception.war.team.BannedMemberException;
import exception.war.team.NotATeamMemberException;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import sql.Session;
import war.battle.Battle;
import war.battle.PreviousBattleMultiplier;
import war.team.Team;
import war.team.WarTeam;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class BattleStatsCommand
{
    private static final String NAME = "stats";
    private static final String DESCRIPTION = "Look up your battle stats";

    private BattleStatsCommand()
    {
    }

    public static MessageCommand createCommand()
    {
        return new MessageCommand.MessageCommandBuilder(NAME).description(DESCRIPTION).requirement(RoleRequirement.VERIFIED)
                .executor(BattleStatsCommand::function).build();
    }

    private static void function(DiscordApi api, MessageReceivedInformation info, List<String> vars, Session session)
    {
        BattleStatsFunctionality functionality = new BattleStatsFunctionality(api, info, session);
        functionality.execute();
    }

    private static class BattleStatsFunctionality
    {
        private final DiscordApi api;
        private final MessageReceivedInformation info;
        private final Session session;

        BattleStatsFunctionality(DiscordApi api, MessageReceivedInformation info, Session session)
        {
            this.api = api;
            this.info = info;
            this.session = session;
        }

        void execute()
        {
            try
            {
                statsEmbed();
            }
            catch (BannedMemberException e)
            {
                DMessage.sendMessage(info.getChannel(), "You are banned from the war.");
            }
            catch (NotATeamMemberException e)
            {
                DMessage.sendMessage(info.getChannel(), "You have not joined the war yet. Use the command `+war join` to be chosen for a team.");
            }
        }

        void statsEmbed()
        {
            long userId = info.getUser().getId();
            if (!Team.isTeamMember(userId, session))
                throw new NotATeamMemberException(userId);
            if (Team.isBanned(userId, session))
                throw new BannedMemberException(userId);

            int wins = Battle.getWins(userId, session);
            int total = Battle.getTotalBattles(userId, session);
            PreviousBattleMultiplier multiplier = Battle.getMultiplier(userId, total, session);
            User user = DUser.getUser(api, userId);
            WarTeam team = Team.getTeam(userId, session);

            DMessage.sendMessage(info.getChannel(), statsEmbed(user, multiplier, team, wins, total));
        }

        private EmbedBuilder statsEmbed(User user, PreviousBattleMultiplier multiplier, WarTeam team, int wins, int total)
        {
            EmbedBuilder builder = new EmbedBuilder();

            int mult = multiplier.getNewMultiplier(info.getTime());
            ZonedDateTime dateTime = ZonedDateTime.ofInstant(info.getTime(), ZoneId.of("America/New_York"));
            if (dateTime.getDayOfWeek() == DayOfWeek.SATURDAY || dateTime.getDayOfWeek() == DayOfWeek.SUNDAY)
                mult += 2;
            int multCount = multiplier.getNewMultiplierCount(info.getTime(), mult) - 1;

            String description = "**Current Multiplier:** " + mult;
            if (mult > 1)
                description += " (" + multCount + "/5 battles at multiplier)";
            description += "\n\n**Time since last battle:** " + timeDifference(info.getTime(), multiplier.getTimestamp());

            int losses = total - wins;
            double ratio;
            if (losses == 0)
                ratio = wins;
            else ratio = 1.0 * wins / losses;

            builder.setAuthor(user.getDiscriminatedName(), null, user.getAvatar()).setTitle("Battle Stats")
                    .setDescription(description).setColor(team.getColor())
                    .addField("Wins", String.valueOf(wins))
                    .addField("Losses", String.valueOf(losses))
                    .addField("Total", String.valueOf(total))
                    .addField("W/L Ratio", String.valueOf(ratio));

            return builder;
        }

        private String timeDifference(Instant now, Instant previous)
        {
            if (previous == null)
                return "N/A";

            Duration duration = Duration.between(previous, now);
            String difference = "";

            long days = duration.toDays();
            int hours = duration.toHoursPart();
            int minutes = duration.toMinutesPart();
            int seconds = duration.toSecondsPart();

            if (days > 1)
                difference += days + " days, ";
            else if (days > 0)
                difference += days + " day, ";

            if (hours > 1)
                difference += hours + " hours, ";
            else if (hours > 0)
                difference += hours + " hour, ";

            if (minutes > 1)
                difference += minutes + " minutes, ";
            else if (minutes > 0)
                difference += minutes + " minute, ";

            if (seconds > 1)
                difference += seconds + " seconds, ";
            else if (seconds > 0)
                difference += seconds + " second, ";

            return difference.substring(0, difference.length() - 2);
        }
    }
}

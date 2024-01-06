package ru.ra_tech.garden_manager.database.repositories.garden;

import org.jooq.Record3;
import org.jooq.Record5;
import org.jooq.Record8;
import org.jooq.RecordMapper;
import ru.ra_tech.garden_manager.database.repositories.user.UserDto;
import ru.ra_tech.garden_manager.database.repositories.user_role.UserRole;

import java.util.List;

public class GardenMappers {
    public static final RecordMapper<
            Record8<Long, String, String, Long, String, String, String, List<GardenParticipantDto>>,
            GardenDto
            > GARDEN_MAPPER = new GardenMapper();
    public static final RecordMapper<Record3<Long, Long, List<Long>>, GardenUsersDto> GARDEN_USERS_MAPPER =
            new GardenUsersMapper();
    public static final RecordMapper<
            Record5<Long, String, String, String, String>,
            GardenParticipantDto
            > GARDEN_PARTICIPANT_MAPPER = new GardenParticipantMapper();

    private static class GardenMapper implements
            RecordMapper<
                    Record8<Long, String, String, Long, String, String, String, List<GardenParticipantDto>>,
                    GardenDto
                    > {
        @Override
        public GardenDto map(
                Record8<Long, String, String, Long, String, String, String, List<GardenParticipantDto>> row
        ) {
            return new GardenDto(
                    row.value1(),
                    row.value2(),
                    row.value3(),
                    new UserDto(
                            row.value4(),
                            row.value5(),
                            row.value6(),
                            row.value7()
                    ),
                    io.vavr.collection.List.ofAll(row.value8())
            );
        }
    }

    private static class GardenUsersMapper implements RecordMapper<
            Record3<Long, Long, List<Long>>,
            GardenUsersDto
            > {
        @Override
        public GardenUsersDto map(Record3<Long, Long, List<Long>> row) {
            return new GardenUsersDto(row.value1(), row.value2(), io.vavr.collection.List.ofAll(row.value3()));
        }
    }

    private static class GardenParticipantMapper implements RecordMapper<
            Record5<Long, String, String, String, String>,
            GardenParticipantDto
            > {

        @Override
        public GardenParticipantDto map(Record5<Long, String, String, String, String> row) {
            return new GardenParticipantDto(
                    row.value1(),
                    row.value2(),
                    row.value3(),
                    row.value4(),
                    UserRole.valueOf(row.value5().toUpperCase())
            );
        }
    }

    private GardenMappers() {}
}

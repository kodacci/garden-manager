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
                Record8<Long, String, String, Long, String, String, String, List<GardenParticipantDto>> record
        ) {
            return new GardenDto(
                    record.value1(),
                    record.value2(),
                    record.value3(),
                    new UserDto(
                            record.value4(),
                            record.value5(),
                            record.value6(),
                            record.value7()
                    ),
                    io.vavr.collection.List.ofAll(record.value8())
            );
        }
    }

    private static class GardenUsersMapper implements RecordMapper<
            Record3<Long, Long, List<Long>>,
            GardenUsersDto
            > {
        @Override
        public GardenUsersDto map(Record3<Long, Long, List<Long>> record) {
            return new GardenUsersDto(record.value1(), record.value2(), io.vavr.collection.List.ofAll(record.value3()));
        }
    }

    private static class GardenParticipantMapper implements RecordMapper<
            Record5<Long, String, String, String, String>,
            GardenParticipantDto
            > {

        @Override
        public GardenParticipantDto map(Record5<Long, String, String, String, String> record) {
            return new GardenParticipantDto(
                    record.value1(),
                    record.value2(),
                    record.value3(),
                    record.value4(),
                    UserRole.valueOf(record.value5().toUpperCase())
            );
        }
    }

    private GardenMappers() {}
}

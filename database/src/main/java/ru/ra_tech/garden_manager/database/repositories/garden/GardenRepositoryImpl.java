package ru.ra_tech.garden_manager.database.repositories.garden;

import io.micrometer.core.annotation.Timed;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.jooq.DSLContext;
import org.jooq.Record8;
import org.jooq.SelectOnConditionStep;
import org.springframework.lang.Nullable;
import ru.ra_tech.garden_manager.database.repositories.AbstractRWRepository;
import ru.ra_tech.garden_manager.database.repositories.api.GardenRepository;
import ru.ra_tech.garden_manager.database.repositories.user_role.UserRole;
import ru.ra_tech.garden_manager.database.schema.tables.records.GardensRecord;
import ru.ra_tech.garden_manager.failure.AppFailure;
import ru.ra_tech.garden_manager.failure.DatabaseFailure;

import java.util.Objects;

import static org.jooq.impl.DSL.*;
import static ru.ra_tech.garden_manager.database.repositories.garden.GardenMappers.*;
import static ru.ra_tech.garden_manager.database.schema.Tables.GARDENS_PARTICIPANTS;
import static ru.ra_tech.garden_manager.database.schema.Tables.USER_ROLES;
import static ru.ra_tech.garden_manager.database.schema.tables.Gardens.GARDENS;
import static ru.ra_tech.garden_manager.database.schema.tables.Users.USERS;
import static ru.ra_tech.garden_manager.failure.DatabaseFailure.DatabaseFailureCode.GARDEN_REPOSITORY_FAILURE;

public class GardenRepositoryImpl extends AbstractRWRepository<Long, CreateGardenRequest, GardenDto, GardensRecord>
        implements GardenRepository {
    private static final int GARDENS_LIMIT = 1000;
    private static final int GARDENS_PARTICIPANTS_LIMIT = 100;

    public GardenRepositoryImpl(DSLContext dsl) {
        super(dsl, GARDENS);
    }

    @Override
    protected AppFailure toFailure(@Nullable Throwable error) {
        return new DatabaseFailure(GARDEN_REPOSITORY_FAILURE, error, getClass().getName());
    }

    private SelectOnConditionStep<
            Record8<Long, String, String, Long, String, String, String, java.util.List<GardenParticipantDto>>
            > makeSelectStep() {
        return getContext().select(
                        GARDENS.ID,
                        GARDENS.NAME,
                        GARDENS.ADDRESS,
                        USERS.ID,
                        USERS.LOGIN,
                        USERS.NAME,
                        USERS.EMAIL,
                        multiset(
                                select(
                                        GARDENS_PARTICIPANTS.PARTICIPANT,
                                        USERS.LOGIN,
                                        USERS.NAME,
                                        USERS.EMAIL,
                                        USER_ROLES.NAME
                                )
                                        .from(GARDENS_PARTICIPANTS)
                                        .join(USERS)
                                        .on(GARDENS_PARTICIPANTS.PARTICIPANT.eq(USERS.ID))
                                        .join(USER_ROLES)
                                        .on(GARDENS_PARTICIPANTS.ROLE.eq(USER_ROLES.ID))
                                        .where(GARDENS_PARTICIPANTS.GARDEN.eq(GARDENS.ID))
                        )
                                .convertFrom(r -> r.map(GARDEN_PARTICIPANT_MAPPER))
                )
                .from(GARDENS)
                .join(USERS)
                .on(GARDENS.OWNER.eq(USERS.ID));
    }

    private Either<AppFailure, Option<GardenDto>> selectGarden(long id) {
        return Try.of(
                () -> makeSelectStep()
                        .where(GARDENS.ID.eq(id))
                        .fetchOne(GARDEN_MAPPER)
        )
                .toEither()
                .mapLeft(this::toFailure)
                .map(Option::of);
    }

    @Override
    @Timed(
            value = "repository.call",
            extraTags = {"repository_name", "garden"},
            histogram = true,
            percentiles = {0.90, 0.95, 0.99}
    )
    public Either<AppFailure, GardenDto> create(CreateGardenRequest garden) {
        return Try.of(
                () -> getContext().insertInto(GARDENS)
                        .set(GARDENS.NAME, garden.name())
                        .set(GARDENS.ADDRESS, garden.address())
                        .set(GARDENS.OWNER, garden.ownerId())
                        .returningResult(GARDENS.ID)
                        .fetchOne()
        )
                .andThen(Objects::requireNonNull)
                .map(row -> row.getValue(GARDENS.ID, Long.class))
                .andThen(Objects::requireNonNull)
                .toEither()
                .mapLeft(this::toFailure)
                .flatMap(this::selectGarden)
                .flatMap(maybeDto -> maybeDto.toEither(toFailure(null)));
    }

    @Override
    @Timed(
            value = "repository.call",
            extraTags = {"repository_name", "garden"},
            histogram = true,
            percentiles = {0.90, 0.95, 0.99}
    )
    public Either<AppFailure, Option<GardenDto>> findById(Long id) {
        return selectGarden(id);
    }

    @Override
    @Timed(
            value = "repository.call",
            extraTags = {"repository_name", "garden"},
            histogram = true,
            percentiles = {0.90, 0.95, 0.99}
    )
    public Either<AppFailure, List<GardenDto>> listByUserId(long id) {
        return Try.of(
                () -> makeSelectStep()
                        .where(GARDENS.OWNER.eq(id))
                        .and(GARDENS.DELETED.eq(false))
                        .limit(GARDENS_LIMIT)
                        .fetch(GARDEN_MAPPER)
        )
                .toEither()
                .mapLeft(this::toFailure)
                .map(List::ofAll);
    }

    @Override
    @Timed(
            value = "repository.call",
            extraTags = {"repository_name", "garden"},
            histogram = true,
            percentiles = {0.90, 0.95, 0.99}
    )
    public Either<AppFailure, Option<GardenUsersDto>> getGardenUsers(long gardenId) {
        return Try.of(
                () -> getContext().select(
                        GARDENS.ID,
                        GARDENS.OWNER,
                        multiset(
                                select(GARDENS_PARTICIPANTS.PARTICIPANT)
                                        .from(GARDENS_PARTICIPANTS)
                                        .where(GARDENS_PARTICIPANTS.GARDEN.eq(gardenId))
                        )
                                .convertFrom(r -> r.into(Long.class))
                )
                        .from(GARDENS)
                        .where(GARDENS.ID.eq(gardenId))
                        .fetchOne(GARDEN_USERS_MAPPER)
        )
                .toEither()
                .mapLeft(this::toFailure)
                .map(Option::of);
    }

    @Override
    @Timed(
            value = "repository.call",
            extraTags = {"repository_name", "garden"},
            histogram = true,
            percentiles = {0.90, 0.95, 0.99}
    )
    public Either<AppFailure, Boolean> addParticipant(long gardenId, long userId, UserRole role) {
        return Try.of(
                () -> getContext().insertInto(GARDENS_PARTICIPANTS)
                        .set(GARDENS_PARTICIPANTS.GARDEN, gardenId)
                        .set(GARDENS_PARTICIPANTS.PARTICIPANT, userId)
                        .set(
                                GARDENS_PARTICIPANTS.ROLE,
                                select(USER_ROLES.ID)
                                        .from(USER_ROLES)
                                        .where(USER_ROLES.NAME.eq(role.toString()))
                        )
                        .execute()
        )
                .toEither()
                .mapLeft(this::toFailure)
                .map(count -> count > 0);
    }

    @Override
    @Timed(
            value = "repository.call",
            extraTags = {"repository_name", "garden"},
            histogram = true,
            percentiles = {0.90, 0.95, 0.99}
    )
    public Either<AppFailure, List<GardenParticipantDto>> listParticipants(long gardenId) {
        return Try.of(
                () -> getContext().select(
                        GARDENS_PARTICIPANTS.PARTICIPANT,
                        USERS.LOGIN,
                        USERS.NAME,
                        USERS.EMAIL,
                        USER_ROLES.NAME
                        )
                        .from(GARDENS_PARTICIPANTS)
                        .join(USERS)
                        .on(GARDENS_PARTICIPANTS.PARTICIPANT.eq(USERS.ID))
                        .join(USER_ROLES)
                        .on(USER_ROLES.ID.eq(GARDENS_PARTICIPANTS.ROLE))
                        .where(GARDENS_PARTICIPANTS.GARDEN.eq(gardenId))
                        .limit(GARDENS_PARTICIPANTS_LIMIT)
                        .fetch(GARDEN_PARTICIPANT_MAPPER)
        )
                .toEither()
                .mapLeft(this::toFailure)
                .map(List::ofAll);
    }

    @Override
    @Timed(
            value = "repository.call",
            extraTags = {"repository_name", "garden"},
            histogram = true,
            percentiles = {0.90, 0.95, 0.99}
    )
    public Either<AppFailure, Option<GardenDto>> update(Long id, CreateGardenRequest update) {
        return Try.of(
                () -> getContext().update(GARDENS)
                        .set(GARDENS.NAME, update.name())
                        .set(GARDENS.ADDRESS, update.address())
                        .execute()
        )
                .toEither()
                .mapLeft(this::toFailure)
                .flatMap(rows -> findById(id));
    }
}

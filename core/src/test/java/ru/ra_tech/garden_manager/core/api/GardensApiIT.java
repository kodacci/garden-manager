package ru.ra_tech.garden_manager.core.api;

import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import ru.ra_tech.garden_manager.core.controllers.gardens.dto.CreateGardenRequest;
import ru.ra_tech.garden_manager.core.controllers.gardens.dto.GardenData;
import ru.ra_tech.garden_manager.core.controllers.gardens.dto.GardenParticipantData;
import ru.ra_tech.garden_manager.core.controllers.users.dto.UserData;
import ru.ra_tech.garden_manager.database.repositories.garden.CreateGardenDto;
import ru.ra_tech.garden_manager.database.repositories.garden.GardenParticipantDto;
import ru.ra_tech.garden_manager.database.repositories.garden.GardenRepository;
import ru.ra_tech.garden_manager.database.repositories.user.CreateUserDto;
import ru.ra_tech.garden_manager.database.repositories.user.UserDto;
import ru.ra_tech.garden_manager.database.repositories.user_role.UserRole;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static ru.ra_tech.garden_manager.core.api.TestUtils.writeUser;
import static ru.ra_tech.garden_manager.database.schema.Tables.*;

@DisplayName("Gardens API test")
class GardensApiIT extends AbstractApiIT {
    private static final String GARDENS_API_URL = "/api/v1/gardens";

    private UserDto owner = null;
    private UserDto participant = null;

    @BeforeAll
    void beforeAll() {
        val user = new CreateUserDto(
                "gardensTestUser", "Gardens Test User", null, "abc12345"
        );
        val testParticipant = new CreateUserDto(
                "gardenParticipant", "Garden Participant", null, "abc12345"
        );

        this.owner = writeUser(getDsl(), user);
        this.participant = writeUser(getDsl(), testParticipant);
    }

    @AfterAll
    void afterAll() {
        getDsl().deleteFrom(USERS).execute();
        getDsl().deleteFrom(GARDENS).execute();
        getDsl().deleteFrom(GARDENS_PARTICIPANTS).execute();
    }

    @Test
    @DisplayName("Should add new garden on POST on /api/v1/gardens")
    void shouldAddNewGarden() {
        val garden = new CreateGardenRequest("Test garden", "Test garden address");

        val entity = new HttpEntity<>(garden, generateAuthHeaders(owner.login()));
        val response = getRestTemplate().exchange(GARDENS_API_URL, HttpMethod.POST, entity, GardenData.class);
        assertHttpResponse(response, HttpStatus.CREATED);

        val body = response.getBody();
        assertThat(body).isNotNull().isInstanceOf(GardenData.class);
        assertThat(body.name()).isEqualTo(garden.name());
        assertThat(body.address()).isEqualTo(garden.address());
        assertThat(body.owner()).isEqualTo(UserData.of(owner));
        assertThat(body.participants()).isEmpty();
    }

    @Test
    @DisplayName("Should add new participant to garden on POST on /api/v1/gardens/${id}/add_participant/${userId}")
    void shouldAddNewParticipantToGarden() {
        val gardenData = new CreateGardenDto(
                "Test garden with participant", "Test garden address", owner.id()
        );
        val repo = new GardenRepository(getDsl());
        val garden = repo.create(gardenData).get();
        val entity = new HttpEntity<>(generateAuthHeaders(owner.login()));

        val url = String.format("%s/%d/add_participant/%d", GARDENS_API_URL, garden.id(), participant.id());
        val response = getRestTemplate().exchange(url, HttpMethod.POST, entity, GardenParticipantData[].class);
        assertHttpResponse(response);

        val body = response.getBody();
        assertThat(body).isNotNull().isInstanceOf(GardenParticipantData[].class).hasSize(1);
        val participantData = body[0];
        assertThat(participantData.id()).isEqualTo(participant.id());
        assertThat(participantData.name()).isEqualTo(participant.name());
        assertThat(participantData.email()).isEqualTo(participant.email());
        assertThat(participantData.role()).isEqualTo(UserRole.EXECUTOR);

        val participants = repo.listParticipants(garden.id()).get();
        assertThat(participants).hasSize(1);
        assertThat(participants.peek()).isInstanceOf(GardenParticipantDto.class);
        assertThat(participantData).isEqualTo(GardenParticipantData.of(participants.peek()));
    }

    @Test
    @DisplayName("Should get garden by id on GET on /api/v1/gardens/${id}")
    void shouldGetGardenById() {
        val gardenData = new CreateGardenDto(
                "Test garden by id", "Test garden address", owner.id()
        );
        val repo = new GardenRepository(getDsl());
        val garden = repo.create(gardenData).get();
        val entity = new HttpEntity<>(generateAuthHeaders(owner.login()));
        val response = getRestTemplate().exchange(
                String.format("%s/%d", GARDENS_API_URL, garden.id()), HttpMethod.GET, entity, GardenData.class
        );

        assertHttpResponse(response);

        val body = response.getBody();
        assertThat(body).isNotNull()
                .isInstanceOf(GardenData.class)
                .isEqualTo(GardenData.of(garden));
    }

    @Test
    @DisplayName("Should list user gardens on GET on /api/v1/gardens")
    void shouldListUserGardens() {
        val userDto = new CreateUserDto(
                "listGardensUser", "List Gardens User", null, "abc12345"
        );
        val user = writeUser(getDsl(), userDto);

        val gardenData1 = new CreateGardenDto("Test list gardens 1", "Address 1", user.id());
        val gardenData2 = new CreateGardenDto("Test list gardens 2", "Address 2", user.id());

        val repo = new GardenRepository(getDsl());
        val garden1 = repo.create(gardenData1).get();
        val garden2 = repo.create(gardenData2).get();
        val entity = new HttpEntity<>(generateAuthHeaders(user.login()));

        val response = getRestTemplate().exchange(GARDENS_API_URL, HttpMethod.GET, entity, GardenData[].class);

        assertHttpResponse(response);

        val body = response.getBody();
        assertThat(body).isNotNull().isInstanceOf(GardenData[].class).hasSize(2);
        assertThat(Arrays.asList(body)).isEqualTo(Stream.of(garden1, garden2).map(GardenData::of).toList());
    }
}

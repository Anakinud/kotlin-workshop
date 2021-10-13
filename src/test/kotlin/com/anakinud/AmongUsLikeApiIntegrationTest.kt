package com.anakinud

import com.anakinud.controller.ApiPaths.*
import com.anakinud.controller.dto.GameDto
import com.anakinud.controller.dto.RegistrationDto
import com.anakinud.controller.dto.WinDto
import com.anakinud.entity.Impostor
import com.anakinud.entity.Player
import com.anakinud.entity.User
import com.anakinud.security.Token
import com.anakinud.service.NotificationService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpStatus
import org.springframework.http.RequestEntity
import java.util.UUID.randomUUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = ["server_port=0, management.port=0"])
open class AmongUsLikeApiIntegrationTest {

    companion object {
        var impostor: Impostor? = null
        var players: MutableList<Player> = mutableListOf()
    }

    @Autowired
    lateinit var client: TestRestTemplate

    @Autowired
    lateinit var testCases: TestCases

    @BeforeEach
    fun clearImpostor() {
        impostor = null
        players = mutableListOf()
    }

    @Test
    fun shouldRegisterUser() {
        //given
        val register = RegistrationDto(randomUUID().toString(), "some-password")

        //when
        val response = client.postForEntity(REGISTER_ENDPOINT, register, String.javaClass)

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun shouldNotRegisterUser() {
        //given
        val registerData = testCases.register()

        //when
        val response = client.postForEntity(REGISTER_ENDPOINT, registerData, String.javaClass)

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun shouldLogin() {
        //given
        val registerData = testCases.register();

        //when
        val response = client.postForEntity(AUTH_ENDPOINT, registerData, Token::class.java)

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        //and
        assertThat(response.body.token).isNotNull
    }

    @Test
    fun shouldNotLogin() {
        //given
        val register = RegistrationDto(randomUUID().toString(), "some-password")

        //when
        val response = client.postForEntity(AUTH_ENDPOINT, register, Token::class.java)

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun shouldCreateGame() {
        //given
        val (_, userToken) = testCases.createUserAndLogin()

        //when
        val response = client.exchange(
            RequestEntity.post(GAME_ENDPOINT).header(AUTHORIZATION, "Bearer $userToken").body(GameDto(randomUUID().toString())),
            GameDto::class.java
        )

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
    }

    @Test
    fun shouldNotCreateGame() {
        //given
        val (_, userToken) = testCases.createUserAndLogin()
        val createdGame = testCases.crateGame(userToken);

        //when
        val response = client.exchange(
            RequestEntity.post(GAME_ENDPOINT).header(AUTHORIZATION, "Bearer $userToken").body(createdGame),
            GameDto::class.java
        )

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
    }

    @Test
    fun shouldOtherUserJoinGame() {
        //given
        val (_, userToken) = testCases.createUserAndLogin()
        val createdGame = testCases.crateGame(userToken);
        val (_, joinerToken) = testCases.createUserAndLogin();

        //when
        val response = client.exchange(
            RequestEntity.post(JOIN_GAME_ENDPOINT, createdGame.name).header(AUTHORIZATION, "Bearer $joinerToken").build(),
            String::class.java
        )

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun shouldNotJoinGameIfNotExists() {
        //given
        val (_, userToken) = testCases.createUserAndLogin()

        //when
        val response = client.exchange(
            RequestEntity.post(JOIN_GAME_ENDPOINT, "notExistingGame").header(AUTHORIZATION, "Bearer $userToken").build(),
            String::class.java
        )

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun shouldStartGame() {
        //given
        val userTokens = testCases.createUserAndLogin(3)
        val game = testCases.crateGame(userTokens.values.first());
        testCases.joinGame(userTokens.values, game)

        //when
        val response = client.exchange(
            RequestEntity.post(START_GAME_ENDPOINT, game.name).header(AUTHORIZATION, "Bearer ${userTokens.values.first()}").build(),
            String::class.java
        )

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        //and
        assertThat(impostor).isNotNull
    }

    @Test
    fun shouldNotStartGameIfNotExists() {
        //given
        val (_, userToken) = testCases.createUserAndLogin()

        //when
        val response = client.exchange(
            RequestEntity.post(START_GAME_ENDPOINT, "notExistingGame").header(AUTHORIZATION, "Bearer $userToken").build(),
            String::class.java
        )

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun shouldKillPlayerAndWinTjeGameIfKilledAlmostAll() {
        //given
        val userTokens = testCases.createUserAndLogin(3)
        val game = testCases.crateGame(userTokens.values.first());
        testCases.joinGameAndStart(userTokens.values, game)

        //when
        val response = client.exchange(
            RequestEntity.delete(KILL_PLAYER_ENDPOINT, game.name, players.first().id)
                .header(AUTHORIZATION, "Bearer ${userTokens[impostor?.id]}").build(),
            WinDto::class.java
        )

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        //and
        assertThat(response.body.isImpostorWins).isTrue

        //and
        assertThat(response.body.isEndGame).isTrue
    }

    @Test
    fun shouldNotKillPlayerIfHeDoesNotExists() {
        //given
        val userTokens = testCases.createUserAndLogin(3)
        val game = testCases.crateGame(userTokens.values.first());
        testCases.joinGameAndStart(userTokens.values, game)

        //when
        val response = client.exchange(
            RequestEntity.delete(KILL_PLAYER_ENDPOINT, game.name, "unknownPlayer")
                .header(AUTHORIZATION, "Bearer ${userTokens[impostor?.id]}")
                .build(),
            WinDto::class.java
        )

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun shouldNotKillPlayerIfGameDoesNotExist() {
        //given
        val userTokens = testCases.createUserAndLogin(3)
        val game = testCases.crateGame(userTokens.values.first());
        testCases.joinGameAndStart(userTokens.values, game)

        //when
        val response = client.exchange(
            RequestEntity.delete(KILL_PLAYER_ENDPOINT, "unknownGame", players.first().id)
                .header(AUTHORIZATION, "Bearer ${userTokens[impostor?.id]}")
                .build(),
            String::class.java
        )

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun shouldNotKillPlayerIfPlayerIsNotInTheGame() {
        //given
        val userTokens = testCases.createUserAndLogin(3)
        val game = testCases.crateGame(userTokens.values.first());
        testCases.joinGameAndStart(userTokens.values, game)
        val (someOtherUerId, _) = testCases.createUserAndLogin()

        //when
        val response = client.exchange(
            RequestEntity.delete(KILL_PLAYER_ENDPOINT, game.name, someOtherUerId)
                .header(AUTHORIZATION, "Bearer ${userTokens[impostor?.id]}")
                .build(),
            WinDto::class.java
        )

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun shouldNotKillPlayerIfPlayerIsNotTheImpostor() {
        //given
        val userTokens = testCases.createUserAndLogin(3)
        val game = testCases.crateGame(userTokens.values.first());
        testCases.joinGameAndStart(userTokens.values, game)

        //when
        val response = client.exchange(
            RequestEntity.delete(KILL_PLAYER_ENDPOINT, game.name, players.first().id)
                .header(AUTHORIZATION, "Bearer ${userTokens[players.last().id]}").build(),
            WinDto::class.java
        )

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun playerShouldBeAbleToCallEmergencyMeeting() {
        //given
        val userTokens = testCases.createUserAndLogin(3)
        val game = testCases.crateGame(userTokens.values.first());
        testCases.joinGameAndStart(userTokens.values, game)

        //when
        val response = client.exchange(
            RequestEntity.post(EMERGENCY_MEETING_ENDPOINT, game.name)
                .header(AUTHORIZATION, "Bearer ${userTokens[players.last().id]}").build(),
            String::class.java
        )

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun impostorShouldNotBeAbleToCallEmergencyMeeting() {
        //given
        val userTokens = testCases.createUserAndLogin(3)
        val game = testCases.crateGame(userTokens.values.first());
        testCases.joinGameAndStart(userTokens.values, game)

        //when
        val response = client.exchange(
            RequestEntity.post(EMERGENCY_MEETING_ENDPOINT, game.name)
                .header(AUTHORIZATION, "Bearer ${userTokens[impostor?.id]}").build(),
            String::class.java
        )

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun meetingShouldNotBeStartIfGameIsNotExists() {
        //given
        val userTokens = testCases.createUserAndLogin(3)
        val game = testCases.crateGame(userTokens.values.first());
        testCases.joinGameAndStart(userTokens.values, game)

        //when
        val response = client.exchange(
            RequestEntity.post(EMERGENCY_MEETING_ENDPOINT, "unknownGame")
                .header(AUTHORIZATION, "Bearer ${userTokens[impostor?.id]}").build(),
            String::class.java
        )

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
    }

    @Test
    fun meetingShouldNotBeStartIfPlayerIsNotInTheGame() {
        //given
        val userTokens = testCases.createUserAndLogin(3)
        val game = testCases.crateGame(userTokens.values.first());
        testCases.joinGameAndStart(userTokens.values, game)
        val (_, otherUserToken) = testCases.createUserAndLogin();
        //when
        val response = client.exchange(
            RequestEntity.post(EMERGENCY_MEETING_ENDPOINT, game.name)
                .header(AUTHORIZATION, "Bearer $otherUserToken").build(),
            String::class.java
        )

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun meetingShouldNotBeStartIfMeetingOnGoing() {
        //given
        val userTokens = testCases.createUserAndLogin(3)
        val game = testCases.crateGame(userTokens.values.first());
        testCases.joinGameAndStart(userTokens.values, game)
        testCases.startMeeting(game, userTokens[players.first().id]!!)

        //when
        val response = client.exchange(
            RequestEntity.post(EMERGENCY_MEETING_ENDPOINT, game.name)
                .header(AUTHORIZATION, "Bearer ${userTokens[impostor?.id]}").build(),
            String::class.java
        )

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.FORBIDDEN)
    }

    @Test
    fun userShouldBeAbleToVote() {
        //given
        val userTokens = testCases.createUserAndLogin(3)
        val game = testCases.crateGame(userTokens.values.first());
        testCases.joinGameAndStart(userTokens.values, game)
        testCases.startMeeting(game, userTokens[players.first().id]!!)

        //when
        val response = client.exchange(
            RequestEntity.post(VOTE_ENDPOINT, game.name, players.last().id)
                .header(AUTHORIZATION, "Bearer ${userTokens[players.first().id]}").build(),
            WinDto::class.java
        )

        //then
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

        //and
        assertThat(response.body.isEndGame).isFalse
        assertThat(response.body.isImpostorWins).isFalse
    }

    @Test
    fun whenUsersVoteForCrewMemberAnd2PlayerLastsImpostorWins() {
        //given
        val userTokens = testCases.createUserAndLogin(3)
        val game = testCases.crateGame(userTokens.values.first());
        testCases.joinGameAndStart(userTokens.values, game)
        testCases.startMeeting(game, userTokens[players.first().id]!!)
        testCases.vote(game, userTokens[players.first().id] !!, players.first().id)
        testCases.vote(game, userTokens[players.last().id] !!, players.first().id)

        //when
        val winDto = testCases.vote(game, userTokens[impostor?.id] !!, players.first().id)

        //then
        assertThat(winDto.isEndGame).isTrue
        assertThat(winDto.isImpostorWins).isTrue
    }

    @Test
    fun whenUsersVoteForImpostorMemberAnd2PlayerLastsImpostorLose() {
        //given
        val userTokens = testCases.createUserAndLogin(3)
        val game = testCases.crateGame(userTokens.values.first());
        testCases.joinGameAndStart(userTokens.values, game)
        testCases.startMeeting(game, userTokens[players.first().id]!!)
        testCases.vote(game, userTokens[players.first().id] !!, impostor?.id !!)
        testCases.vote(game, userTokens[players.last().id] !!, impostor?.id !!)

        //when
        val winDto = testCases.vote(game, userTokens[impostor?.id] !!, impostor?.id !!)

        //then
        assertThat(winDto.isEndGame).isTrue
        assertThat(winDto.isImpostorWins).isFalse
    }
}

@Configuration
open class Config {
    @Bean
    open fun notificationService(): NotificationService {
        return object : NotificationService {
            override fun sendNotification(player: Player?) {
                AmongUsLikeApiIntegrationTest.players.add(player!!)
            }

            override fun sendNotification(impostor: Impostor?) {
                AmongUsLikeApiIntegrationTest.impostor = impostor
            }

            override fun sendEmergencyNotification(user: User?) {
            }

        }
    }
}
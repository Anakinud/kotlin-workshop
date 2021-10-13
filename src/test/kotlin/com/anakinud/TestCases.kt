package com.anakinud

import com.anakinud.controller.ApiPaths.*
import com.anakinud.controller.dto.GameDto
import com.anakinud.controller.dto.RegistrationDto
import com.anakinud.controller.dto.WinDto
import com.anakinud.entity.Player
import com.anakinud.security.Token
import org.slf4j.LoggerFactory
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import java.util.*

@Component
class TestCases(private val client: TestRestTemplate) {

    companion object {
        val log = LoggerFactory.getLogger(TestCases.javaClass)
    }

    fun register(): RegistrationDto {
        val register = RegistrationDto(UUID.randomUUID().toString(), "some-password")
        client.postForEntity(REGISTER_ENDPOINT, register, String.javaClass)
        return register;
    }

    fun createUserAndLogin(): Pair<String, String> {
        val registerData = register()
        val response = client.postForEntity(AUTH_ENDPOINT, registerData, Token::class.java)
        return Pair(registerData.id, response.body.token)
    }

    fun createUserAndLogin(n: Int = 1): Map<String, String> {
        return (1..n).associate { this.createUserAndLogin() }
    }

    fun crateGame(userToken: String): GameDto {
        val response = client.exchange(
            RequestEntity.post(GAME_ENDPOINT).header(HttpHeaders.AUTHORIZATION, "Bearer $userToken")
                .body(GameDto(UUID.randomUUID().toString())),
            GameDto::class.java
        )
        return response.body
    }

    fun joinGame(userTokens: Collection<String>, game: GameDto) {
        userTokens.forEach { joinGame(it, game) }
    }

    fun joinGameAndStart(userTokens: Collection<String>, game: GameDto) {
        userTokens.forEach { joinGame(it, game) }
        startGame(game, userTokens.first())

    }

    private fun startGame(game: GameDto, userToken: String) {
        client.exchange(
            RequestEntity.post(START_GAME_ENDPOINT, game.name).header(HttpHeaders.AUTHORIZATION, "Bearer $userToken").build(),
            String::class.java
        )
    }

    private fun joinGame(userToken: String, game: GameDto) {
        client.exchange(
            RequestEntity.post(JOIN_GAME_ENDPOINT, game.name).header(HttpHeaders.AUTHORIZATION, "Bearer $userToken").build(),
            String::class.java
        )
    }

    fun startMeeting(game: GameDto, userToken: String) {
        client.exchange(
            RequestEntity.post(EMERGENCY_MEETING_ENDPOINT, game.name)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $userToken").build(),
            String::class.java
        )
    }

    fun vote(game: GameDto, voterToken: String, electId: String): WinDto {
        return client.exchange(
            RequestEntity.post(VOTE_ENDPOINT, game.name, electId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $voterToken").build(),
            WinDto::class.java
        ).body
    }

}
package com.anakinud.controller;

import com.anakinud.controller.dto.EmergencyMeetingDto;
import com.anakinud.controller.dto.GameDto;
import com.anakinud.controller.dto.RegistrationDto;
import com.anakinud.service.UserNotifier;
import com.anakinud.controller.dto.WinDto;
import com.anakinud.entity.EmergencyMeeting;
import com.anakinud.entity.Game;
import com.anakinud.entity.User;
import com.anakinud.repository.GameRepository;
import com.anakinud.repository.UserRepository;
import com.anakinud.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Set;

import static com.anakinud.controller.ApiPaths.AUTH_ENDPOINT;
import static com.anakinud.controller.ApiPaths.EMERGENCY_MEETING_ENDPOINT;
import static com.anakinud.controller.ApiPaths.GAME_ENDPOINT;
import static com.anakinud.controller.ApiPaths.JOIN_GAME_ENDPOINT;
import static com.anakinud.controller.ApiPaths.KILL_PLAYER_ENDPOINT;
import static com.anakinud.controller.ApiPaths.REGISTER_ENDPOINT;
import static com.anakinud.controller.ApiPaths.START_GAME_ENDPOINT;
import static com.anakinud.controller.ApiPaths.VOTE_ENDPOINT;

@RestController
@Slf4j
public class AmongUsLikeApi {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final JwtService jwtService;
    private final UserNotifier userNotifier;

    public AmongUsLikeApi(UserRepository userRepository,
            GameRepository gameRepository,
            JwtService jwtService,
            UserNotifier userNotifier) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.jwtService = jwtService;
        this.userNotifier = userNotifier;
    }

    @PostMapping(REGISTER_ENDPOINT)
    public ResponseEntity register(@RequestBody RegistrationDto registerData) {
        if (userRepository.findById(registerData.getId()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        userRepository.save(new User(registerData.getId(), registerData.getPassword()));
        return ResponseEntity.ok().build();
    }

    @PostMapping(AUTH_ENDPOINT)
    public ResponseEntity auth(@RequestBody RegistrationDto authData) {
        return userRepository.findById(authData.getId())
                .map(jwtService::toToken)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping(GAME_ENDPOINT)
    public ResponseEntity createGame(@RequestBody GameDto gameDto, UsernamePasswordAuthenticationToken auth) {
        User host = (User) auth.getPrincipal();
        if (gameRepository.findByName(gameDto.getName()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        Game game = gameDto.toGame();
        game.addPlayer(host);
        gameRepository.save(game);
        return ResponseEntity.status(HttpStatus.CREATED).body(new GameDto(game.getName()));
    }

    @PostMapping(JOIN_GAME_ENDPOINT)
    public ResponseEntity joinGame(@PathVariable String gameName, UsernamePasswordAuthenticationToken auth) {
        User host = (User) auth.getPrincipal();
        return gameRepository.findByName(gameName)
                .map(game -> {
                    log.info("Adding player to game {}", gameName);
                    game.addPlayer(host);
                    gameRepository.save(game);
                    return game;
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(START_GAME_ENDPOINT)
    public ResponseEntity startGame(@PathVariable String gameName, UsernamePasswordAuthenticationToken auth) {
        log.info("Request to start game: {}", gameName);
        return gameRepository.findByName(gameName)
                .map(game -> {
                    Set<User> users = game.startGame();
                    gameRepository.save(game);
                    userNotifier.sendNotification(users);
                    log.info("Started game. Notified users");
                    return game;
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping(KILL_PLAYER_ENDPOINT)
    public ResponseEntity killPlayer(@PathVariable String gameName, @PathVariable String userId, UsernamePasswordAuthenticationToken auth) {
        User killer = (User) auth.getPrincipal();
        Optional<Game> possibleGame = gameRepository.findByName(gameName);
        Optional<User> possibleDead = userRepository.findById(userId);

        log.info("Game: {}, user: {}", possibleGame, possibleDead);
        if (possibleDead.isEmpty() || possibleGame.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Game game = possibleGame.get();
        User toKill = possibleDead.get();

        if (!game.has(toKill)) {
            return ResponseEntity.notFound().build();
        }

        if (!game.isImpostor(killer) || game.meetingInProgress()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Set<User> remainPlayers = game.kill(toKill);
        gameRepository.save(game);
        boolean impostorWins = remainPlayers.size() == 2;
        return ResponseEntity.ok(new WinDto(impostorWins, impostorWins));
    }

    @PostMapping(EMERGENCY_MEETING_ENDPOINT)
    public ResponseEntity emergency(@PathVariable String gameName, UsernamePasswordAuthenticationToken auth) {
        User crew = (User) auth.getPrincipal();
        Optional<Game> possibleGame = gameRepository.findByName(gameName);

        if (possibleGame.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Game game = possibleGame.get();

        if (game.isImpostor(crew) || game.meetingInProgress() || !game.has(crew)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        game.startEmergencyMeeting();
        userNotifier.sendEmergencyNotification(game.getPlayers());
        gameRepository.save(game);
        return ResponseEntity.ok().build();
    }

    @PostMapping(VOTE_ENDPOINT)
    public ResponseEntity vote(@PathVariable String gameName, @PathVariable String userId, UsernamePasswordAuthenticationToken auth) {
        User voter = (User) auth.getPrincipal();
        Optional<Game> possibleGame = gameRepository.findByName(gameName);
        Optional<User> possibleElect = userRepository.findById(userId);

        if (possibleGame.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Game game = possibleGame.get();
        User elect = possibleElect.get();

        if (!game.has(voter) || !game.has(elect)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Optional<User> loser = game.vote(voter, elect);
        loser.ifPresent(game::kill);
        gameRepository.save(game);
        return loser.map(l -> {
                    if (game.isImpostor(l)) {
                        return new WinDto(false, true);
                    } else if (game.getPlayers().size() == 2) {
                        return new WinDto(true, true);
                    } else {
                        return new WinDto(false, false);
                    }
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok(new WinDto(false, false)));
    }
}

package com.anakinud.repository;

import com.anakinud.entity.Game;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;

@Component
public class InMemoryGameRepository implements GameRepository {

    private final HashMap<String, Game> games = new HashMap<>();

    @Override
    public void save(Game game) {
        games.put(game.getName(), game);
    }

    @Override
    public Optional<Game> findByName(String name) {

        return Optional.ofNullable(games.get(name));
    }
}

package com.anakinud.repository;

import com.anakinud.entity.Game;

import java.util.Optional;

public interface GameRepository {
    void save(Game game);

    Optional<Game> findByName(String name);
}

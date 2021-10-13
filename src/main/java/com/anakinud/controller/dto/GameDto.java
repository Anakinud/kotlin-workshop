package com.anakinud.controller.dto;

import com.anakinud.entity.Game;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class GameDto {
    private String name;

    public Game toGame() {
        return new Game(name);
    }
}

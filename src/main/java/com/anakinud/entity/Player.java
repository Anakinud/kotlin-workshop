package com.anakinud.entity;

public class Player extends User {
    public Player(String id, String password) {
        super(id, password);
    }

    public Player(User u) {
        super(u.getId(), u.getPassword());
    }
}

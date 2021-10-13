package com.anakinud.controller;

public final class ApiPaths {
    public static final String REGISTER_ENDPOINT = "/register";
    public static final String AUTH_ENDPOINT = "/auth";
    public static final String GAME_ENDPOINT = "/game";
    public static final String JOIN_GAME_ENDPOINT = "/game/{gameName}/user";
    public static final String START_GAME_ENDPOINT = "/game/{gameName}";
    public static final String KILL_PLAYER_ENDPOINT = "/game/{gameName}/user/{userId}";
    public static final String EMERGENCY_MEETING_ENDPOINT = "/game/{gameName}/emergency";
    public static final String VOTE_ENDPOINT = "/game/{gameName}/emergency/{userId}";
}

package com.anakinud.entity;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Optional.empty;

@Getter
public class Game {
    private final static Random RND = new Random();

    private final String name;

    private Set<User> players = new HashSet<>();

    private Optional<EmergencyMeeting> meeting = empty();

    private Impostor impostor;

    public Game(String name) {
        this.name = name;
    }

    public void addPlayer(User player) {
        players.add(player);
    }

    public Set<User> startGame() {
        List<User> crew = new ArrayList<>(players);
        User impostor = crew.get(RND.nextInt(players.size()));
        this.impostor = impostor.toImpostor();
        players.remove(impostor);
        this.players = players.stream().map(u -> new Player(u)).collect(Collectors.toSet());
        players.add(this.impostor);
        return this.players;
    }

    public boolean has(User user) {
        return players.contains(user);
    }

    public boolean isImpostor(User killer) {
        return this.impostor.equals(killer);
    }

    public Set<User> kill(User toKill) {
        this.players.remove(toKill);
        return this.players;
    }

    public EmergencyMeeting startEmergencyMeeting() {
        EmergencyMeeting newMeeting = new EmergencyMeeting(this.players);
        this.meeting = Optional.of(newMeeting);
        return newMeeting;
    }

    public boolean meetingInProgress() {
        return meeting.isPresent();
    }

    public Optional<User> vote(User voter, User elect) {
        Optional<User> loser = this.meeting.flatMap(meeting -> meeting.vote(voter, elect));
        if (loser.isPresent()) {
            meeting = empty();
        }
        return loser;
    }
}

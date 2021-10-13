package com.anakinud.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Collections.reverseOrder;

public class EmergencyMeeting {

    private Set<User> voters;
    private Map<User, Integer> votes = new HashMap<>();

    public EmergencyMeeting(Set<User> players) {
        this.voters = new HashSet<>(players);
    }

    public Optional<User> vote(User voter, User elect) {
        if (voters.remove(voter)) {
            votes.put(elect, votes.getOrDefault(elect, 0) + 1);
        }
        if (voters.isEmpty()) {
            return votes.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(reverseOrder()))
                    .findFirst().map(Map.Entry::getKey);
        }
        return Optional.empty();
    }
}

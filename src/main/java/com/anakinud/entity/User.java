package com.anakinud.entity;

import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
public class User {
    private final String id;

    private final String password;

    public User(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public Impostor toImpostor() {
        return new Impostor(this.id, this.password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || !(o instanceof User))
            return false;

        User user = (User) o;

        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

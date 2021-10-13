package com.anakinud.entity;

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class EmergencyMeetingTest {

    @Test
    fun shouldReturnMostVotedPlayer() {
        //given
        val users = listOf(User("1", "somePassword"), User("2", "somePassword"), User("3", "somePassword"))
        val meeting = EmergencyMeeting(users.toSet())

        //when
        meeting.vote(users[0], users[2])
        meeting.vote(users[1], users[2])
        val loser = meeting.vote(users[2], users[0])

        //then
        assertThat(loser.isPresent).isTrue;

        //and
        assertThat(loser.get()).isEqualTo(users[2])
    }
}
package first;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaRecord {

    //JDK16
    @Test
    public void JavaRecordHasSome() {
        var player = new Character("SuperSayan", 13, 5, 6);
        player.nickname();
        System.out.println(player);
        assertThat(player.equals(new Character("SuperSayan", 13, 5, 6))).isTrue();
        assertThat(player.hashCode() == new Character("SuperSayan", 13, 5, 6).hashCode()).isTrue();
    }
}

record Character(String nickname, Integer magicLvl, Integer axeLvl, Integer shieldLvl) {
}

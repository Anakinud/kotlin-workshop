package first

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DataClassKotlin {

    @Test
    fun `Data class generates for you equals`() {
        val player = Player("SuperSayan", 12, 5, 3)

        assertThat(player == (Player("SuperSayan", 12, 5, 3))).isTrue
    }

    @Test
    fun `Data class generates for you hashCode`() {
        val player = Player("SuperSayan", 12, 5, 3)

        assertThat(player.hashCode()).isEqualTo(Player("SuperSayan", 12, 5, 3).hashCode())
    }

    @Test
    fun `Data class generates for you copy`() {
        val player = Player("SuperSayan", 12, 5, 3)
        val playerCopy = player.copy(magicLvl = 13)
        assertThat(playerCopy === player).isFalse
        assertThat(playerCopy == player).isFalse
    }

}

data class Player(val nickname: String, val magicLvl: Int, val axeLvl: Int, val shieldLvl: Int)
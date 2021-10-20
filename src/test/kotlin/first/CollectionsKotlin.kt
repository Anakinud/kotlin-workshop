package first

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CollectionsKotlin {

    @Test
    fun `Compiler saves you surprises`() {
        //given
        val immutableList: List<Int> = listOf(1, 2, 3, 4)
        // immutableList.add(12) You can't do this, compiler saves you
    }

    @Test
    fun `Compiler saves you surprises 2`() {
        //given
        val mutableList: MutableList<Int> = mutableListOf(1, 2, 3, 4)
        mutableList.add(12)

        //
        assertThat(mutableList).containsExactly(1, 2, 3, 4, 12)
    }

    @Test
    fun `Kotlin saves you from writing boilerplate code`() {
        //given
        val list: List<Int> = mutableListOf(1, 2, 3, 4)
        val plus2List = list.map { it + 2 }

        //
        assertThat(plus2List).containsExactly(3, 4, 5, 6)
    }

    @Test
    fun `Kotlin saves you from writing boilerplate code Example2`() {
        //given
        val list: List<Int> = mutableListOf(1, 2, 3, 4)
        val evenList = list.filter { it % 2 == 0 }

        //
        assertThat(evenList).containsExactly(2, 4)
    }

    @Test
    fun `Kotlin saves you from writing boilerplate code but it's not stream!!!`() {
        //given
        val list: MutableList<Int> = mutableListOf(1, 2, 3, 4)
        val plus2List = list.map { it + 2 }
        list.add(12)
        //
        assertThat(plus2List.toList()).containsExactly(3, 4, 5, 6)
    }

    @Test
    fun `Kotlin stream equivalent`() {
        //given
        val list: MutableList<Int> = mutableListOf(1, 2, 3, 4)
        val plus2List = list.asSequence().map { it + 2 }
        list.add(12)

        //
        assertThat(plus2List.toList()).containsExactly(3, 4, 5, 6, 14)
    }

    @Test
    fun `Kotlin maps handy tricks`() {
        //given
        val pairList = listOf(Pair("1", 1), Pair("2", 2))


        //expect
        assertThat(pairList.toMap()).containsExactlyEntriesOf(mapOf(Pair("1", 1), Pair("2", 2)))
    }
}
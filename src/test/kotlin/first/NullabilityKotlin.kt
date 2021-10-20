package first

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.Test

class NullabilityKotlin {

    @Test
    fun `Lack Of NullPointerException Out Of The Box`() {
        //given
        //val firstnameWillNotWork: String = null
        val firstname: String = "Text, because i can't assign null"

    }

    @Test
    fun `Kotlin Optional alternative`() {
        //given
        val nullableString: String? = null

        //when
        assertThatCode { nullableString!! }.isInstanceOf(NullPointerException::class.java)
    }

    //https://typealias.com/guides/java-optionals-and-kotlin-nulls/#:~:text=map(),in%20Kotlin%20is%20let()%20.
    @Test
    fun `Kotlin map() alternative`() {
        //given
        val firstname: String? = null
        val upperCaseFirstname: String = firstname?.uppercase() ?: ""

        //when
        assertThat(upperCaseFirstname).isEqualTo("")
    }

    @Test
    fun `Kotlin flatMap() alternative`() {
        //given
        val businessEntity: ExampleKClass? = ExampleKClass()

        //when
        val result: String? = businessEntity?.let { it.emptyField }

        //then
        assertThat(result).isNull()
    }
}

internal class ExampleKClass {
    val emptyField: String? = null
}
package first

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LambdaKotlin {

    @Test
    fun `Lambda To Variable`() {
        val lambdaVariable = { s: String -> s.lowercase() }

        assertThat(lambdaVariable("BIG TEXT")).isEqualTo("big text")
    }

    @Test
    fun `Lambda To Variable Other Example`() {
        val lambdaVariable: (String) -> String = { it.lowercase() }

        assertThat(lambdaVariable("BIG TEXT")).isEqualTo("big text")
    }

    @Test
    fun `Ignore unnecessary argument`() {
        val lambdaVariable: (String, String) -> String = { _, s -> s.lowercase() }

        assertThat(lambdaVariable("IGNORED BIG TEXT", "BIG TEXT")).isEqualTo("big text")
    }
}
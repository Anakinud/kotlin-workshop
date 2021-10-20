package first

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity

class ExtensionMethods {
    @Test
    fun `Built in kotlin extension function`() {
        //given
        val firstnameUpper = "John".let { it.uppercase() }

        //expect
        assertThat(firstnameUpper).isEqualTo("JOHN")
    }

    @Test
    fun `Using own extension functions`() {
        assertThat("john.wick".toGmail()).isEqualTo("john.wick@gmail.com")
    }
}

private fun String.toGmail(): String {
    return "$this@gmail.com"
}

private fun <T> T?.toResponse(): ResponseEntity<Any> {
    return this?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
}

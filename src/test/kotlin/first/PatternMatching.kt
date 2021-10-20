package first

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SoCalledPatternMatching {

    @Test
    fun `When like switch case`() {
        val patternMatchingResult = when (3) {
            1 -> "1"
            3 -> "3"
            else -> ""
        }

        assertThat(patternMatchingResult).isEqualTo("3")
    }

    @Test
    fun `When as pattern matching`() {
        val user: User = Admin()
        val patternMatchingResult = when (user) {
            is Admin -> "Admin Stuff"
            is Customer -> "Customer Stuff"
        }

        assertThat(patternMatchingResult).isEqualTo("Admin Stuff")
    }

    @Test
    fun `When as if-else-if`() {
        val someone = Person(34);
        val patternMatchingResult = when {
            someone.age > 63 -> "retiree"
            someone.age in 19..62 -> "adult"
            else -> "child"
        }

        assertThat(patternMatchingResult).isEqualTo("adult")
    }
}

class Person(val age: Int) {
}

abstract sealed class User

class Admin : User()

class Customer : User()

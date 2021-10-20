package first;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class NullabilityJava {

    @Test
    public void optionalAndNpeReplacement() {
        //given
        Optional<String> someNullableString = Optional.empty();

        //expect
        assertThatCode(() -> someNullableString.get()).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void optionalSafeMappingBasic() {
        //given
        Optional<String> emptyString = Optional.empty();

        //when
        String result = emptyString.map(s -> "Hello" + s).orElse("");

        //then
        assertThat(result).isEqualTo("");
    }

    @Test
    public void mysteriousMonads() {
        //given
        Optional<ExampleJClass> someEntity = Optional.of(new ExampleJClass());

        //when
        Optional<String> result = someEntity.flatMap(e -> e.getEmptyField());

        //then
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void optionalPossibleProblem() {
        //given
        Optional<ExampleJClass> someEntity = null;

        //expect
        assertThatCode(() -> someEntity.isPresent()).isInstanceOf(NullPointerException.class);
    }
}

class ExampleJClass {
    private String emptyField = null;

    public Optional<String> getEmptyField() {
        return Optional.ofNullable(emptyField);
    }
}
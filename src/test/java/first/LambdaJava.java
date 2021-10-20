package first;

import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class LambdaJava {

    @Test
    public void assignLambdaToVariable() {
        Function<String, String> variableLambda = s -> s.toLowerCase();

        assertThat(variableLambda.apply("BIG TEXT")).isEqualTo("big text");
    }


}

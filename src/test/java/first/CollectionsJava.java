package first;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class CollectionsJava {

    @Test
    public void sadSurprisesWithImmutabilityJdk8() {
        //given
        List<Integer> unknownTypeOfList = Collections.unmodifiableList(Arrays.asList(1, 2, 3, 4));

        //expect
        assertThatCode(() -> unknownTypeOfList.add(12)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void sadSurprisesWithImmutabilityJdk9() {
        //given
        List<Integer> unknownTypeOfList = List.of(1, 2, 3, 4);

        //expect
        assertThatCode(() -> unknownTypeOfList.add(12)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void streams() {
        //given
        List<Integer> someList = new ArrayList<>(List.of(1, 2, 3, 4));

        //when
        Stream<Integer> plus2Stream = someList.stream().map(i -> i + 2);

        //and
        someList.add(12);

        //then
        assertThat(plus2Stream.collect(Collectors.toList())).containsExactly(3, 4, 5, 6, 14);
    }

    @Test
    public void streamMaps() {
        //given
        Map<String, Integer> map = Map.of("first", 1, "second", 2);

        //expect
        map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

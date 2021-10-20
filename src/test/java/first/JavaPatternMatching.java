package first;

import org.junit.jupiter.api.Test;

public class JavaPatternMatching {

    //JDK16
    @Test
    public void patternMatching() {
        Object someObject = "Example";

        if (someObject instanceof String knownTypeObject) {
            //Do something with this
        }
    }
}

abstract sealed class Root {
    final class A extends Root {
    }

    final class B extends Root {
    }

    final class C extends Root {
    }
}

abstract sealed class RootPrim permits APrim, BPrim{
}

final class APrim extends RootPrim {
}

final class BPrim extends RootPrim {
}

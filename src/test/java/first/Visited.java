package first;

import org.springframework.http.ResponseEntity;

public interface Visited {
    ResponseEntity accept(Visitor v);
}

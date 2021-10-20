package first;

import org.springframework.http.ResponseEntity;

public interface Visitor {
    ResponseEntity visit(NotFound notFound);

    ResponseEntity visit(NoAccess notFound);
}

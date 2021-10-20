package first;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseEntityCreatorVisitor implements Visitor {
    @Override
    public ResponseEntity visit(NotFound notFound) {
        return ResponseEntity.notFound().build();
    }

    @Override public ResponseEntity visit(NoAccess notFound) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}

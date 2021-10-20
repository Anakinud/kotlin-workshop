package first;

import org.springframework.http.ResponseEntity;

public class NoAccess implements Visited{
    @Override
    public ResponseEntity accept(Visitor v) {
        return v.visit(this);
    }
}

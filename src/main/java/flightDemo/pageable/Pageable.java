package flightDemo.pageable;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.Max;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RegisterForReflection
public class Pageable {

    public static final Pageable unpaged = new Pageable(false);

    @QueryParam("page")
    @DefaultValue("0")
    private int page;

    @Max(100)
    @QueryParam("size")
    @DefaultValue("10")
    private int size;

    @QueryParam("paged")
    @DefaultValue("true")
    private boolean paged;

    public Pageable() {
    }

    private Pageable(boolean paged) {
        this.paged = paged;
    }

    public Pageable(int page, int size) {
        this.paged = true;
        this.page = page;
        this.size = size;
    }

    public static Pageable of(int page, int size) {
        return new Pageable(page, size);
    }

    public Pageable next() {
        return new Pageable(page + 1, size);
    }


}

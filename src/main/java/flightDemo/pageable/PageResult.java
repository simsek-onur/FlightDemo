package flightDemo.pageable;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

@RegisterForReflection
public record PageResult<T>(long currentPage, long pageSize, long totalCount, long totalPages, Collection<T> content) {

    public static <E> PageResult<E> of(PanacheQuery<E> query, Pageable pageable) {
        return of(query.list(), query.count(), pageable);
    }

    public static <E> PageResult<E> of(Collection<E> data, long count, Pageable pageable) {
        var totalPages = pageable.isPaged() ? (int) Math.ceil((double) count / pageable.getSize()) : 1;
        var pageSize = pageable.isPaged() ? pageable.getSize() : count;
        return new PageResult<>(pageable.getPage(), pageSize, count, totalPages, data);
    }

    @JsonProperty("hasNext")
    public boolean hasNext() {
        return currentPage() + 1 < totalPages();
    }

    public <U> PageResult<U> map(Function<T, U> mapper) {
        Objects.requireNonNull(mapper);
        var newData = content.stream().map(mapper).toList();
        return new PageResult<>(currentPage, pageSize, totalCount, totalPages, newData);
    }

}

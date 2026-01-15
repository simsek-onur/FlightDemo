package flightDemo.repository;

import flightDemo.pageable.PageResult;
import flightDemo.pageable.Pageable;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Page;

public interface Repository<Entity, Id> extends PanacheRepositoryBase<Entity, Id> {

    default<T> PageResult<T> createPageQuery(PanacheQuery<T> query, Pageable pageable) {
        if (pageable.isPaged()) {
            query = query.page(toPage(pageable));
        }
        return PageResult.of(query, pageable);
    }

    static Page toPage(Pageable pageable) {
        return Page.of(pageable.getPage(), pageable.getSize());
    }

    default void merge(Entity entity) {
        getEntityManager().merge(entity);
    }

    default void mergeAndFlush(Entity entity) {
        getEntityManager().merge(entity);
        getEntityManager().flush();
    }
}

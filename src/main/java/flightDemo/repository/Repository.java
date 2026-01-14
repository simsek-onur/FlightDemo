package flightDemo.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;

public interface Repository<Entity, Id> extends PanacheRepositoryBase<Entity, Id> {

    default void merge(Entity entity) {
        getEntityManager().merge(entity);
    }

    default void mergeAndFlush(Entity entity) {
        getEntityManager().merge(entity);
        getEntityManager().flush();
    }
}

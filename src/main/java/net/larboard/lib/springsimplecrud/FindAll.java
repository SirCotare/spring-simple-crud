package net.larboard.lib.springsimplecrud;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class FindAll<T extends CrudModel<ID>, ID> {
    private final SimpleCrudManager simpleCrudManager;
    private final Class<T> modelClazz;

    private Class<T> getModelClazz() {
        return modelClazz;
    }

    private Consumer<List<T>> afterFetchExecutor = (instance) -> {
        // do nothing
    };

    private Consumer<Throwable> onErrorExecutor = (throwable) -> {
        throw new RuntimeException("Failed to fetch all " + getModelClazz().getSimpleName() + ": " + throwable.getMessage(), throwable);
    };

    @Transactional
    @org.springframework.lang.NonNull
    public List<T> retrieve() {
        try {
            var criteriaBuilder = simpleCrudManager.entityManager.getCriteriaBuilder();
            var criteriaQuery = criteriaBuilder.createQuery(modelClazz);

            var root = criteriaQuery.from(modelClazz);
            criteriaQuery.select(root);

            var instances = simpleCrudManager.entityManager.createQuery(criteriaQuery).getResultList();

            afterFetchExecutor.accept(instances);

            return instances;
        } catch (Exception e) {
            onErrorExecutor.accept(e);
            if (e instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }

            throw new RuntimeException(e);
        }
    }

    @org.springframework.lang.NonNull
    public FindAll<T, ID> afterFetch(@NonNull Consumer<List<T>> executor) {
        afterFetchExecutor = executor;
        return this;
    }

    @org.springframework.lang.NonNull
    public FindAll<T, ID> onError(@NonNull Consumer<Throwable> executor) {
        onErrorExecutor = executor;
        return this;
    }
}

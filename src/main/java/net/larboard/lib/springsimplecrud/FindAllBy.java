package net.larboard.lib.springsimplecrud;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class FindAllBy<T extends CrudModel<ID>, ID> {
    private final String propertyName;
    private final Object value;
    private final SimpleCrudManager simpleCrudManager;
    private final Class<T> modelClazz;

    private Class<T> getModelClazz() {
        return modelClazz;
    }

    private Consumer<List<T>> afterFetchExecutor = (instance) -> {
        // do nothing
    };

    private TriConsumer<Throwable, String, Object> onErrorExecutor = (throwable, propertyName, value) -> {
        throw new RuntimeException("Failed to fetch " + getModelClazz().getSimpleName() + " (propertyName: " + propertyName + ", value: " + value + "): " + throwable.getMessage(), throwable);
    };

    @Transactional
    @org.springframework.lang.NonNull
    public List<T> retrieve() {
        try {
            var cb = simpleCrudManager.entityManager.getCriteriaBuilder();
            var cq = cb.createQuery(modelClazz);
            var root = cq.from(modelClazz);

            var condition = cb.equal(root.get(propertyName), value);
            cq.where(condition);

            var resultList = simpleCrudManager.entityManager.createQuery(cq).getResultList();

            afterFetchExecutor.accept(resultList);

            if (resultList == null) {
                return Collections.emptyList();
            }
            return resultList;
        } catch (Exception e) {
            onErrorExecutor.accept(e, propertyName, value);
            throw new NoSuchElementException();
        }
    }

    @org.springframework.lang.NonNull
    public FindAllBy<T, ID> afterFetch(@NonNull Consumer<List<T>> executor) {
        afterFetchExecutor = executor;
        return this;
    }

    @org.springframework.lang.NonNull
    public FindAllBy<T, ID> onError(@NonNull TriConsumer<Throwable, String, Object> executor) {
        onErrorExecutor = executor;
        return this;
    }
}

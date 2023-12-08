package net.larboard.lib.springsimplecrud;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Update<T extends CrudModel<ID>, ID> {
    private final ID id;
    private final CrudParameter<T> parameter;
    private final SimpleCrudManager simpleCrudManager;
    private final Class<T> modelClazz;

    private Class<T> getModelClazz() {
        return modelClazz;
    }

    private Consumer<T> afterFetchExecutor = (instance) -> {
        // do nothing
    };
    private Consumer<T> beforePersistExecutor = (instance) -> {
        // do nothing
    };
    private Consumer<T> afterPersistExecutor = (instance) -> {
        // do nothing
    };
    private TriConsumer<Throwable, ID, CrudParameter<T>> onErrorExecutor = (throwable, id, parameter) -> {
        throw new RuntimeException("Failed to update " + getModelClazz().getSimpleName() + " (ID: " + id + ") with parameter " + parameter.toString() + ": " + throwable.getMessage(), throwable);
    };

    @Transactional
    @org.springframework.lang.NonNull
    public T commit() {
        try {
            var instance = simpleCrudManager.entityManager.find(modelClazz, id);

            afterFetchExecutor.accept(instance);

            BeanUtils.copyProperties(parameter, instance);

            beforePersistExecutor.accept(instance);

            simpleCrudManager.entityManager.persist(instance);

            afterPersistExecutor.accept(instance);

            simpleCrudManager.publishEvent(
                    new CrudUpdateEvent(this, modelClazz, instance.getId())
            );

            return instance;
        } catch (Exception e) {
            onErrorExecutor.accept(e, id, parameter);
            if (e instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }

            throw new RuntimeException(e);
        }
    }

    @org.springframework.lang.NonNull
    public Update<T, ID> afterFetch(@NonNull Consumer<T> executor) {
        afterFetchExecutor = executor;
        return this;
    }

    @org.springframework.lang.NonNull
    public Update<T, ID> beforePersist(@NonNull Consumer<T> executor) {
        beforePersistExecutor = executor;
        return this;
    }

    @org.springframework.lang.NonNull
    public Update<T, ID> afterPersist(@NonNull Consumer<T> executor) {
        afterPersistExecutor = executor;
        return this;
    }

    @org.springframework.lang.NonNull
    public Update<T, ID> onError(@NonNull TriConsumer<Throwable, ID, CrudParameter<T>> executor) {
        onErrorExecutor = executor;
        return this;
    }
}

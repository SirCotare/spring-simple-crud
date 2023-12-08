package net.larboard.lib.springsimplecrud;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Delete<T extends CrudModel<ID>, ID> {
    private final ID id;
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
    private BiConsumer<Throwable, ID> onErrorExecutor = (throwable, id) -> {
        throw new RuntimeException("Failed to delete " + getModelClazz().getSimpleName() + " (ID: " + id + "): " + throwable.getMessage(), throwable);
    };

    @Transactional
    @org.springframework.lang.NonNull
    public void commit() {
        try {
            var instance = simpleCrudManager.entityManager.find(modelClazz, id);

            afterFetchExecutor.accept(instance);

            if (instance == null) {
                return;
            }

            beforePersistExecutor.accept(instance);

            simpleCrudManager.entityManager.remove(instance);

            afterPersistExecutor.accept(instance);

            simpleCrudManager.publishEvent(
                    new CrudDeleteEvent(this, modelClazz, id)
            );
        } catch (Exception e) {
            onErrorExecutor.accept(e, id);
            if (e instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }

            throw new RuntimeException(e);
        }
    }

    @org.springframework.lang.NonNull
    public Delete<T, ID> afterFetch(@NonNull Consumer<T> executor) {
        afterFetchExecutor = executor;
        return this;
    }

    @org.springframework.lang.NonNull
    public Delete<T, ID> beforePersist(@NonNull Consumer<T> executor) {
        beforePersistExecutor = executor;
        return this;
    }

    @org.springframework.lang.NonNull
    public Delete<T, ID> afterPersist(@NonNull Consumer<T> executor) {
        afterPersistExecutor = executor;
        return this;
    }

    @org.springframework.lang.NonNull
    public Delete<T, ID> onError(@NonNull BiConsumer<Throwable, ID> executor) {
        onErrorExecutor = executor;
        return this;
    }
}

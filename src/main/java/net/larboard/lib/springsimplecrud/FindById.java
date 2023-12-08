package net.larboard.lib.springsimplecrud;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class FindById<T extends CrudModel<ID>, ID> {
    private final ID id;
    private final SimpleCrudManager simpleCrudManager;
    private final Class<T> modelClazz;

    private Class<T> getModelClazz() {
        return modelClazz;
    }

    private Consumer<T> afterFetchExecutor = (instance) -> {
        // do nothing
    };

    private BiConsumer<Throwable, ID> onErrorExecutor = (throwable, id) -> {
        throw new RuntimeException("Failed to fetch " + getModelClazz().getSimpleName() + " (ID: " + id + "): " + throwable.getMessage(), throwable);
    };

    @Transactional
    @org.springframework.lang.NonNull
    public Optional<T> retrieve() {
        try {
            var instance = simpleCrudManager.entityManager.find(modelClazz, id);

            afterFetchExecutor.accept(instance);

            if (instance == null) {
                return Optional.empty();
            }

            return Optional.of(instance);
        } catch (Exception e) {
            onErrorExecutor.accept(e, id);
            throw new NoSuchElementException();
        }
    }

    @org.springframework.lang.NonNull
    public FindById<T, ID> afterFetch(@NonNull Consumer<T> executor) {
        afterFetchExecutor = executor;
        return this;
    }

    @org.springframework.lang.NonNull
    public FindById<T, ID> onError(@NonNull BiConsumer<Throwable, ID> executor) {
        onErrorExecutor = executor;
        return this;
    }
}

package net.larboard.lib.springsimplecrud;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Create<T extends CrudModel<ID>, ID> {
    private final CrudParameter<T> parameter;
    private final SimpleCrudManager simpleCrudManager;
    private final Class<T> modelClazz;

    private Class<T> getModelClazz() {
        return modelClazz;
    }

    private Consumer<T> beforePersistExecutor = (instance) -> {
        // do nothing
    };
    private Consumer<T> afterPersistExecutor = (instance) -> {
        // do nothing
    };
    private BiConsumer<Throwable, CrudParameter<T>> onErrorExecutor = (throwable, parameter) -> {
        throw new RuntimeException("Failed to create " + getModelClazz().getSimpleName() + " with parameter " + parameter.toString() + ": " + throwable.getMessage(), throwable);
    };

    @Transactional
    @org.springframework.lang.NonNull
    public T commit() {
        try {
            var instance = modelClazz.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(parameter, instance);

            beforePersistExecutor.accept(instance);

            simpleCrudManager.entityManager.persist(instance);

            afterPersistExecutor.accept(instance);

            simpleCrudManager.publishEvent(
                    new CrudCreateEvent(this, modelClazz, instance.getId())
            );

            return instance;
        } catch (Exception e) {
            onErrorExecutor.accept(e, parameter);

            if (e instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }

            throw new RuntimeException(e);
        }
    }

    @org.springframework.lang.NonNull
    public Create<T, ID> beforePersist(@NonNull Consumer<T> executor) {
        beforePersistExecutor = executor;
        return this;
    }

    @org.springframework.lang.NonNull
    public Create<T, ID> afterPersist(@NonNull Consumer<T> executor) {
        afterPersistExecutor = executor;
        return this;
    }

    @org.springframework.lang.NonNull
    public Create<T, ID> onError(@NonNull BiConsumer<Throwable, CrudParameter<T>> executor) {
        onErrorExecutor = executor;
        return this;
    }
}

package net.larboard.lib.springsimplecrud;

import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

@Slf4j
public class SimpleCrudManager {
    final EntityManager entityManager;
    private final ApplicationEventPublisher applicationEventPublisher;
    private static volatile SimpleCrudManager instance;

    private SimpleCrudManager(EntityManager entityManager, ApplicationEventPublisher applicationEventPublisher) {
        this.entityManager = entityManager;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public static SimpleCrudManager getInstance(@NonNull EntityManager entityManager, @NonNull ApplicationEventPublisher applicationEventPublisher) {
        if (instance == null) {
            synchronized (SimpleCrudManager.class) {
                if (instance == null) {
                    instance = new SimpleCrudManager(entityManager, applicationEventPublisher);
                }
            }
        }
        return instance;
    }

    public static SimpleCrudManager getInstance(@NonNull EntityManager entityManager) {
        if (instance == null) {
            synchronized (SimpleCrudManager.class) {
                if (instance == null) {
                    instance = new SimpleCrudManager(entityManager, null);
                }
            }
        }
        return instance;
    }

    public static <T extends CrudModel<ID>, ID> SimpleCrud<T, ID> simpleCrud(@NonNull Class<T> modelClazz) {
        if (instance == null) {
            throw new IllegalStateException("no instance");
        }
        return new SimpleCrud<T, ID>(instance, modelClazz);
    }

    void publishEvent(ApplicationEvent event) {
        if (applicationEventPublisher == null) {
            return;
        }

        applicationEventPublisher.publishEvent(
                event
        );
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SimpleCrud<T extends CrudModel<ID>, ID> {
        private final SimpleCrudManager simpleCrudManager;
        private final Class<T> modelClazz;

        @org.springframework.lang.NonNull
        public <P extends CrudParameter<T>> Create<T, ID> create(@NonNull P parameter) {
            return new Create<>(parameter, simpleCrudManager, modelClazz);
        }

        @org.springframework.lang.NonNull
        public <P extends CrudParameter<T>> Update<T, ID> update(@NonNull ID id, @NonNull P parameter) {
            return new Update<>(id, parameter, simpleCrudManager, modelClazz);
        }

        @org.springframework.lang.NonNull
        public <P extends CrudParameter<T>> Delete<T, ID> delete(@NonNull ID id) {
            return new Delete<>(id, simpleCrudManager, modelClazz);
        }

        @org.springframework.lang.NonNull
        public <P extends CrudParameter<T>> FindById<T, ID> findById(@NonNull ID id) {
            return new FindById<>(id, simpleCrudManager, modelClazz);
        }

        @org.springframework.lang.NonNull
        public <P extends CrudParameter<T>> FindAll<T, ID> findAll() {
            return new FindAll<>(simpleCrudManager, modelClazz);
        }
    }
}

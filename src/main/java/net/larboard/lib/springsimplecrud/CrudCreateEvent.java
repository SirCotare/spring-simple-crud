package net.larboard.lib.springsimplecrud;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public final class CrudCreateEvent extends ApplicationEvent {
    private final Object entityId;
    private final Class<? extends CrudModel<?>> entityClazz;

    <ID> CrudCreateEvent(Object source, Class<? extends CrudModel<ID>> entityClazz, ID entityId) {
        super(source);
        this.entityId = entityId;
        this.entityClazz = entityClazz;
    }
}

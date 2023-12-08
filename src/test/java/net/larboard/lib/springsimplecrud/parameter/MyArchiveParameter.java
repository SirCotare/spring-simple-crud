package net.larboard.lib.springsimplecrud.parameter;

import net.larboard.lib.springsimplecrud.CrudParameter;
import net.larboard.lib.springsimplecrud.model.MyModel;

public record MyArchiveParameter(
        boolean archived
) implements CrudParameter<MyModel> {

}

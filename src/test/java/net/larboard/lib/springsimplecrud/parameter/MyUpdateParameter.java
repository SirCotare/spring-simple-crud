package net.larboard.lib.springsimplecrud.parameter;

import net.larboard.lib.springsimplecrud.CrudParameter;
import net.larboard.lib.springsimplecrud.model.MyModel;

public record MyUpdateParameter(
        String description,
        int age
) implements CrudParameter<MyModel> {

}

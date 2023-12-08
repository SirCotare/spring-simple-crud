package net.larboard.lib.springsimplecrud.parameter;

import net.larboard.lib.springsimplecrud.CrudParameter;
import net.larboard.lib.springsimplecrud.model.MyModel;

public record MyCreateParameter(
        String name,
        String description,
        int age
) implements CrudParameter<MyModel> {

}

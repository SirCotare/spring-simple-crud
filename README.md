# Spring Simple Crud

**Note: this is still WIP**

***

This utility gets rid of the architectural part of building an application
by providing a fluent API to the most basic CRUD operations.

## Maven Dependency

```xml

<dependency>
    <groupId>net.larboard.lib</groupId>
    <artifactId>spring-simple-crud</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Bean Configuration

With Spring Events

```java

@Configuration
public class BeanConfig {
    @Bean
    public SimpleCrudManager simpleCrudManager(EntityManager entityManager, ApplicationEventPublisher applicationEventPublisher) {
        return SimpleCrudManager.getInstance(entityManager, applicationEventPublisher);
    }
}
```

Without Spring Events

```java

@Configuration
public class BeanConfig {
    @Bean
    public SimpleCrudManager simpleCrudManager(EntityManager entityManager) {
        return SimpleCrudManager.getInstance(entityManager);
    }
}
```

## Usage

* Create an `@Entity` model class and have it implement `CrudModel`.
* Create a parameter class for that entity that implements `CrudParameter`.

```java

@Entity
@Getter
@Setter
public static class MyModel implements CrudModel<Long> {
    @Id
    private Long id;

    private String name;
}

public record MyParameter(
        String name
) implements CrudParameter<MyModel> {
}
```

* Use `simpleCrud`:

```java
public MyModel createEntity(MyParameter parameter) {
    return simpleCrud(MyModel.class)
            .create(parameter)
            .commit();
}
```

## Support

For support, please feel free to contact me on Discord (sircotare).

## Credits

- [All Contributors](../../contributors)

## License

The MIT License (MIT). Please see the [license file](LICENSE.md) for more information.
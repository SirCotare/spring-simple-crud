package net.larboard.lib.springsimplecrud;

import com.github.javafaker.Faker;
import jakarta.persistence.EntityManager;
import net.larboard.lib.springsimplecrud.model.MyModel;
import net.larboard.lib.springsimplecrud.parameter.MyArchiveParameter;
import net.larboard.lib.springsimplecrud.parameter.MyCreateParameter;
import net.larboard.lib.springsimplecrud.parameter.MyUpdateParameter;
import net.larboard.lib.springsimplecrud.support.WithPostgres;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static net.larboard.lib.springsimplecrud.SimpleCrudManager.simpleCrud;
import static org.assertj.core.api.Assertions.assertThat;

@WithPostgres
@SpringBootTest
class SimpleCrudTests {
    private static final Faker FAKER = new Faker();

    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    EntityManager entityManager;

    @BeforeEach
    void setupTables() {
        jdbcTemplate.execute("""
                DROP TABLE IF EXISTS public.test_entity;
                CREATE TABLE public.test_entity (
                                       id SERIAL PRIMARY KEY,
                                       name TEXT,
                                       description TEXT,
                                       age integer,
                                       archived boolean
                                       )
                """);
    }

    @Nested
    class Create {

        @Test
        @Transactional
        void shouldSucceed() {
            // given
            var parameter = new MyCreateParameter(
                    "test",
                    "lorem testum",
                    33
            );

            // when
            var result = simpleCrud(MyModel.class)
                    .create(parameter)
                    .commit();

            // then
            var expectedResult = new MyModel();
            expectedResult.setName(parameter.name());
            expectedResult.setDescription(parameter.description());
            expectedResult.setAge(parameter.age());
            expectedResult.setArchived(false);

            assertThat(result.getId()).isNotNull();
            assertThat(result).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedResult);
        }
    }

    @Nested
    class Update {

        @Test
        @Transactional
        void shouldSucceed() {
            // setup
            var item = createRandomItem();

            // given
            var parameter = new MyUpdateParameter(
                    "weheee",
                    11
            );

            // when
            var result = simpleCrud(MyModel.class)
                    .update(item.getId(), parameter)
                    .commit();

            // then
            var expectedResult = new MyModel();
            expectedResult.setId(item.getId());
            expectedResult.setName(item.getName());
            expectedResult.setDescription(parameter.description());
            expectedResult.setAge(parameter.age());
            expectedResult.setArchived(item.isArchived());

            assertThat(result.getId()).isNotNull();
            assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
        }

        @ParameterizedTest
        @ValueSource(booleans = {true, false})
        @Transactional
        void archiving_shouldSucceed(boolean newValue) {
            // setup
            var item = createRandomItem();
            item.setArchived(!newValue);
            entityManager.persist(item);

            // given
            var parameter = new MyArchiveParameter(
                    newValue
            );

            // when
            var result = simpleCrud(MyModel.class)
                    .update(item.getId(), parameter)
                    .commit();

            // then
            var expectedResult = new MyModel();
            expectedResult.setId(item.getId());
            expectedResult.setName(item.getName());
            expectedResult.setDescription(item.getDescription());
            expectedResult.setAge(item.getAge());
            expectedResult.setArchived(parameter.archived());

            assertThat(result.getId()).isNotNull();
            assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);
        }
    }

    @Nested
    class Delete {
        @Test
        @Transactional
        void existingItem_shouldSucceed() {
            // given
            var item = createRandomItem();

            // when
            simpleCrud(MyModel.class)
                    .delete(item.getId())
                    .commit();

            // then
            var result = entityManager.find(MyModel.class, item.getId());
            assertThat(result).isNull();
        }

        @Test
        @Transactional
        void notExistingItem_shouldSucceed() {
            // given
            var notExistingId = -99L;

            // when
            simpleCrud(MyModel.class)
                    .delete(notExistingId)
                    .commit();

            // then
            var result = entityManager.find(MyModel.class, notExistingId);
            assertThat(result).isNull();
        }
    }

    @Nested
    class FindById {
        @Test
        @Transactional
        void existingItem_shouldSucceed() {
            // given
            var item = createRandomItem();

            // when
            var result = simpleCrud(MyModel.class)
                    .findById(item.getId())
                    .retrieve();

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(item.getId());
        }

        @Test
        @Transactional
        void notExistingItem_shouldSucceed() {
            // given
            var notExistingId = -99L;

            // when
            var result = simpleCrud(MyModel.class)
                    .findById(notExistingId)
                    .retrieve();

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class FindAll {
        @Test
        @Transactional
        void existingItems_shouldSucceed() {
            // given
            var itemCount = 43;

            for (int i = 0; i < itemCount; i++) {
                createRandomItem();
            }

            // when
            var result = simpleCrud(MyModel.class)
                    .findAll()
                    .retrieve();

            // then
            assertThat(result).hasSize(itemCount);
        }

        @Test
        @Transactional
        void noExistingItems_shouldSucceed() {
            // given
            /* nothing */

            // when
            var result = simpleCrud(MyModel.class)
                    .findAll()
                    .retrieve();

            // then
            assertThat(result).isEmpty();
        }
    }

    private MyModel createRandomItem() {
        var item = new MyModel();
        item.setName(FAKER.pokemon().name());
        item.setDescription(FAKER.lorem().paragraph());
        item.setAge(FAKER.random().nextInt(10, 99));
        item.setArchived(FAKER.bool().bool());
        entityManager.persist(item);
        return item;
    }
}

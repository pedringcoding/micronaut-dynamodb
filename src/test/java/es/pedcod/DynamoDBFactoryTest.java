package es.pedcod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.amazonaws.services.s3.model.Region;
import java.lang.reflect.Modifier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/** Unit Test Class for {@link DynamoDBFactory} in Factory bean storage. */
@DisplayName("Factory to inject DynamoDBMapper bean in context storage")
@TestMethodOrder(MethodName.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class DynamoDBFactoryTest {

    /** The principal component to test */
    DynamoDBFactory factory;

    /** The AWS references for secret name */
    String accessKey;
    /** The AWS references for secret value */
    String secretKey;
    /** The AWS references for region resources */
    String region;

    /*
     * should be executed before each test method
     */
    @BeforeEach
    void setUpMethod() {
        // Prepare arguments to constructor
        setAccessKey(RandomStringUtils.randomAlphabetic(10));
        setSecretKey(RandomStringUtils.randomAlphabetic(10));
        setRegion(Region.values()[RandomUtils.nextInt(1, 20)].getFirstRegionId());
        // Prepare service to execution
        setFactory(new DynamoDBFactory(getAccessKey(), getSecretKey(), getRegion()));
    }

    /*
     * should be executed after each test method
     */
    @AfterEach
    void setDownMethod() {
        setFactory(null);
    }

    @Nested
    @DisplayName("For nominal cases")
    class testNominal {

        @Test
        @DisplayName("When DynamoDBMapper Bean is initialized")
        void whenStartContext_thenBeanCreated() {
            var actualBean = getFactory().mapper();

            assertThat(actualBean).isNotNull();
        }

        @Test
        @DisplayName("When use Amazon credentials - Access key")
        void whenAwsAccessKey_thenSameValue() {
            var expect = getAccessKey();

            var actual = getFactory().accessKey;

            assertThat(actual).isEqualTo(expect);
        }

        @Test
        @DisplayName("When use Amazon credentials - Secret key")
        void whenAwsSecretKey_thenSameValue() {
            var expect = getSecretKey();

            var actual = getFactory().secretKey;

            assertThat(actual).isEqualTo(expect);
        }

        @Test
        @DisplayName("When use Amazon credentials - Region")
        void whenAwsRegionKey_thenSameValue() {
            var expect = getRegion();

            var actual = getFactory().region;

            assertThat(actual).isEqualTo(expect);
        }
    }

    @Nested
    @DisplayName("For corner cases")
    class testCorner {

        @Test
        @DisplayName("When contructor are available")
        void testPublicContructor() {
            var constructor = AmazonMQFactory.class.getConstructors();

            assertThat(constructor)
                    .satisfies(
                            cons -> {
                                assertThat(Modifier.isPublic(cons[0].getModifiers())).isTrue();
                                assertThat(Modifier.isPublic(cons[1].getModifiers())).isTrue();
                            });
        }

        @Test
        @DisplayName("When all arguments are instanced")
        void testAllArguments() {
            var expectFactory = new DynamoDBFactory(getAccessKey(), getSecretKey(), getRegion());

            var actualFactory = getFactory();

            assertThat(actualFactory)
                    .satisfies(
                            factory -> {
                                assertThat(factory.accessKey).isEqualTo(expectFactory.accessKey);
                                assertThat(factory.secretKey).isEqualTo(expectFactory.secretKey);
                                assertThat(factory.region).isEqualTo(expectFactory.region);
                            });
        }

        @Test
        @DisplayName("When no argument are instanced")
        void testNoArgument() {
            var actual = new AmazonMQFactory();

            assertThat(actual)
                    .satisfies(
                            factory -> {
                                assertThat(factory.accessKey).isNull();
                                assertThat(factory.secretKey).isNull();
                                assertThat(factory.region).isNull();
                            });
        }

        @Nested
        @DisplayName("The context exceptions")
        class testContextExceptions {

            @Test
            @DisplayName("When invalid AWS access key provided")
            void whenInvalidAccessKey_thenIllegalArgument() throws Exception {
                // given - prepare context to invalid region
                var factory =
                        new DynamoDBFactory(
                                null,
                                getSecretKey(),
                                Region.values()[RandomUtils.nextInt(1, 20)].getFirstRegionId());
                // given - BasicAWSCredentials response when it try to bean instance
                final IllegalArgumentException expect =
                        new IllegalArgumentException("Access key cannot be null.");

                // when
                final IllegalArgumentException actual =
                        assertThrows(IllegalArgumentException.class, () -> factory.mapper());

                // then
                assertThat(actual)
                        .satisfies(
                                runtimeEx -> {
                                    assertThat(runtimeEx.getClass()).isEqualTo(expect.getClass());
                                    assertThat(runtimeEx.getMessage())
                                            .contains(expect.getMessage());
                                });
            }

            @Test
            @DisplayName("When invalid AWS secret key provided")
            void whenInvalidSecretKey_thenIllegalArgument() throws Exception {
                // given - prepare context to invalid region
                var factory =
                        new DynamoDBFactory(
                                getAccessKey(),
                                null,
                                Region.values()[RandomUtils.nextInt(1, 20)].getFirstRegionId());
                // given - BasicAWSCredentials response when it try to bean instance
                final IllegalArgumentException expect =
                        new IllegalArgumentException("Secret key cannot be null.");

                // when
                final IllegalArgumentException actual =
                        assertThrows(IllegalArgumentException.class, () -> factory.mapper());

                // then
                assertThat(actual)
                        .satisfies(
                                runtimeEx -> {
                                    assertThat(runtimeEx.getClass()).isEqualTo(expect.getClass());
                                    assertThat(runtimeEx.getMessage())
                                            .contains(expect.getMessage());
                                });
            }

            @Test
            @DisplayName("When invalid AWS region provided")
            void whenInvalidRegion_thenIllegalArgument() throws Exception {
                // given - prepare context to invalid region
                var factory =
                        new DynamoDBFactory(
                                getAccessKey(),
                                getSecretKey(),
                                Region.values()[RandomUtils.nextInt(1, 20)].name());
                // given - BasicAWSCredentials response when it try to bean instance
                final IllegalArgumentException expect =
                        new IllegalArgumentException(
                                String.format(
                                        "Endpoint does not contain a valid host name:"
                                                + " https://dynamodb.%s.amazonaws.com",
                                        factory.region));

                // when
                final IllegalArgumentException actual =
                        assertThrows(IllegalArgumentException.class, () -> factory.mapper());

                // then
                assertThat(actual)
                        .satisfies(
                                runtimeEx -> {
                                    assertThat(runtimeEx.getClass()).isEqualTo(expect.getClass());
                                    assertThat(runtimeEx.getMessage())
                                            .contains(expect.getMessage());
                                });
            }
        }
    }
}

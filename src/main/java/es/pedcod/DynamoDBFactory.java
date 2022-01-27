package es.pedcod;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Factory
@NoArgsConstructor
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
public class DynamoDBFactory {

    @Value("${aws.accessKeyId}")
    String accessKey;

    @Value("${aws.secretAccessKey}")
    String secretKey;

    @Value("${aws.region}")
    String region;

    /**
     * Object mapper for domain-object interaction with DynamoDB
     *
     * @return Bean initialized
     */
    @Bean
    public DynamoDBMapper mapper() {
        return new DynamoDBMapper(awsDynamoDBConfig());
    }

    /**
     * Build the connection in configuration parameters provided.
     *
     * @return Fully configured implementation of AmazonDynamoDB.
     */
    private AmazonDynamoDB awsDynamoDBConfig() {
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                String.format("dynamodb.%s.amazonaws.com", region), region))
                .withCredentials(
                        new AWSStaticCredentialsProvider(
                                new BasicAWSCredentials(accessKey, secretKey)))
                .build();
    }
}

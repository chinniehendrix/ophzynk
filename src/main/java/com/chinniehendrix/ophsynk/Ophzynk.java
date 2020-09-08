package com.chinniehendrix.ophzynk;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AlterConsumerGroupOffsetsResult;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.DescribeConsumerGroupsResult;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.ConsumerGroupState;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.mirror.RemoteClusterUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import java.util.Properties;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Collections;
import java.util.Map;
import java.io.*;

public class Ophzynk 
{
    static Logger logger = LogManager.getLogger("CONSOLE_JSON_APPENDER");
    public static final String consumerGroupId = "myconsumergroup";
    public static final String propertiesFileName = "/etc/config/ophzynk.properties";

    public static AdminClient createClient(String bootstrapServers){
        Properties properties = new Properties();

        logger.info("Checking properties file {}", propertiesFileName);
        File propertiesFile = new File(propertiesFileName);
        // if (propertiesFile.isFile()) {
          logger.info("Loading properties from {}", propertiesFileName);
          Properties propertyOverrides = new Properties();
          try (BufferedReader propsReader = new BufferedReader(new FileReader(propertiesFile))) {
            propertyOverrides.load(propsReader);
          } catch (IOException e) {
            logger.error(e);
          }
          properties.putAll(propertyOverrides);
        // }

        properties.put("bootstrap.servers", bootstrapServers);
        properties.put("connections.max.idle.ms", 10000);
        properties.put("request.timeout.ms", 20000);
        return AdminClient.create(properties);
    }

    public static boolean checkConsumerGroups(String bootstrapServers) {
        AdminClient adminClient = createClient(bootstrapServers);
        if (adminClient != null) {
            logger.info("Entering checkConsumerGroups \n");
            DescribeConsumerGroupsResult describeConsumerGroupsResult = adminClient.describeConsumerGroups(Collections.singletonList(consumerGroupId));
            logger.info("describeConsumerGroups call completed \n");
            Map<String, ConsumerGroupDescription> consumerGroupDescriptions;
            try {
                logger.info("Entering try \n");
                consumerGroupDescriptions = describeConsumerGroupsResult.all().get();
                logger.info("ConsumerGroupDescriptions for consumer group {} - {} \n", consumerGroupId, consumerGroupDescriptions);
                return consumerGroupDescriptions.get(consumerGroupId).state() == ConsumerGroupState.EMPTY || (consumerGroupDescriptions.get(consumerGroupId).state() == ConsumerGroupState.DEAD);
            } catch (InterruptedException | ExecutionException e) {
                logger.error(e);
                return false;
            }

        } else {
            logger.error("AdminClient is null. Cannot proceed. \n");
            return false;
        }
    }

    public static void createTopics(String bootstrapServers) {
        try (AdminClient client = createClient(bootstrapServers)) {
            CreateTopicsResult result = client.createTopics(Arrays.asList(
                    new NewTopic("storage-topic", 1, (short) 1),
                    new NewTopic("global-id-topic", 1, (short) 1),
                    new NewTopic("snapshot-topic", 1, (short) 1)
            ));
            try {
                result.all().get();
            } catch ( InterruptedException | ExecutionException e ) {
                throw new IllegalStateException(e);
            }
        }
    }

    public static void main( String[] args )  throws InterruptedException 
    {
        logger.debug( "Entering main function" );
        createTopics("cluster-kafka-bootstrap.kafka.svc.cluster.local:9092");
        Thread.sleep(3600);
    }
}

package kafka.consumer;

import kafka.config.AppConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;
import java.util.regex.Pattern;

public class ElasticSearchConsumer {
    static Logger logger = LoggerFactory.getLogger(ElasticSearchConsumer.class);

    static KafkaConsumer<String,String> getKafkaConsumer(String topicName){
        Properties consumerConfigs = AppConfig.getConsumerConfigs();
        KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(consumerConfigs);
        consumer.subscribe(Pattern.compile(topicName));
        return consumer;
    }


    public static void main(String ...a) throws Exception{
        RestHighLevelClient elasticSearchClient = AppConfig.getElasticSearchClient();
        KafkaConsumer<String, String> twitterConsumer = getKafkaConsumer("twitter_tweets");
        while(true){
            ConsumerRecords<String, String> consumerRecords = twitterConsumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                String tweet = consumerRecord.value();
                logger.info("Received tweet from kafka "+tweet);
                // if index twitter does not exists , then it will throw error , so create twitter index in elastic first
                IndexRequest indexRequest = new IndexRequest("twitter","tweets").source(tweet, XContentType.JSON);
                IndexResponse indexResponse = elasticSearchClient.index(indexRequest, RequestOptions.DEFAULT);
                String id = indexResponse.getId();
                logger.info("Document created with id  "+  id);

            }
            //close client gracefully
            //elasticSearchClient.close();//close client gracefully
        }




    }


}

package cloud.connect;

import akka.actor.typed.ActorRef;
import base.model.bean.BasicCommon;
import base.model.connect.bean.KafkaMsg;
import cloud.global.GlobalKafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author ：LLH
 * @date ：Created in 2021/4/16 15:42
 * @description：kafka 接入数据
 */
public class KafkaConnectInDemo {
    private static Logger logger = Logger.getLogger(KafkaConnectInDemo.class.getName());

    KafkaConsumer<String, String> consumer;

    public KafkaConnectInDemo() {
        init();
    }

    private void init() {
        Properties kafkaPropertie = new Properties();
        //配置broker地址，配置多个容错
        kafkaPropertie.put("bootstrap.servers", "localhost:9092");
        //配置key-value允许使用参数化类型，反序列化
        kafkaPropertie.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        kafkaPropertie.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        //指定消费者所属的群组
        kafkaPropertie.put("group.id","1");
        //创建KafkaConsumer，将kafkaPropertie传入。
        consumer = new KafkaConsumer<String, String>(kafkaPropertie);
        List<String> topics = new ArrayList<>();
        topics.add("test");

//        Pattern pattern = Pattern.compile(GlobalKafkaConfig.cloud_in_topic);
        consumer.subscribe(topics);
        logger.log(Level.INFO, "EdgeKafkaConnectIn is listening..." + GlobalKafkaConfig.cloud_in_topic);
        //轮询消息
        while (true) {
            //获取ConsumerRecords，一秒钟轮训一次
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            //消费消息，遍历records
//            KafkaMsg outData = new KafkaMsg();
            List<KafkaMsg> msgs = new ArrayList<>();
            for (ConsumerRecord<String, String> r : records) {

                KafkaMsg data = new KafkaMsg();

                System.out.println(r.topic()+"    333333333333");
                data.setTopic(r.topic());
                data.setKey(r.key());
                data.setValue(r.value());
                msgs.add(data);
                System.out.println("kafkaConnectIn " + r.topic() + ":" + r.key() + ":" + r.value());
            }

        }
    }

    public static void main(String[] args) {
        KafkaConnectInDemo kafkaConnectInDemo = new KafkaConnectInDemo();
    }
}

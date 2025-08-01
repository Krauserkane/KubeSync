package KubeSycn.Consumer;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.*;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.util.*;

@Component
public class Consumer{

    @PostConstruct
    public void init() {
        new Thread(() -> {
            try {
                consumeMessage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void consumeMessage() throws IOException {
        Properties properties=new Properties();
        properties.setProperty("bootstrap.servers","kafka-0.kafka.kafka.svc.cluster.local:9092");
        properties.put("key.deserializer", LongDeserializer.class.getName());
        properties.setProperty("value.deserializer", ByteArrayDeserializer.class.getName());

        int randomGroupId = new Random().nextInt(9000) + 1000;

        properties.setProperty("group.id",String.valueOf(randomGroupId));
        properties.setProperty("auto.offset.reset","earliest");

        KafkaConsumer<String,byte[]> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList("demoTopic"));

        HashMap<String, ArrayList<byte[]>>map = new HashMap<>();

       while(true){
           System.out.println("Polling....");

           ConsumerRecords<String, byte[]> records= consumer.poll(Duration.ofMillis(5000));

           for(ConsumerRecord<String,byte[]> record:records){

               //Do something

               //Add the element to the HashMap
               //If it is the last element the call an async funcation to make it special, hehehehheheh
               String fileId = new String(record.headers().lastHeader("fileId").value());
               String currentChunk = new String(record.headers().lastHeader("chunkNumber").value());
               String totalChunk = new String(record.headers().lastHeader("totalChunks").value());
               String ipAddress = new String(record.headers().lastHeader("IP").value());
               String fileName= new String(record.headers().lastHeader("fileName").value());
               System.out.println("Producer Ip address- "+ipAddress);
               System.out.println("Consunmer Ip address- "+InetAddress.getLocalHost().getHostAddress());
               if(ipAddress.equalsIgnoreCase(InetAddress.getLocalHost().getHostAddress())){
                   System.out.println("I found a similar IP address");
                   continue;
               }


               if(map.get(fileId)==null){
                   ArrayList<byte[]> list= new ArrayList<>();
                   list.add(record.value());
                   map.put(fileId,list);
               }
               else{
                   map.get(fileId).add(record.value());
               }

               if(currentChunk.equalsIgnoreCase(totalChunk)){
                   processFile(map.get(fileId),fileName);

               }
           }
       }

    }

    private void processFile(ArrayList<byte[]> bytes, String fileName) throws IOException {
        System.out.println("In here writing to chunk");
        String uploadDir="/files/";

            try(BufferedOutputStream buffer=new BufferedOutputStream(new FileOutputStream(uploadDir+fileName))){

            for(byte[] chunkedByte:bytes){
                System.out.println(chunkedByte.length);
                buffer.write(chunkedByte);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

}

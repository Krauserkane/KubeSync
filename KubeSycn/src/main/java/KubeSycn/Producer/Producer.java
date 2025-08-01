package KubeSycn.Producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;
import java.net.InetAddress;

@Component
public class Producer {


    public void sendMessage(String fileName) throws IOException {

        String uploadDir="/files/";
        Properties properties=new Properties();
        properties.setProperty("bootstrap.servers","kafka-0.kafka.kafka.svc.cluster.local:9092");
        properties.put("key.serializer", LongSerializer.class.getName());
        properties.setProperty("value.serializer", ByteArraySerializer.class.getName());

        KafkaProducer<Long,byte[]> producer= new KafkaProducer<>(properties);

        BufferedInputStream buffer= new BufferedInputStream(new FileInputStream(uploadDir+fileName));

        byte[] chunkSize=new byte[1024];
        String fileId = (String.valueOf(UUID.randomUUID()));

        File file= new File(uploadDir+fileName);
        Long fileSize=  file.length();
        Integer totatlChunksCreated= (int) Math.ceil((double)fileSize/1024);
        int currentChunk=0;
        System.out.println(totatlChunksCreated);

        int byteData;
        while ((byteData = buffer.read(chunkSize)) != -1) {
            // Process the single byte here
            currentChunk++;
            System.out.println("Read byte: " + byteData);
            byte[] chunk = new byte[byteData];
            System.arraycopy(chunkSize, 0, chunk, 0, byteData);
            System.out.println(chunk);
            ProducerRecord<Long,byte[]>record= new ProducerRecord<>("demoTopic",chunk);
            System.out.println("TheFileId is"+fileId);

            setHeaders(record,fileName,fileId,totatlChunksCreated,currentChunk);
            try {
                producer.send(record);
                System.out.println("Record sent sucessfully");
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        System.out.println("or am I here");
        producer.close();
        System.out.println("Am I here");

    }

    public void setHeaders(ProducerRecord<Long,byte[]>record,String fileName,String fileId,Integer totatlChunksCreated,int currentChunk) throws UnknownHostException {
        record.headers().add("fileId", fileId.getBytes());
        record.headers().add("totalChunks",String.valueOf(totatlChunksCreated).getBytes());
        record.headers().add("chunkNumber",String.valueOf(currentChunk).getBytes());
        record.headers().add("IP",String.valueOf(InetAddress.getLocalHost().getHostAddress()).getBytes());
        record.headers().add("fileName",fileName.getBytes());
    }

}

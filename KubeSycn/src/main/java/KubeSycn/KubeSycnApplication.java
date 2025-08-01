package KubeSycn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("KubeSycn")
public class KubeSycnApplication {

	public static void main(String[] args) {
		SpringApplication.run(KubeSycnApplication.class, args);
	}

}

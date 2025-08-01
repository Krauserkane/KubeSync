package KubeSycn.controllers;


import KubeSycn.Producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



@RestController
public class FileController {

    @Autowired
    Producer producer;

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file){

        String uploadDir="/files/";
        try {
            // Create the file on the server
            File newFile = new File(uploadDir + file.getOriginalFilename());
            // Save the file to the specified location
            try (BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(newFile))) {
                stream.write(file.getBytes());
            }

            producer.sendMessage(file.getOriginalFilename());
            System.out.println("Am I here>?");

            return String.valueOf(new ResponseEntity<>("File uploaded successfully: " + newFile.getAbsolutePath(), HttpStatus.OK));
        } catch (IOException e) {
            e.printStackTrace();
            return String.valueOf(new ResponseEntity<>("File upload failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }

    }

}

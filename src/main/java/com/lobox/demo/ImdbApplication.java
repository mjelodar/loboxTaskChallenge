package com.lobox.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@SpringBootApplication
public class ImdbApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImdbApplication.class, args);
//		readFile();
	}

	public static void readFile(){
		try(BufferedReader br = new BufferedReader(new FileReader("name.basics.tsv"));
			FileWriter fw = new FileWriter("nameTest.tsv")){

			int i = 1000000;

			while(i > 0){
				log.info("step" + i);
				fw.write(br.readLine());
				fw.write("\n");
				i--;
			}

		}catch(IOException e){
			log.error("error in reading file : {}", e);
		}




	}

}

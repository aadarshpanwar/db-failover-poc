package com.example.demo;

import com.fasterxml.jackson.databind.JsonNode;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
public class DemoApplication {

	@Autowired
	private PersonRepo personRepo;

	private RestTemplate template = new RestTemplate();

	private String IP = "3.249.163.205";

	public static void main(String[] args) {
//		java.security.Security.setProperty("networkaddress.cache.ttl" , "1");
//		java.security.Security.setProperty("networkaddress.cache.negative.ttl" , "3");
		SpringApplication.run(DemoApplication.class, args);

	}


	@PostMapping("/person")
	public Person addOne(@RequestBody Person person) {
		return personRepo.save(person);
	}

	@GetMapping("/person-all")
	@Transactional(readOnly = true)
	public List<Person> getAll() {
		return personRepo.findAll();
	}

	@GetMapping("/send-local/{count}")
	public void sendPostLocal(@PathVariable("count") int count) throws InterruptedException {
		for(int i = 0;i<count;++i) {
			try {
				Person person = new Person();
				person.setName("ram");
				person.setAge(12);
				personRepo.save(person);
				System.out.println("saved :"+ person.getId());
			}catch(Exception e) {
				System.out.println("Exception Person list " + e.getMessage());
			}
			Thread.sleep(1000);
		}
	}
	@GetMapping("send-find-all/{count}")
	public void getAll(@PathVariable("count") int count) throws InterruptedException {
		for(int i = 0;i<count;++i) {
			try {
				JsonNode res = template.getForObject("http://"+IP+ ":8080/person-all", JsonNode.class);
				List<Person> list = personRepo.findAll();
				System.out.println("Person list size :"+ list.size());
			}catch(Exception e) {
				System.out.println("Exception Person list " + e.getMessage());
			}
			Thread.sleep(1000);
		}
	}



	@GetMapping("/send-req-ec2/{count}")
	public Object send(@PathVariable("count") int count) throws InterruptedException {
		 Person person = new Person();
		 person.setName("ram");
		 person.setAge(12);
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Person> request =
				new HttpEntity<Person>(person, headers);
		for(int i = 0;i<count;++i) {
			try {
				JsonNode res = template.postForObject("http://"+IP+":8080/person", request, JsonNode.class);
				System.out.println("response :"+ res.toString());
			}catch(Exception e) {
				System.out.println("Exception: " + e.getMessage());
			}
			Thread.sleep(1000);
		}
		return null;
	}
}
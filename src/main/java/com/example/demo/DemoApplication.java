package com.example.demo;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;

@SpringBootApplication
@RestController
public class DemoApplication {

	@Autowired
	private PersonRepo personRepo;

	@Autowired
	private DataSource source;

	@Autowired
	private ApplicationContext applicationContext;
	@Value("${spring.datasource.url}")
	private String url;
	@Value("${spring.datasource.username}")
	private String username;
	@Value("${spring.datasource.password}")
	private String password;

	@Autowired
	LocalContainerEntityManagerFactoryBean entityManager;

	public static void main(String[] args) {
//		java.security.Security.setProperty("networkaddress.cache.ttl" , "1");
//		java.security.Security.setProperty("networkaddress.cache.negative.ttl" , "3");
		SpringApplication.run(DemoApplication.class, args);

	}

	@PostMapping("/person-all")
	public void add() throws InterruptedException {
		//return personRepo.save(person);
		for (int i = 0; i < 1000; i++) {

			try {
//				InetAddress address = InetAddress.getByName("dbfailover-poc.cluster-cdibd9suphjv.eu-west-1.rds.amazonaws.com");
//				System.out.println(getCurrentTime() + " lookup success " + address);
				Person person = new Person();
				person.setName("name");
				person.setAge(12);
				personRepo.save(person);
				System.out.println(getCurrentTime() + ":saved");
			} catch (Exception ignore) {
				System.out.println(getCurrentTime() +",save failed:"+ ignore.getMessage());
			} finally {
				Thread.sleep(1000);
			}
		}
	}

	@PostMapping("/person")
	public Person addOne(@RequestBody Person person) {
		return personRepo.save(person);
	}

	private static String getCurrentTime() {
		DateFormat dateFormat = new SimpleDateFormat("mm:ss");
		return dateFormat.format(Calendar.getInstance().getTime());
	}

	@GetMapping("/soft-evict")
	public void context() throws InterruptedException {
		HikariDataSource hikari = (HikariDataSource)source;
		HikariConfigMXBean config = hikari.getHikariConfigMXBean();

		HikariPool pool = (HikariPool) hikari.getHikariPoolMXBean();
		hikari.getHikariPoolMXBean().softEvictConnections();
	}

	@GetMapping("pool-stat")
	public Map<String,Object> getPoolState() {
		HikariDataSource hikari = (HikariDataSource)source;
		HikariPool pool = (HikariPool) hikari.getHikariPoolMXBean();
		Map<String, Object> map = new HashMap<>();
		map.put("total", pool.getTotalConnections());
		map.put("idle", pool.getIdleConnections());
		map.put("active", pool.getActiveConnections());
		map.put("awaiting", pool.getThreadsAwaitingConnection());
		return map;
	}

	public DataSource dataSource(){
		HikariConfig hikariConfig = new HikariConfig();
		hikariConfig.setDriverClassName("org.postgresql.Driver");
		hikariConfig.setJdbcUrl(url);
		hikariConfig.setUsername(username);
		hikariConfig.setPassword(password);

		hikariConfig.setMaximumPoolSize(5);
		hikariConfig.setConnectionTestQuery("SELECT 1");


		HikariDataSource dataSource = new HikariDataSource(hikariConfig);

		return dataSource;
	}

}
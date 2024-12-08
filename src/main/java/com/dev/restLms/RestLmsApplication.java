package com.dev.restLms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



// 
/* 현 상태
 * 1. DB 제외
 * 2. 스웨거 사용을 위해 컨트롤러 패키지 스캔 -> @SpringBootApplication(scanBasePackages = "com.dev")
 * 
 */

//  MySQL 포함
@SpringBootApplication
public class RestLmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestLmsApplication.class, args);
	}
}
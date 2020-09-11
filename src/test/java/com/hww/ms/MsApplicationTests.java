package com.hww.ms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class MsApplicationTests {
@Autowired RedisTemplate redisTemplate;
	@Test
	void contextLoads() {
		//redisTemplate.opsForSet().add("setValue","A","B","C","B","D","E","F");
		boolean isMember = redisTemplate.opsForSet().isMember("blacklist","12");
		System.out.println(isMember);
	}

}

package com.skillsync.mentor.feign;

import com.skillsync.mentor.dto.UserProfileResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/users")
public interface UserFeignClient {

    @GetMapping("/profile/{userId}")
    UserProfileResponseDto getUserProfile(@PathVariable("userId") Long userId);
}

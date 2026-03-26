package com.skillsync.session.feign;

import com.skillsync.session.dto.MentorResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "mentor-service", path = "/mentors")
public interface MentorFeignClient {
    @GetMapping("/{mentorId}")
    MentorResponseDto getMentorById(@PathVariable("mentorId") Long mentorId);
}

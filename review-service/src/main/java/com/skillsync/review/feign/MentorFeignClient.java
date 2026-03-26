package com.skillsync.review.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "mentor-service", path = "/api/mentors")
public interface MentorFeignClient {

    @PutMapping("/{mentorId}/rating")
    Map<String, String> updateMentorRating(@PathVariable("mentorId") Long mentorId, 
                                           @RequestParam("newRating") Double newRating);
}

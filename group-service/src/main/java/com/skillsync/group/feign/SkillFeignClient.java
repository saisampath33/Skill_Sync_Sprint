package com.skillsync.group.feign;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "skill-service", path = "/skills")
public interface SkillFeignClient {

    @GetMapping("/{id}")
    SkillResponseDto getSkillById(@PathVariable("id") Long id);

    @Data
    class SkillResponseDto {
        private Long id;
        private String name;
    }
}

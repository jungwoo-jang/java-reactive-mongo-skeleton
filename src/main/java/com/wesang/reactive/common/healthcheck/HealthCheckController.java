package com.wesang.reactive.common.healthcheck;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {

  private final BuildProperties buildProperties;

  @GetMapping("/")
  public Mono<SystemStatus> getSystemStatus() {
    return Mono.just(new SystemStatus(buildProperties.getVersion()));
  }
}

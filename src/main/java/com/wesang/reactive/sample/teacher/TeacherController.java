package com.wesang.reactive.sample.teacher;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

import com.wesang.reactive.sample.teacher.dto.TeacherGetResponse;
import com.wesang.reactive.sample.teacher.dto.TeacherPostRequest;
import com.wesang.reactive.sample.teacher.dto.TeacherPostResponse;

@RestController
@RequiredArgsConstructor
public class TeacherController {

  private final TeacherService teacherService;

  @PostMapping(path = "v1/teachers")
  ResponseEntity<Mono<TeacherPostResponse>> postTeacher(
      @RequestBody final TeacherPostRequest teacherPostRequest) {
    return ResponseEntity.ok()
        .body(
            teacherService
                .registerTeacher(teacherPostRequest.teacherId(), teacherPostRequest.name())
                .map(teacher -> new TeacherPostResponse(teacher.getTeacherId())));
  }

  @GetMapping(path = "v1/teachers/{teacherId}")
  ResponseEntity<Mono<TeacherGetResponse>> getTeacher(@PathVariable final String teacherId) {
    return ResponseEntity.ok()
        .body(
            this.teacherService
                .getTeacher(teacherId)
                .map(teacher -> new TeacherGetResponse(teacher.getTeacherId(), teacher.getName())));
  }
}

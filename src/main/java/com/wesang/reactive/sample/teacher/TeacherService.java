package com.wesang.reactive.sample.teacher;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TeacherService {

  private final TeacherRepository teacherRepository;

  public Mono<Teacher> registerTeacher(String teacherId, String name) {
    return this.teacherRepository.save(Teacher.builder().teacherId(teacherId).name(name).build());
  }

  public Mono<Teacher> getTeacher(String teacherId) {
    return teacherRepository.findById(teacherId);
  }
}

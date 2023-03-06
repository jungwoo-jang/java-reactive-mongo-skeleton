package com.wesang.reactive.sample.teacher;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TeacherRepository extends ReactiveMongoRepository<Teacher, String> {}

package com.wesang.sample.teacher.dto;

import java.time.ZonedDateTime;

public record TeacherGetResponse(
        String teacherId, String name, ZonedDateTime createdAt, ZonedDateTime updatedAt) {}

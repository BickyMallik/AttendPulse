package com.AttendPulse.attend_backend.controller;

import com.AttendPulse.attend_backend.dto.AttendanceSessionRequest;
import com.AttendPulse.attend_backend.dto.StudentRequest;
import com.AttendPulse.attend_backend.dto.SubjectRequest;
import com.AttendPulse.attend_backend.entity.AttendanceSession;
import com.AttendPulse.attend_backend.entity.Student;
import com.AttendPulse.attend_backend.entity.Subject;
import com.AttendPulse.attend_backend.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @PostMapping("/subject")
    public ResponseEntity<Subject> addSubject(@Valid @RequestBody SubjectRequest request,
                                              Authentication auth) {
        return ResponseEntity.ok(teacherService.addSubject(request, auth.getName()));
    }

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getMySubjects(Authentication auth) {
        return ResponseEntity.ok(teacherService.getMySubjects(auth.getName()));
    }

    @PostMapping("/student")
    public ResponseEntity<Student> addStudent(@Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(teacherService.addStudent(request));
    }

    @PostMapping("/enroll/{studentId}/{subjectId}")
    public ResponseEntity<String> enrollStudent(@PathVariable Long studentId,
                                                @PathVariable Long subjectId) {
        return ResponseEntity.ok(teacherService.enrollStudent(studentId, subjectId));
    }

    @PostMapping("/session/start")
    public ResponseEntity<AttendanceSession> startSession(
            @Valid @RequestBody AttendanceSessionRequest request,
            Authentication auth) {
        return ResponseEntity.ok(teacherService.startSession(request, auth.getName()));
    }

    @PutMapping("/session/lock/{sessionId}")
    public ResponseEntity<String> lockSession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(teacherService.lockSession(sessionId));
    }

    @GetMapping("/session/{subjectId}")
    public ResponseEntity<List<AttendanceSession>> getSessions(@PathVariable Long subjectId) {
        return ResponseEntity.ok(teacherService.getSessionsBySubject(subjectId));
    }
}
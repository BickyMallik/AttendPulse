package com.AttendPulse.attend_backend.service;

import com.AttendPulse.attend_backend.dto.AttendanceSessionRequest;
import com.AttendPulse.attend_backend.dto.StudentRequest;
import com.AttendPulse.attend_backend.dto.SubjectRequest;
import com.AttendPulse.attend_backend.entity.*;
import com.AttendPulse.attend_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class TeacherService {

    @Autowired private UserRepository userRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private EnrollmentRepository enrollmentRepository;
    @Autowired private AttendanceSessionRepository sessionRepository;
    @Autowired private PasswordEncoder passwordEncoder;


    public Subject addSubject(SubjectRequest request, String teacherEmail) {
        User teacher = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found!"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found!"));

        Subject subject = new Subject();
        subject.setName(request.getName());
        subject.setCode(request.getCode());
        subject.setDepartment(department);
        subject.setTeacher(teacher);
        subject.setSemester(request.getSemester());
        subject.setSessionLabel(request.getSessionLabel());

        return subjectRepository.save(subject);
    }


    public List<Subject> getMySubjects(String teacherEmail) {
        User teacher = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found!"));
        return subjectRepository.findByTeacherId(teacher.getId());
    }


    public Student addStudent(StudentRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.Role.STUDENT);
        userRepository.save(user);

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found!"));

        Student student = new Student();
        student.setUser(user);
        student.setRollNo(request.getRollNo());
        student.setDepartment(department);
        student.setBatchYear(request.getBatchYear());

        return studentRepository.save(student);
    }

    public String enrollStudent(Long studentId, Long subjectId) {
        if (enrollmentRepository.existsByStudentIdAndSubjectId(studentId, subjectId)) {
            return "Student already enrolled!";
        }
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found!"));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found!"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setSubject(subject);
        enrollmentRepository.save(enrollment);
        return "Student enrolled successfully!";
    }

    public AttendanceSession startSession(AttendanceSessionRequest request, String teacherEmail) {
        User teacher = userRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found!"));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found!"));

        String otp = String.format("%06d", new Random().nextInt(999999));

        AttendanceSession session = new AttendanceSession();
        session.setSubject(subject);
        session.setOtpCode(otp);
        session.setOtpExpiresAt(LocalDateTime.now().plusMinutes(10));
        session.setIsLocked(false);
        session.setMaxCount(request.getMaxCount());
        session.setSessionDate(LocalDateTime.now());
        session.setTeacher(teacher);

        return sessionRepository.save(session);
    }

    public String lockSession(Long sessionId) {
        AttendanceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found!"));
        session.setIsLocked(true);
        sessionRepository.save(session);
        return "Session locked!";
    }

    public List<AttendanceSession> getSessionsBySubject(Long subjectId) {
        return sessionRepository.findBySubjectId(subjectId);
    }
}
package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudent(@PathVariable long id) {
        Student student = service.findStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PostMapping
    public Student addStudent(@RequestBody Student student) {
        return service.addStudent(student);
    }

    @PutMapping
    public ResponseEntity<Student> editeStudent(@RequestBody Student student) {
        Student foundStudent = service.updateStudent(student);
        if (foundStudent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Student> removeStudent(@PathVariable long id) {
        service.removeStudent(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Student>> filterByAge(@RequestParam(required = false) Integer age,
                                                     @RequestParam(required = false) Integer min,
                                                     @RequestParam(required = false) Integer max) {
        if (age != null) {
            return ResponseEntity.ok(service.filterStudentByAge(age));
        }
        if (min != null && max != null) {
            return ResponseEntity.ok(service.findByAgeBetween(min, max));
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/{id}/faculty")
    public ResponseEntity<Faculty> getFaculty(@PathVariable Long id) {
        if (id != null) {
            return ResponseEntity.ok(service.getStudentsFaculty(id));
        }
        return ResponseEntity.notFound().build();
    }
}

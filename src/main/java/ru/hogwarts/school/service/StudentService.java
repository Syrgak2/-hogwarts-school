package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final StudentRepository studentRepo;

    private final Object flag = new Object();

//    счетчик id students во время работы метода
//    ID увеличивается после работы каждого потока.
    public Integer countId = 0;
    Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(StudentRepository studentRepo) {
        this.studentRepo = studentRepo;
    }

    public Student addStudent(Student student) {
        logger.trace("Wos invoked method for save student");
        return studentRepo.save(student);
    }

    public List<Student> saveAll(List<Student> students) {
        return studentRepo.saveAll(students);
    }

    public Student findStudent(long id) {
        logger.trace("Wos invoked method for find student");
        return studentRepo.findById(id).get();
    }

    public Student updateStudent(Student student) {
        logger.trace("Wos invoked method for edite student");
        return studentRepo.save(student);
    }

    public void removeStudent(long id) {
        logger.trace("Wos invoked method for remove student");
        studentRepo.deleteById(id);
    }

    public List<Student> filterStudentByAge(int age) {
        logger.trace("Wos invoked method for filter students by age");
        return studentRepo.findStudentsByAge(age);
    }

    public List<Student> findByAgeBetween(int min, int max) {
        logger.trace("Wos invoked method for filter students between age");
        return studentRepo.findByAgeBetween(min, max);
    }

    public Faculty getStudentsFaculty(Long id) {
        logger.trace("Wos invoked method for get students faculty");
        return findStudent(id).getFaculty();
    }

    public Integer countStudents() {
        logger.trace("Wos invoked method for get students amount ");
        return studentRepo.countStudents();
    }

    public Integer countAverageAge() {
        logger.trace("Wos invoked method for count students average age");
        return studentRepo.countAverageAge();
    }

    public List<Student> findLastFiveStudents() {
        logger.trace("Wos invoked method for find last five students");
        return studentRepo.finFirst5ByOrderByIdDesc();
    }

    public List<Student> findAllStudents(Integer pageNumber, Integer pageSize) {
        logger.trace("Wos invoked method for get all students");
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return studentRepo.findAll(pageRequest).getContent();
    }

    public List<Student> findAll() {
        return studentRepo.findAll();
    }

    public List<Student> findWhoseNameStartsA() {
//        Сортировку вынес на уровень бд
        return studentRepo.findWhoseNameStartsA().stream()
                .peek(student -> {
                    String capitalized = student.getName().substring(0, 1).toUpperCase() + student.getName().substring(1);
                    student.setName(capitalized);
                })
                .sorted(Comparator.comparing(Student::getName))
                .collect(Collectors.toList());
    }

    public Double getAverageAge() {
        return findAll().stream()
                .mapToInt(Student::getAge)
                .average()
                .orElse(0);
    }

    public void printNameParallel() {
        System.out.println(Thread.currentThread().getName() + getName(1));
        System.out.println(Thread.currentThread().getName() + getName(2));

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + getName(3));
            System.out.println(Thread.currentThread().getName() + getName(4));
        }).start();

        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + getName(5));
            System.out.println(Thread.currentThread().getName() + getName(6));
        }).start();

    }

    private String getName(int id) {
        List<Student> students = findAll();
        return students.get(id).getName();
    }


    public void printNameSynchronized() {
        printSynchronized(Thread.currentThread().getName());
        printSynchronized(Thread.currentThread().getName());

        new Thread(() -> {
            printSynchronized(Thread.currentThread().getName());
            printSynchronized(Thread.currentThread().getName());
        }).start();

        new Thread(() -> {
            printSynchronized(Thread.currentThread().getName());
            printSynchronized(Thread.currentThread().getName());
        }).start();
    }

    private void printSynchronized(String streamId) {
        List<Student> students = findAll();
        synchronized (flag) {
            countId++;
        }
        System.out.println(students.get(countId).getName() + " " + countId + " " + streamId);
    }


}

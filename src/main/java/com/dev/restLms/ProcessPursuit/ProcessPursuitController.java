// ProcessPursuitController.java
package com.dev.restLms.ProcessPursuit;

import com.dev.restLms.entity.Course;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/course")
@Tag(name = "ProcessPursuitController", description = "과정 목록 및 해당 과정의 수강자 수, 과정 책임자")
public class ProcessPursuitController {

    @Autowired
    private ProcessPursuitCourseRepository courseRepository;

    @Autowired
    private ProcessPursuitUserOwnCourseRepository processPursuitUserOwnCourseRepository;

    @Autowired
    private ProcessPursuitUserRepository processPursuitUserRepository;

    @GetMapping("/titles")
    @Operation(summary = "모든 과정 조회", description = "전체 과정 목록을 반환합니다.")
    public ResponseEntity<?> getAllCoursesWithTeachers() {
        // 모든 과정 정보 가져오기
        List<Course> courses = courseRepository.findAll();

        if(courses.isEmpty()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("개설과목이 없습니다.");
        }
        
        // 결과를 저장할 리스트
        List<Map<String, Object>> resultList = new ArrayList<>();
        
        for (Course course : courses) {

            if(Long.parseLong(course.getEnrollEndDate()) >= Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))) && !course.getCourseTitle().equals("개별과목")){

                // 수강자 수 조회
                List<ProcessPursuitUserOwnCourse> userCount = processPursuitUserOwnCourseRepository.findByCourseId(course.getCourseId());
                int studentCount = userCount.size();
    
                // 과정 책임자 정보 조회
                Optional<ProcessPursuitUser> processPursuitUsers = processPursuitUserRepository.findBySessionId(course.getSessionId());
    
                
                // 각 과정 정보와 수강자 수를 HashMap에 추가
                HashMap<String, Object> courseMap = new HashMap<>();
                courseMap.put("coureId", course.getCourseId());
                courseMap.put("courseTitle", course.getCourseTitle());
                courseMap.put("courseCapacity", course.getCourseCapacity());
                courseMap.put("enrollStartDate", course.getEnrollStartDate());
                courseMap.put("enrollEndDate", course.getEnrollEndDate());
                courseMap.put("studentCount", studentCount);
                // 책임자 정보 가져오기
                courseMap.put("courseOfficerSessionId", processPursuitUsers.get().getSessionId());
                courseMap.put("courseOfficerUserName", processPursuitUsers.get().getUserName());
    
    
                // 결과를 리스트에 추가
                resultList.add(courseMap);

            }
            
        }

        Random random = new Random();
        int randomIndex = random.nextInt(resultList.size());
        Map<String, Object> ramdomCourse = new HashMap<>();
        ramdomCourse.put("ramdomCourse", resultList.get(randomIndex));
        ramdomCourse.put("Courses", resultList);

        return ResponseEntity.ok().body(ramdomCourse);
    }
}

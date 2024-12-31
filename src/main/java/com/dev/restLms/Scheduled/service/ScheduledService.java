// package com.dev.restLms.Scheduled.service;

// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Service;

// import com.dev.restLms.Scheduled.persistence.ScheduledCourseRepository;
// import com.dev.restLms.entity.Course;

// @Service
// public class ScheduledService {

//     @Autowired
//     private ScheduledCourseRepository scheduledCourseRepository;

//     @Scheduled(cron = "0 0 0 * * ?")
//     public void updateCourseProgressStatus(){

//         List<Course> courses = scheduledCourseRepository.findAll();
//         Long nowDate = Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

//         for(Course course : courses){

//             if(Long.parseLong(course.getEnrollStartDate()) >= nowDate && Long.parseLong(course.getEnrollEndDate()) > nowDate){
//                 course.setCourseProgressStatus("1");
//             }
//             if(Long.parseLong(course.getEnrollEndDate()) < nowDate){
//                 course.setCourseProgressStatus("2");
//             }

//             scheduledCourseRepository.save(course);

//         }

//     }
    
// }

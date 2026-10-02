package com.interviewbridge.service.impl;

import com.interviewbridge.constants.EntityConstants;
import com.interviewbridge.dto.request.AIQuestionRequest;
import com.interviewbridge.dto.response.AIQuestionResponse;
import com.interviewbridge.service.AIQuestionGenerationService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation generating mock structured questions customized by technology and experience level.
 */
@Service
@Profile("mock")
public class AIQuestionGenerationServiceImpl implements AIQuestionGenerationService {

    @Override
    public List<AIQuestionResponse> generateQuestions(AIQuestionRequest request) {
        List<AIQuestionResponse> questions = new ArrayList<>();
        String tech = request.technologyName();
        String exp = request.experienceLabel();
        int count = request.numberOfQuestions() > 0 ? request.numberOfQuestions() : EntityConstants.PracticeSession.DEFAULT_TOTAL_QUESTIONS;

        String techLower = tech != null ? tech.toLowerCase() : "";
        boolean isJava = techLower.contains("java") && !techLower.contains("javascript");
        boolean isSpringBoot = techLower.contains("spring");
        boolean isAngular = techLower.contains("angular");
        boolean isSql = techLower.contains("sql") || techLower.contains("database") || techLower.contains("postgres");

        for (int i = 1; i <= count; i++) {
            String questionText;
            if (isJava) {
                switch (i) {
                    case 1: questionText = String.format("Explain the difference between JDK, JRE, and JVM in Java. How is it relevant to a %s developer?", exp); break;
                    case 2: questionText = String.format("Describe memory management in Java, focusing on Heap, Stack, and Garbage Collection tuning for %s applications.", exp); break;
                    case 3: questionText = String.format("How do you implement thread safety and manage concurrent state in Java at a %s level?", exp); break;
                    case 4: questionText = String.format("Discuss the Java Collections Framework. When would you choose a HashMap vs a ConcurrentHashMap in a %s scenario?", exp); break;
                    case 5: questionText = String.format("Explain the functional programming concepts introduced in Java 8+, such as Streams and Lambdas, and how a %s developer uses them.", exp); break;
                    case 6: questionText = String.format("Describe Exception Handling best practices in Java. How do checked vs unchecked exceptions impact %s design?", exp); break;
                    case 7: questionText = String.format("What is the reflection API in Java? Discuss its use cases and performance implications in %s frameworks.", exp); break;
                    case 8: questionText = String.format("Explain how ClassLoaders work in Java and how they affect deployment configurations for %s applications.", exp); break;
                    case 9: questionText = String.format("Discuss design patterns (e.g., Singleton, Factory, Builder) and their implementation in Java at a %s level.", exp); break;
                    case 10: default: questionText = String.format("What are virtual threads (Project Loom) in recent Java versions, and how do they benefit %s concurrency scaling?", exp); break;
                }
            } else if (isSpringBoot) {
                switch (i) {
                    case 1: questionText = String.format("What is the core difference between Spring Framework and Spring Boot? Why would a %s engineer prefer Spring Boot?", exp); break;
                    case 2: questionText = String.format("Explain the Spring IoC Container and Dependency Injection lifecycle in a %s architecture.", exp); break;
                    case 3: questionText = String.format("Describe the autowiring mechanisms in Spring Boot, including @Component, @Autowired, and bean scopes for %s levels.", exp); break;
                    case 4: questionText = String.format("Discuss Spring Boot starters and auto-configuration. How do you customize auto-configuration at a %s level?", exp); break;
                    case 5: questionText = String.format("How does Spring AOP work? Explain JoinPoints, Pointcuts, and Advices in %s application patterns.", exp); break;
                    case 6: questionText = String.format("Explain transaction management in Spring Boot using @Transactional. What are propagation and isolation levels for %s systems?", exp); break;
                    case 7: questionText = String.format("How does Spring Security secure a REST API using JWT? Explain filters and SecurityContext configurations for a %s app.", exp); break;
                    case 8: questionText = String.format("What is Spring Boot Actuator? How do you monitor and expose custom metrics/endpoints for %s production environments?", exp); break;
                    case 9: questionText = String.format("Describe database integration in Spring Boot using Spring Data JPA, focusing on Query creation and LazyInitializationException for %s devs.", exp); break;
                    case 10: default: questionText = String.format("How do you handle profiles and configuration properties dynamically in a %s Spring Boot microservices setup?", exp); break;
                }
            } else if (isAngular) {
                switch (i) {
                    case 1: questionText = String.format("What are the key architectural building blocks of an Angular application for a %s developer?", exp); break;
                    case 2: questionText = String.format("Explain Angular Component Lifecycle Hooks (e.g. ngOnInit, ngOnChanges) and when to use them in %s UIs.", exp); break;
                    case 3: questionText = String.format("Describe dependency injection in Angular. What are hierarchical injectors and how do they affect %s module designs?", exp); break;
                    case 4: questionText = String.format("Discuss Angular change detection strategies (Default vs OnPush) and performance optimizations for %s apps.", exp); break;
                    case 5: questionText = String.format("Explain data binding in Angular, including two-way binding and property/event binding at a %s level.", exp); break;
                    case 6: questionText = String.format("What is RxJS and how does it integrate with Angular? Compare Promises and Observables in %s applications.", exp); break;
                    case 7: questionText = String.format("Describe Angular routing, lazy loading, and route guards (CanActivate, Resolve) for %s frontend security.", exp); break;
                    case 8: questionText = String.format("Compare Template-driven forms vs Reactive forms in Angular. How do you implement custom validation for %s forms?", exp); break;
                    case 9: questionText = String.format("What are Angular directives and pipes? How do you create a custom structural directive or pure pipe in a %s project?", exp); break;
                    case 10: default: questionText = String.format("What is Angular Standalone Components, and how do they change dependency management for %s application structures?", exp); break;
                }
            } else if (isSql) {
                switch (i) {
                    case 1: questionText = String.format("Explain the difference between inner, left, right, and full joins. How does a %s engineer decide which to use?", exp); break;
                    case 2: questionText = String.format("What is database normalization (1NF, 2NF, 3NF, BCNF)? Discuss the trade-offs of denormalization in %s databases.", exp); break;
                    case 3: questionText = String.format("Describe database indexing. How do clustered vs non-clustered indexes impact read/write performance in %s schemas?", exp); break;
                    case 4: questionText = String.format("Discuss the ACID properties of database transactions. How are they implemented in %s production environments?", exp); break;
                    case 5: questionText = String.format("What are execution plans? Explain how a %s developer profiles and optimizes a slow-running SQL query.", exp); break;
                    case 6: questionText = String.format("Explain database concurrency issues (e.g. Dirty Reads, Non-repeatable Reads, Phantom Reads) and transaction isolation levels for %s applications.", exp); break;
                    case 7: questionText = String.format("What is the difference between SQL and NoSQL databases? When should a %s architect choose one over the other?", exp); break;
                    case 8: questionText = String.format("Describe the purpose and use cases of stored procedures, triggers, and views in a %s enterprise setup.", exp); break;
                    case 9: questionText = String.format("Discuss database clustering, replication, and sharding concepts for achieving high availability in %s architectures.", exp); break;
                    case 10: default: questionText = String.format("Explain database backup, recovery, and point-in-time recovery strategies necessary for %s level data safety.", exp); break;
                }
            } else {
                switch (i) {
                    case 1: questionText = String.format("What are the core architectural components of %s that every %s professional should know?", tech, exp); break;
                    case 2: questionText = String.format("Explain memory management and resource allocation strategies in %s for %s applications.", tech, exp); break;
                    case 3: questionText = String.format("How do you handle concurrency, multithreading, or asynchronous operations in %s at a %s level?", tech, exp); break;
                    case 4: questionText = String.format("Describe the best practices for error handling, validation, and debugging in %s projects.", tech, exp); break;
                    case 5: questionText = String.format("How would you design a secure, high-performance API or component using %s suitable for %s constraints?", tech, exp); break;
                    case 6: questionText = String.format("What design patterns are most commonly applied in %s, and how do they benefit %s development?", tech, exp); break;
                    case 7: questionText = String.format("Explain how dependency injection, compilation, and runtime environments function in %s.", tech, exp); break;
                    case 8: questionText = String.format("How do you optimize performance, monitor resource leaks, and profile applications in %s for %s environments?", tech, exp); break;
                    case 9: questionText = String.format("Describe how testing frameworks, mocking libraries, and integration tests are configured in %s for %s systems.", tech, exp); break;
                    case 10: default: questionText = String.format("What are the major new features introduced in recent versions of %s, and how should a %s developer leverage them?", tech, exp); break;
                }
            }
            String mockReferenceAnswer = String.format("A comprehensive reference answer explaining key concepts of %s for question %d at %s level.", tech, i, exp);
            questions.add(new AIQuestionResponse(i, questionText, mockReferenceAnswer));
        }

        return questions;
    }
}


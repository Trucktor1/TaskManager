//package com.example.taskmanager.runner;
//
//import com.example.taskmanager.model.Task;
//import com.example.taskmanager.repository.TaskRepository;
//import com.example.taskmanager.service.TaskService;
//import com.example.taskmanager.service.TaskServiceImpl;
//import com.example.taskmanager.config.AppProperties;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.ApplicationContext;
//import org.springframework.stereotype.Component;
//import java.util.Arrays;
//
//@Component
//public class AppRunner implements CommandLineRunner {
//    private final TaskService taskService;
//    private final AppProperties appProperties;
//    private final ApplicationContext applicationContext;
//
//    public AppRunner(TaskService taskService,
//                     AppProperties appProperties,
//                     ApplicationContext applicationContext) {
//        this.taskService = taskService;
//        this.appProperties = appProperties;
//        this.applicationContext = applicationContext;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        System.out.println("\n" + "=".repeat(60));
//
//        step1Greeting();
//
//        step2AddTasks();
//
//        step3EdgeCases();
//
//        step4UpdateStatus();
//
//        step5PrototypeDemo();
//
//        step6ApplicationContextDemo();
//
//        System.out.println("=".repeat(60) + "\n");
//    }
//
//    private void step1Greeting() {
//        System.out.println("\nШаг 1: Приветствие");
//        System.out.println("Добро пожаловать в " + appProperties.getAppName() +
//                "! Лимит: " + appProperties.getMaxTasks() +
//                ". Приоритет по умолчанию: " + appProperties.getDefaultPriority());
//    }
//
//    private void step2AddTasks() {
//        System.out.println("\nШаг 2: Добавление задач");
//
//        try {
//            taskService.createTask("Низкоприоритетная задача", "Описание LOW", Task.Priority.LOW);
//            taskService.createTask("Среднеприоритетная задача", "Описание MEDIUM", Task.Priority.MEDIUM);
//            taskService.createTask("Высокоприоритетная задача", "Описание HIGH", Task.Priority.HIGH);
//
//            System.out.println("Созданы задачи:");
//            taskService.getAllTasks().forEach(task ->
//                    System.out.println("  " + task));
//        } catch (Exception e) {
//            System.err.println("Ошибка: " + e.getMessage());
//        }
//    }
//
//    private void step3EdgeCases() {
//        System.out.println("\nШаг 3: Граничные случаи");
//
//        // Пустой заголовок
//        try {
//            taskService.createTask("", "Описание", Task.Priority.LOW);
//        } catch (IllegalArgumentException e) {
//            System.err.println("Ошибка (пустой заголовок): " + e.getMessage());
//        }
//
//        // Превышение лимита
//        try {
//            for (int i = 0; i < appProperties.getMaxTasks() + 2; i++) {
//                taskService.createTask("Задача " + i, "Описание", Task.Priority.LOW);
//            }
//        } catch (IllegalStateException e) {
//            System.err.println("Ошибка (лимит задач): " + e.getMessage());
//        }
//    }
//
//    private void step4UpdateStatus() {
//        System.out.println("\nШаг 4: Изменение статусов");
//
//        var tasks = taskService.getAllTasks();
//        if (tasks.size() >= 2) {
//            taskService.updateTaskStatus(tasks.get(0).getId(), Task.Status.IN_PROGRESS);
//            taskService.updateTaskStatus(tasks.get(1).getId(), Task.Status.DONE);
//
//            System.out.println("DONE задачи:");
//            taskService.getTasksByStatus(Task.Status.DONE).forEach(task ->
//                    System.out.println("  " + task));
//
//            System.out.println("HIGH задачи:");
//            taskService.getTasksByPriority(Task.Priority.HIGH).forEach(task ->
//                    System.out.println("  " + task));
//        }
//    }
//
//    private void step5PrototypeDemo() {
//        System.out.println("\nШаг 5: Prototype и ObjectProvider");
//
//        TaskServiceImpl serviceImpl = (TaskServiceImpl) taskService;
//        serviceImpl.showStats();
//        serviceImpl.showStats();
//    }
//
//    private void step6ApplicationContextDemo() {
//        System.out.println("\nШаг 6: ApplicationContext");
//
//        TaskRepository repo = applicationContext.getBean(TaskRepository.class);
//        System.out.println("Бин TaskRepository получен вручную: " + repo.getClass().getSimpleName());
//
//        System.out.println("Всего бинов в контексте: " + applicationContext.getBeanDefinitionCount());
//
//        System.out.println("Бины, содержащие 'task':");
//        Arrays.stream(applicationContext.getBeanDefinitionNames())
//                .filter(name -> name.toLowerCase().contains("task"))
//                .forEach(name -> System.out.println("  - " + name));
//    }
//}
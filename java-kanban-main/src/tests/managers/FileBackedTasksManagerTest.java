package managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import taskss.Task;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest {
    static String file = ("C:/Users/Saske/Desktop/test/11.csv");
    Task task = new Task("Test", "test");
    FileBackedTasksManager fileBackedTasksManager;


    @AfterEach
    void clearFile() throws IOException {

        FileInputStream input = new FileInputStream("C:/Users/Saske/Desktop/test/11 (2).csv");
        FileOutputStream output = new FileOutputStream("C:/Users/Saske/Desktop/test/11.csv");

        while (input.available() > 0) {
            int data = input.read();
            output.write(data);
        }
        input.close();
        output.close();
    }

    @Test
    void shouldReturnEpicId2() {
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals(2, fileBackedTasksManager.getEpicByID(2).getId());
    }

    @Test
    void shouldAddNewTaskToFile() {
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        fileBackedTasksManager.add(task);
        assertEquals(task, fileBackedTasksManager.getTaskByID(0));
    }

    @Test
    void shouldCreateStringFromTask() {
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        assertEquals("0,TASK,Test,NEW,test,0,null", fileBackedTasksManager.toString(task));
    }

    @Test
    void shouldCreateStringFromHistory() {
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        assertNotNull(fileBackedTasksManager.getHistory());
        assertEquals(3, fileBackedTasksManager.getHistory().size());
    }

    @Test
    void shouldReturnHistoryFromString() {
        String string = "1,2,3";
        List<Integer> testList = new ArrayList<>();
        testList.add(1);
        testList.add(2);
        testList.add(3);
        assertEquals(testList, FileBackedTasksManager.historyFromString(string));


    }

    @Test
    void shouldReturn1EpicFromString() {
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(file);
        String textTask = "1,TASK,Task1,NEW,Description task1,10,2021-12-21T21:21:21";
        assertEquals(fileBackedTasksManager.getTaskByID(1), fileBackedTasksManager.taskFromString(textTask));
    }
}
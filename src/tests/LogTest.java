package tests;

import static org.junit.Assert.*;
import java.io.File;
import java.nio.file.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import domain.Log;

/**
 * Tests of the error log.
 */
public class LogTest {

    private String realName;

    /** Uses a test file so the real log is not touched. */
    @Before
    public void setUp(){
        realName = Log.name;
        Log.name = "LogTest";
        new File("LogTest.log").delete();
    }

    /** Deletes the test file and puts back the real name. */
    @After
    public void tearDown(){
        new File("LogTest.log").delete();
        Log.name = realName;
    }

    /** Should create the log file with the error. */
    @Test
    public void shouldSaveTheErrorInTheFile() throws Exception {
        Log.record(new Exception("error de prueba"));
        File file = new File("LogTest.log");
        assertTrue(file.exists());
        String text = new String(Files.readAllBytes(file.toPath()));
        assertTrue(text.contains("error de prueba"));
    }

    /** Should keep the old errors when a new one is saved. */
    @Test
    public void shouldKeepTheOldErrors() throws Exception {
        Log.record(new Exception("primer error"));
        Log.record(new Exception("segundo error"));
        String text = new String(Files.readAllBytes(new File("LogTest.log").toPath()));
        assertTrue(text.contains("primer error"));
        assertTrue(text.contains("segundo error"));
    }

    /** Should save in which class the error happened. */
    @Test
    public void shouldSaveWhereTheErrorHappened() throws Exception {
        Log.record(new Exception("error con traza"));
        String text = new String(Files.readAllBytes(new File("LogTest.log").toPath()));
        assertTrue(text.contains("LogTest"));
    }
}

package fr.lernejo.fileinjector;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class LauncherTest {
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    @BeforeEach
    public void setUpStreams() {
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setErr(originalErr);
    }

    @Test
    void main_terminates_before_5_sec() {
        assertTimeoutPreemptively(
            Duration.ofSeconds(5L),
            () -> Launcher.main(new String[]{}));
    }

    @Test
    void main_terminates_with_no_argument() {
        try {
            Launcher.main(new String[]{});
        } catch (Exception ignored) {

        }
        assertEquals("Error during reading the file. Error : Index 0 out of bounds for length 0\n", errContent.toString());
    }

    @Test
    void main_terminates_with_bad_argument() {
        try {
            Launcher.main(new String[]{"/bad/path/to/json/file.json"});
        } catch (Exception ignored) {

        }
        assertEquals("Error during reading the file. Error : The file can't be read. Error : /bad/path/to/json/file.json (No such file or directory)\n", errContent.toString());
    }

    @Test
    void main_terminates_with_rabbit_error() {
        try {
            Launcher.main(new String[]{getClass().getClassLoader().getResource("games_test.json").getFile()});
        } catch (Exception ignored) {

        }

        assertEquals("Can't send to rabbit queue : game_info. Error : java.net.ConnectException: Connection refused\n" +
            "Can't send to rabbit queue : game_info. Error : java.net.ConnectException: Connection refused\n", errContent.toString());
    }
}

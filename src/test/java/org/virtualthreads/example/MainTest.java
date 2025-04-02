package org.virtualthreads.example;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class MainTest {

    @Test
    void shouldFindValuesInDataBaseLessThan6Seconds() {
        assertTimeoutPreemptively(Duration.ofSeconds(6), () -> {
            Main.main(new String[]{"1"});
        });
    }

}

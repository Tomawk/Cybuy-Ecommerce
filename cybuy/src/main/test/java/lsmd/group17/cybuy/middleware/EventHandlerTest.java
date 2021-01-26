package lsmd.group17.cybuy.middleware;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertSame;

public class EventHandlerTest {
    @Test
    void getInstance() throws ExecutionException, InterruptedException {

        // Test the getInstance in multi-threading world
        Callable<EventHandler> callable = () -> EventHandler.getInstance();

        ExecutorService executor = Executors.newCachedThreadPool();
        final int NUM_THREAD = 5;

        List<Future<EventHandler>> futureList = new ArrayList<>();
        for (int i = 0; i < NUM_THREAD; i++) {
            futureList.add(executor.submit(callable));
        }

        List<EventHandler> eventHandlers = new ArrayList<>();
        for (int i = 0; i< NUM_THREAD; i++)
        {
            eventHandlers.add(futureList.get(i).get());
        }

        eventHandlers.stream().parallel()
                .forEach(eventHandler -> assertSame(eventHandler, EventHandler.getInstance()));

        executor.shutdown();
    }
}

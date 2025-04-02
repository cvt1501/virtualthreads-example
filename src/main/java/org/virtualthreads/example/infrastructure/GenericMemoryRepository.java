package org.virtualthreads.example.infrastructure;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class GenericMemoryRepository<T> {

    private static final Map<Class<?>, GenericMemoryRepository<?>> instances = new ConcurrentHashMap<>();

    private final Map<String, T> data = new ConcurrentHashMap<>();


    private GenericMemoryRepository() {}

    @SuppressWarnings("unchecked")
    public static synchronized <T> GenericMemoryRepository<T> getInstance(Class<T> clazz) {
        return (GenericMemoryRepository<T>) instances.computeIfAbsent(clazz, c -> new GenericMemoryRepository<>());
    }

    public Optional<T> findById(final String id) {
        this.simulateDelay();

        return Optional.ofNullable(this.data.get(id));
    }

    public void save(final String id, final T object) {
        this.data.put(id, object);
    }

    private void simulateDelay() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}

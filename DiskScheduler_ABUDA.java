// DiskScheduler.java
import java.util.List;

public interface DiskScheduler_ABUDA {
    enum Direction {
        LEFT,  // Toward lower-numbered cylinders (0)
        RIGHT  // Toward higher-numbered cylinders (diskSize - 1)
    }

    DiskScheduleResult_ABUDA schedule(List<Integer> requests, int initialHead, int diskSize, Direction direction);
}
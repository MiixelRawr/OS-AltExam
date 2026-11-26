import java.util.ArrayList;
import java.util.List;

public class FCFSDiskScheduler {
    public static final int DEFAULT_DISK_SIZE = 200;

    public FCFSDiskScheduleResult schedule(int initialHead,
                                           List<Integer> requests,
                                           int diskSize) {
        if (diskSize <= 0) {
            throw new IllegalArgumentException("Disk size must be greater than zero.");
        }

        if (initialHead < 0 || initialHead >= diskSize) {
            throw new IllegalArgumentException("Initial head must be between 0 and " + (diskSize - 1) + ".");
        }

        if (requests == null) {
            throw new IllegalArgumentException("Request list is missing.");
        }

        List<Integer> serviceOrder = new ArrayList<Integer>();
        List<Integer> seekPath = new ArrayList<Integer>();
        List<Integer> movements = new ArrayList<Integer>();

        int currentHead = initialHead;
        long totalMovement = 0;

        seekPath.add(initialHead);

        // FCFS follows the request list from first to last.
        for (int i = 0; i < requests.size(); i++) {
            int request = requests.get(i);

            if (request < 0 || request >= diskSize) {
                throw new IllegalArgumentException(
                    "Request " + request + " must be between 0 and " + (diskSize - 1) + ".");
            }

            int movement = Math.abs(request - currentHead);

            serviceOrder.add(request);
            movements.add(movement);
            seekPath.add(request);

            totalMovement += movement;
            currentHead = request;
        }

        double average = 0;
        if (!serviceOrder.isEmpty()) {
            average = (double) totalMovement / serviceOrder.size();
        }

        return new FCFSDiskScheduleResult(
            serviceOrder, seekPath, movements, totalMovement, average);
    }
}

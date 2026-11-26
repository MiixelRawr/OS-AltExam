import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FCFSTest {
    private static int passed = 0;

    public static void main(String[] args) {
        FCFSDiskScheduler scheduler = new FCFSDiskScheduler();

        check(scheduler, "demo sample", 73,
            Arrays.asList(33, 93, 53, 113, 63, 13), 200, 300);

        FCFSDiskScheduleResult demo = scheduler.schedule(
            73, Arrays.asList(33, 93, 53, 113, 63, 13), 200);
        if (!demo.getMovements().equals(Arrays.asList(40, 60, 40, 60, 50, 50))
                || demo.getAverageMovement() != 50.0) {
            throw new AssertionError("Demo movement or average calculation failed.");
        }

        check(scheduler, "increasing requests", 10,
            Arrays.asList(20, 30, 40), 200, 30);

        check(scheduler, "decreasing requests", 90,
            Arrays.asList(70, 50, 20), 200, 70);

        check(scheduler, "duplicate requests", 40,
            Arrays.asList(40, 40, 10), 200, 30);

        check(scheduler, "boundary requests", 0,
            Arrays.asList(199, 0), 200, 398);

        check(scheduler, "20 requests", 50,
            Arrays.asList(5, 190, 12, 180, 25, 170, 35, 160, 45, 150,
                55, 140, 65, 130, 75, 120, 85, 110, 95, 100), 200, 1856);

        FCFSDiskScheduleResult empty =
            scheduler.schedule(25, Collections.<Integer>emptyList(), 200);
        if (empty.getTotalMovement() == 0 && empty.getAverageMovement() == 0) {
            pass("empty queue");
        } else {
            throw new AssertionError("Empty queue test failed.");
        }

        invalid(scheduler, -1, Arrays.asList(10), 200, "negative head");
        invalid(scheduler, 10, Arrays.asList(200), 200, "request out of range");
        invalid(scheduler, 10, Arrays.asList(20), 0, "zero disk size");

        System.out.println("All " + passed + " FCFS tests passed.");
    }

    private static void check(FCFSDiskScheduler scheduler, String name,
                              int head, List<Integer> requests, int size,
                              long expectedTotal) {
        FCFSDiskScheduleResult result = scheduler.schedule(head, requests, size);

        if (result.getTotalMovement() != expectedTotal
                || !result.getServiceOrder().equals(requests)) {
            throw new AssertionError(name + " failed.");
        }

        pass(name);
    }

    private static void invalid(FCFSDiskScheduler scheduler, int head,
                                List<Integer> requests, int size, String name) {
        try {
            scheduler.schedule(head, requests, size);
            throw new AssertionError(name + " should be rejected.");
        } catch (IllegalArgumentException ex) {
            pass("validation - " + name);
        }
    }

    private static void pass(String name) {
        passed++;
        System.out.println("PASS: " + name);
    }
}

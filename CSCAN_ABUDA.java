/**
 * ICS26012 - Operating Systems
 * Module: Disk Scheduling - Circular SCAN (C-SCAN)
 * Author: [Your Name Here]
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CSCAN_ABUDA implements DiskScheduler_ABUDA {

    @Override
    public DiskScheduleResult_ABUDA schedule(List<Integer> requests,
                                       int initialHead,
                                       int diskSize,
                                       Direction direction) {
        // 1. Input validation
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Request queue cannot be empty.");
        }
        if (diskSize <= 1) {
            throw new IllegalArgumentException("Disk size must be greater than 1.");
        }
        int maxCylinder = diskSize - 1;
        if (initialHead < 0 || initialHead > maxCylinder) {
            throw new IllegalArgumentException(
                "Initial head must be between 0 and " + maxCylinder + ".");
        }
        if (direction == null) {
            throw new IllegalArgumentException("Direction must be LEFT or RIGHT.");
        }

        // 2. Partition requests relative to initialHead
        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();

        for (Integer cylinder : requests) {
            if (cylinder == null || cylinder < 0 || cylinder > maxCylinder) {
                throw new IllegalArgumentException(
                    "Invalid cylinder request: " + cylinder +
                    ". Must be between 0 and " + maxCylinder + ".");
            }
            if (cylinder < initialHead) {
                left.add(cylinder);
            } else if (cylinder > initialHead) {
                right.add(cylinder);
            } else {
                // Request at exact initialHead position (0 movement)
                if (direction == Direction.RIGHT) {
                    right.add(cylinder);
                } else {
                    left.add(cylinder);
                }
            }
        }

        // 3. Sort both partitions in ascending order
        Collections.sort(left);
        Collections.sort(right);

        List<Integer> seekSequence = new ArrayList<>();
        List<Integer> stepMovements = new ArrayList<>();
        int totalMovement = 0;
        int currentHead = initialHead;

        seekSequence.add(currentHead);

        // 4. Service requests based on initial direction
        if (direction == Direction.RIGHT) {
            // Service higher-numbered cylinders
            for (int cylinder : right) {
                int dist = Math.abs(cylinder - currentHead);
                totalMovement += dist;
                stepMovements.add(dist);
                seekSequence.add(cylinder);
                currentHead = cylinder;
            }

            // Wrap around only if there are remaining requests on the left
            if (!left.isEmpty()) {
                if (currentHead != maxCylinder) {
                    int distToEnd = Math.abs(maxCylinder - currentHead);
                    totalMovement += distToEnd;
                    stepMovements.add(distToEnd);
                    seekSequence.add(maxCylinder);
                    currentHead = maxCylinder;
                }

                // Jump back to cylinder 0 (counted in total movement)
                int jumpDist = Math.abs(0 - currentHead);
                totalMovement += jumpDist;
                stepMovements.add(jumpDist);
                seekSequence.add(0);
                currentHead = 0;

                // Continue servicing remaining requests in ascending order
                for (int cylinder : left) {
                    int dist = Math.abs(cylinder - currentHead);
                    totalMovement += dist;
                    stepMovements.add(dist);
                    seekSequence.add(cylinder);
                    currentHead = cylinder;
                }
            }
        } else { // Direction.LEFT
            // Service lower-numbered cylinders in descending order
            Collections.reverse(left);
            for (int cylinder : left) {
                int dist = Math.abs(cylinder - currentHead);
                totalMovement += dist;
                stepMovements.add(dist);
                seekSequence.add(cylinder);
                currentHead = cylinder;
            }

            // Wrap around only if there are remaining requests on the right
            if (!right.isEmpty()) {
                if (currentHead != 0) {
                    int distToStart = Math.abs(0 - currentHead);
                    totalMovement += distToStart;
                    stepMovements.add(distToStart);
                    seekSequence.add(0);
                    currentHead = 0;
                }

                // Jump back to maxCylinder (counted in total movement)
                int jumpDist = Math.abs(maxCylinder - currentHead);
                totalMovement += jumpDist;
                stepMovements.add(jumpDist);
                seekSequence.add(maxCylinder);
                currentHead = maxCylinder;

                // Continue servicing remaining requests in descending order
                Collections.reverse(right);
                for (int cylinder : right) {
                    int dist = Math.abs(cylinder - currentHead);
                    totalMovement += dist;
                    stepMovements.add(dist);
                    seekSequence.add(cylinder);
                    currentHead = cylinder;
                }
            }
        }

        double averageSeek = (double) totalMovement / requests.size();
        return new DiskScheduleResult_ABUDA(seekSequence, stepMovements, totalMovement, averageSeek);
    }

    // Quick standalone verification against the lecture sample dataset
    public static void main(String[] args) {
        CSCAN_ABUDA cscan = new CSCAN_ABUDA();
        List<Integer> sampleQueue = Arrays.asList(98, 183, 37, 122, 14, 124, 65, 67);
        DiskScheduleResult_ABUDA result = cscan.schedule(sampleQueue, 53, 200, Direction.RIGHT);

        System.out.println("Seek Sequence:   " + result.getSeekSequence());
        System.out.println("Step Movements:  " + result.getStepMovements());
        System.out.println("Total Movement:  " + result.getTotalHeadMovement());
        System.out.printf("Average Seek:    %.2f%n", result.getAverageSeekDistance());
    }
}
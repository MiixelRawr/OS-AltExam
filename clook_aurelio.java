package src;

import java.util.ArrayList;
import java.util.Collections;

public class clook_aurelio {

    // Performs the C-LOOK disk scheduling algorithm
    public static ArrayList<Integer> schedule(
            ArrayList<Integer> requests,
            int head,
            String direction) {

        ArrayList<Integer> higher = new ArrayList<>();
        ArrayList<Integer> lower = new ArrayList<>();

        // Separate requests based on the current head position
        for (int request : requests) {

            if (request >= head) {
                higher.add(request);
            } else {
                lower.add(request);
            }
        }

        // Sort the requests
        Collections.sort(higher);
        Collections.sort(lower);

        ArrayList<Integer> sequence = new ArrayList<>();

        // Add the starting head position
        sequence.add(head);

        // Moving UP
        if (direction.equalsIgnoreCase("UP")) {

            sequence.addAll(higher);
            sequence.addAll(lower);

        }

        // Moving DOWN
        else if (direction.equalsIgnoreCase("DOWN")) {

            Collections.reverse(lower);
            Collections.reverse(higher);

            sequence.addAll(lower);
            sequence.addAll(higher);

        }

        return sequence;
    }

    // Calculate total head movement
    public static int calculateTotalMovement(
            ArrayList<Integer> sequence) {

        int total = 0;

        for (int i = 1; i < sequence.size(); i++) {

            total += Math.abs(
                    sequence.get(i) - sequence.get(i - 1)
            );
        }

        return total;
    }

    // Calculate average seek distance
    public static double calculateAverageSeek(
            int totalMovement, int numberOfRequests) {

        if (numberOfRequests == 0) {
            return 0;
        }

        return (double) totalMovement / numberOfRequests;
    }
}

package src;

import java.util.ArrayList;
import java.util.Collections;

public class clook {

    public static ArrayList<Integer> schedule(
            ArrayList<Integer> requests,
            int head,
            String direction) {

        ArrayList<Integer> higher = new ArrayList<>();
        ArrayList<Integer> lower = new ArrayList<>();

        for (int request : requests) {

            if (request >= head) {
                higher.add(request);
            } else {
                lower.add(request);
            }
        }

        Collections.sort(higher);
        Collections.sort(lower);

        ArrayList<Integer> sequence = new ArrayList<>();

        sequence.add(head);

        if (direction.equalsIgnoreCase("UP")) {

            sequence.addAll(higher);
            sequence.addAll(lower);

        }

        else if (direction.equalsIgnoreCase("DOWN")) {

            Collections.reverse(lower);
            Collections.reverse(higher);

            sequence.addAll(lower);
            sequence.addAll(higher);

        }

        return sequence;
    }

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

    public static double calculateAverageSeek(
            int totalMovement, int numberOfRequests) {

        if (numberOfRequests == 0) {
            return 0;
        }

        return (double) totalMovement / numberOfRequests;
    }
}

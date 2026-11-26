package src;

import java.util.ArrayList;

public class main {

    public static void main(String[] args) {

        // Sample disk requests
        ArrayList<Integer> requests = new ArrayList<>();

        requests.add(98);
        requests.add(183);
        requests.add(37);
        requests.add(122);
        requests.add(14);
        requests.add(124);
        requests.add(65);
        requests.add(67);

        // Initial head position
        int head = 53;

        // Initial direction
        String direction = "UP";

        // Run C-LOOK algorithm
        ArrayList<Integer> sequence =
                clook.schedule(requests, head, direction);

        // Calculate total movement
        int totalMovement =
                clook.calculateTotalMovement(sequence);

        // Calculate average seek distance
        double averageSeek =
                clook.calculateAverageSeek(
                        totalMovement, requests.size());

        // Display results
        System.out.println("C-LOOK DISK SCHEDULING");
        System.out.println("Seek Sequence: " + sequence);
        System.out.println("Total Head Movement: " + totalMovement);
        System.out.println("Average Seek Distance: " + averageSeek);
    }
}
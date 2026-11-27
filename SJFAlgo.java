
import java.util.ArrayList;
import java.util.Scanner;

// CIAR, JADE ANNE DENISE C.
//3ITE
//SHORTEST JOB FIRST - NON PREEMPTIVE

public class SJFAlgo {

    // process information
    public static class Process {
        public int pid, arrival, burst;
        public int ct, tat, wt, rt;
        public boolean completed = false;

        public Process(int pid, int arrival, int burst) {
            this.pid = pid;
            this.arrival = arrival;
            this.burst = burst;
        }
    }

    // gantt chart 
    public static class Segment {
        public String name;
        public int start, end;

        public Segment(String name, int start, int end) {
            this.name = name;
            this.start = start;
            this.end = end;
        }
    }

    // results
    public static class Result {
        public ArrayList<Process> processes;
        public ArrayList<Segment> gantt;

        public double avgWT, avgTAT, avgRT;
        public double cpuUtilization, throughput;
        public int totalTime;

        public Result(ArrayList<Process> p, ArrayList<Segment> g) {
            processes = p;
            gantt = g;
        }
    }

    // main process of algo
    public static Result runSJF(ArrayList<Process> processes) {

        if (processes == null) {
            throw new IllegalArgumentException("Process list cannot be null");
        }

        ArrayList<Segment> gantt = new ArrayList<>();

        int currentTime = 0;
        int completed = 0;

        double totalWT = 0;
        double totalTAT = 0;
        double totalRT = 0;
        int totalBurst = 0;

        for (Process p : processes) {
            if (p == null) {
                throw new IllegalArgumentException("Process list cannot contain null entries");
            }
            p.completed = false;
            p.ct = 0;
            p.tat = 0;
            p.wt = 0;
            p.rt = 0;
        }

        if (processes.isEmpty()) {
            return new Result(processes, gantt);
        }

        while (completed < processes.size()) {

            Process shortest = null;

            for (Process p : processes) {

                if (p.completed == false &&
                    p.arrival <= currentTime) {

                    if (shortest == null || p.burst < shortest.burst ||
                        (p.burst == shortest.burst && p.arrival < shortest.arrival) ||
                        (p.burst == shortest.burst && p.arrival == shortest.arrival && p.pid < shortest.pid)) {

                        shortest = p;
                    }
                }
            }

            if (shortest == null) {

                int nextArrival = Integer.MAX_VALUE;

                for (Process p : processes) {

                    if (p.completed == false &&
                        p.arrival < nextArrival) {

                        nextArrival = p.arrival;
                    }
                }

                gantt.add(new Segment(
                    "IDLE", currentTime, nextArrival));

                currentTime = nextArrival;
            }

            else {

                int startTime = currentTime;
                shortest.ct = currentTime + shortest.burst;
                shortest.tat = shortest.ct - shortest.arrival;
                shortest.wt = shortest.tat - shortest.burst;
                shortest.rt = startTime - shortest.arrival;

                // add process to Gantt Chart
                gantt.add(new Segment(
                    "P" + shortest.pid,
                    startTime,
                    shortest.ct
                ));

                currentTime = shortest.ct;

                shortest.completed = true;
                completed++;

                // adding
                totalWT += shortest.wt;
                totalTAT += shortest.tat;
                totalRT += shortest.rt;
                totalBurst += shortest.burst;
            }
        }

        // aves
        int n = processes.size();

        Result result = new Result(processes, gantt);

        result.avgWT = totalWT / n;
        result.avgTAT = totalTAT / n;
        result.avgRT = totalRT / n;

        result.totalTime = currentTime;

        // cpu utilization
        result.cpuUtilization =
            (totalBurst / (double) currentTime) * 100;

        // throughput
        result.throughput =
            n / (double) currentTime;

        return result;
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int processCount;

        // user inputs
        do {
            System.out.print("Enter the number of processes (minimum 10): ");

            while (!input.hasNextInt()) {
                System.out.print("Please enter a valid integer: ");
                input.next();
            }

            processCount = input.nextInt();

            if (processCount < 10) {
                System.out.println("You must enter at least 10 processes.");
            }
        } while (processCount < 10);

        ArrayList<Process> processes = new ArrayList<>();

        for (int i = 1; i <= processCount; i++) {
            System.out.println("\nEnter details for Process " + i + ":");

            int arrival;
            do {
                System.out.print("Enter Arrival Time for P" + i + ": ");

                while (!input.hasNextInt()) {
                    System.out.print("Please enter a valid Arrival Time: ");
                    input.next();
                }

                arrival = input.nextInt();

                if (arrival < 0) {
                    System.out.println("Arrival time cannot be negative.");
                }
            } while (arrival < 0);

            int burst;
            do {
                System.out.print("Enter Burst Time for P" + i + ": ");

                while (!input.hasNextInt()) {
                    System.out.print("Please enter a valid Burst Time: ");
                    input.next();
                }

                burst = input.nextInt();

                if (burst <= 0) {
                    System.out.println("Burst time must be greater than zero.");
                }
            } while (burst <= 0);

            processes.add(new Process(i, arrival, burst));
        }

        Result result = runSJF(processes);

        // display
        System.out.println("\n========== GANTT CHART ==========");
        for (Segment segment : result.gantt) {
            System.out.println(
                segment.name + " [" + segment.start + "-" + segment.end + "]"
            );
        }

        System.out.println("\n========== SJF RESULTS ==========");
        for (Process p : result.processes) {
            System.out.println("\nProcess ID: P" + p.pid);
            System.out.println("Completion Time: " + p.ct);
            System.out.println("Turnaround Time: " + p.tat);
            System.out.println("Waiting Time: " + p.wt);
            System.out.println("Response Time: " + p.rt);
        }

        System.out.println("\n========== PERFORMANCE METRICS ==========");
        System.out.println("Average Waiting Time: " + result.avgWT);
        System.out.println("Average Turnaround Time: " + result.avgTAT);
        System.out.println("Average Response Time: " + result.avgRT);
        System.out.println("CPU Utilization: " + result.cpuUtilization + "%");
        System.out.printf("Throughput: %.2f%n", result.throughput);

        input.close();
    }
}
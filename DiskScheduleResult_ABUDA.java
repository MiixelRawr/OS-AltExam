// DiskScheduleResult.java
import java.util.Collections;
import java.util.List;

public class DiskScheduleResult_ABUDA {
    private final List<Integer> seekSequence;
    private final List<Integer> stepMovements;
    private final int totalHeadMovement;
    private final double averageSeekDistance;

    public DiskScheduleResult_ABUDA(List<Integer> seekSequence,
                              List<Integer> stepMovements,
                              int totalHeadMovement,
                              double averageSeekDistance) {
        this.seekSequence = Collections.unmodifiableList(seekSequence);
        this.stepMovements = Collections.unmodifiableList(stepMovements);
        this.totalHeadMovement = totalHeadMovement;
        this.averageSeekDistance = averageSeekDistance;
    }

    public List<Integer> getSeekSequence() { return seekSequence; }
    public List<Integer> getStepMovements() { return stepMovements; }
    public int getTotalHeadMovement() { return totalHeadMovement; }
    public double getAverageSeekDistance() { return averageSeekDistance; }
}
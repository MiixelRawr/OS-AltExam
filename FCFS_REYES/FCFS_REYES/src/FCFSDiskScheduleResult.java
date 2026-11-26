import java.util.List;

public class FCFSDiskScheduleResult {
    private List<Integer> serviceOrder;
    private List<Integer> seekPath;
    private List<Integer> movements;
    private long totalMovement;
    private double averageMovement;

    public FCFSDiskScheduleResult(List<Integer> serviceOrder,
                                  List<Integer> seekPath,
                                  List<Integer> movements,
                                  long totalMovement,
                                  double averageMovement) {
        this.serviceOrder = serviceOrder;
        this.seekPath = seekPath;
        this.movements = movements;
        this.totalMovement = totalMovement;
        this.averageMovement = averageMovement;
    }

    public List<Integer> getServiceOrder() {
        return serviceOrder;
    }

    public List<Integer> getSeekPath() {
        return seekPath;
    }

    public List<Integer> getMovements() {
        return movements;
    }

    public long getTotalMovement() {
        return totalMovement;
    }

    public double getAverageMovement() {
        return averageMovement;
    }
}

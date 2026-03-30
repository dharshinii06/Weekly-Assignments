import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

class ParkingSpot {

    enum Status { EMPTY, OCCUPIED, DELETED }

    String licensePlate;
    Status status;
    LocalDateTime entryTime;

    public ParkingSpot() {
        status = Status.EMPTY;
    }
}

class ParkingLot {

    private ParkingSpot[] spots;
    private int capacity;
    private int totalProbes = 0;
    private int totalParkedVehicles = 0;

    // Track peak hours
    private Map<Integer, Integer> hourOccupancy;

    public ParkingLot(int capacity) {
        this.capacity = capacity;
        spots = new ParkingSpot[capacity];
        for (int i = 0; i < capacity; i++) {
            spots[i] = new ParkingSpot();
        }
        hourOccupancy = new HashMap<>();
    }

    // Simple hash function based on license plate
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    // Park a vehicle
    public void parkVehicle(String licensePlate) {

        int index = hash(licensePlate);
        int probes = 0;

        while (spots[index].status == ParkingSpot.Status.OCCUPIED) {
            probes++;
            index = (index + 1) % capacity;
        }

        spots[index].licensePlate = licensePlate;
        spots[index].status = ParkingSpot.Status.OCCUPIED;
        spots[index].entryTime = LocalDateTime.now();

        totalProbes += probes;
        totalParkedVehicles++;

        int currentHour = spots[index].entryTime.getHour();
        hourOccupancy.put(currentHour, hourOccupancy.getOrDefault(currentHour, 0) + 1);

        System.out.println("Vehicle " + licensePlate +
                " parked at spot #" + index +
                " (" + probes + " probes)");
    }

    // Exit vehicle
    public void exitVehicle(String licensePlate) {

        int index = hash(licensePlate);
        int probes = 0;

        while (spots[index].status != ParkingSpot.Status.EMPTY) {

            if (spots[index].status == ParkingSpot.Status.OCCUPIED &&
                    spots[index].licensePlate.equals(licensePlate)) {

                LocalDateTime exitTime = LocalDateTime.now();
                Duration duration = Duration.between(spots[index].entryTime, exitTime);

                double fee = calculateFee(duration);

                spots[index].status = ParkingSpot.Status.EMPTY;
                spots[index].licensePlate = null;
                spots[index].entryTime = null;

                System.out.println("Vehicle " + licensePlate +
                        " exited from spot #" + index +
                        ", Duration: " + formatDuration(duration) +
                        ", Fee: $" + String.format("%.2f", fee) +
                        " (" + probes + " probes)");

                return;
            }

            probes++;
            index = (index + 1) % capacity;
        }

        System.out.println("Vehicle " + licensePlate + " not found.");
    }

    private double calculateFee(Duration duration) {
        double hours = duration.toMinutes() / 60.0;
        return Math.ceil(hours) * 5.0; // $5 per hour
    }

    private String formatDuration(Duration duration) {
        long h = duration.toHours();
        long m = duration.toMinutes() % 60;
        return h + "h " + m + "m";
    }

    // Get parking statistics
    public void getStatistics() {

        long occupied = Arrays.stream(spots)
                .filter(s -> s.status == ParkingSpot.Status.OCCUPIED)
                .count();

        double occupancy = (occupied * 100.0) / capacity;
        double avgProbes = totalParkedVehicles == 0 ? 0 : (totalProbes * 1.0) / totalParkedVehicles;

        int peakHour = hourOccupancy.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(-1);

        System.out.println("\n--- Parking Lot Statistics ---");
        System.out.println("Occupancy: " + String.format("%.2f", occupancy) + "%");
        System.out.println("Average Probes: " + String.format("%.2f", avgProbes));
        if (peakHour != -1) {
            System.out.println("Peak Hour: " + peakHour + ":00 - " + (peakHour + 1) + ":00");
        }
        System.out.println("-------------------------------\n");
    }
}

public class ParkingLotTest {

    public static void main(String[] args) throws InterruptedException {

        ParkingLot lot = new ParkingLot(500);

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        Thread.sleep(2000); // simulate parking duration

        lot.exitVehicle("ABC-1234");
        lot.exitVehicle("XYZ-9999");

        lot.getStatistics();
    }
}
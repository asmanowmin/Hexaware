import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

class Employee {
    String employeeId;
    String name;
    String email;
    String password;
    boolean profileVerified;
    String role;
    List<String> feedback;

    public Employee(String employeeId, String name, String email, String password, String role) {
        this.employeeId = employeeId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.profileVerified = false;
        this.role = role;
        this.feedback = new ArrayList<>();
    }

    public void addFeedback(String feedback) {
        this.feedback.add(feedback);
    }
}

class RideShareApplication {
    List<Employee> employees;
    List<Ride> rides;
    List<String> notifications;

    public RideShareApplication() {
        this.employees = new ArrayList<>();
        this.rides = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    public void registerEmployee(String employeeId, String name, String email, String password, String role) {
        Employee employee = findEmployeeById(employeeId);
        if (employee == null) {
            employee = new Employee(employeeId, name, email, password, role);
            this.employees.add(employee);
            System.out.println("Employee " + name + " registered successfully as " + role + ".");
        } else {
            System.out.println("Employee ID " + employeeId + " already exists.");
        }
    }

    public Employee findEmployeeById(String employeeId) {
        for (Employee employee : employees) {
            if (employee.employeeId.equals(employeeId)) {
                return employee;
            }
        }
        return null;
    }

    public boolean authenticateUser(String employeeId, String password) {
        Employee employee = findEmployeeById(employeeId);
        if (employee != null && employee.password.equals(password)) {
            System.out.println("Welcome " + employee.name + "! You are logged in as " + employee.role + ".");
            return true;
        } else {
            System.out.println("Authentication failed. Please check your credentials.");
            return false;
        }
    }

    public void verifyProfile(String employeeId) {
        Employee employee = findEmployeeById(employeeId);
        if (employee != null) {
            employee.profileVerified = true;
            System.out.println("Profile for " + employee.name + " has been verified.");
        } else {
            System.out.println("Employee not found.");
        }
    }

    public void postRide(String employeeId, String origin, String destination, Date travelTime, int availableSeats, String vehicleNo) {
        Employee employee = findEmployeeById(employeeId);
        if (employee == null || !employee.role.equals("Driver")) {
            System.out.println("Only Drivers can post ride details.");
            return;
        }

        Ride ride = new Ride(rides.size() + 1, employeeId, origin, destination, travelTime, availableSeats, vehicleNo, employee.name);
        this.rides.add(ride);
        System.out.println("Ride posted by " + employee.name + " from " + origin + " to " + destination + " at " + travelTime + ".");
    }

    public void searchRides(String origin, String destination) {
        boolean found = false;
        System.out.println("Available rides from " + origin + " to " + destination + ":");
        for (Ride ride : rides) {
            if (ride.origin.equalsIgnoreCase(origin) && ride.destination.equalsIgnoreCase(destination)) {
                System.out.println("Ride ID: " + ride.rideId + ", Driver: " + ride.driver + ", Time: " + ride.travelTime + ", Vehicle No: " + ride.vehicleNo + ", Cost: INR " + ride.cost);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No available rides found.");
        }
    }

    public boolean requestRide(int rideId, String employeeId) {
        Ride ride = findRideById(rideId);
        Employee employee = findEmployeeById(employeeId);

        if (ride == null || employee == null || !employee.role.equals("Rider")) {
            System.out.println("Invalid ride ID or employee role.");
            return false;
        }

        if (ride.availableSeats > 0) {
            ride.matchedRiders.add(employee.name);
            ride.availableSeats -= 1;
            System.out.println("Ride request from " + employee.name + " has been accepted.");
            this.notifications.add("Ride confirmed for " + employee.name + " with driver " + ride.driver + ".");
            return true;
        } else {
            System.out.println("No available seats for this ride.");
            return false;
        }
    }

    public void confirmRideDetails(int rideId) {
        Ride ride = findRideById(rideId);
        if (ride != null) {
            System.out.println("Ride Details: \nDriver: " + ride.driver + "\nRoute: " + ride.origin + " to " + ride.destination + "\nTime: " + ride.travelTime + "\nCost: INR " + ride.cost);
        } else {
            System.out.println("Ride not found.");
        }
    }

    public void processPayment(int rideId) {
        Ride ride = findRideById(rideId);
        if (ride != null) {
            double totalCost = ride.cost;
            double splitCost = totalCost / (ride.matchedRiders.size() + 1);
            System.out.println("Total cost: INR " + totalCost + ". Each rider pays: INR " + splitCost);
        } else {
            System.out.println("Ride not found.");
        }
    }

    public void collectFeedback(String employeeId, String feedback) {
        Employee employee = findEmployeeById(employeeId);
        if (employee != null) {
            employee.addFeedback(feedback);
            System.out.println("Feedback received from " + employee.name + ": " + feedback);
        } else {
            System.out.println("Employee not found.");
        }
    }

    public void showRideHistory(String employeeId) {
        Employee employee = findEmployeeById(employeeId);
        if (employee != null) {
            System.out.println("Ride History for " + employee.name + ":");
            for (Ride ride : rides) {
                if (ride.matchedRiders.contains(employee.name)) {
                    System.out.println("Ride ID: " + ride.rideId + ", From: " + ride.origin + ", To: " + ride.destination + ", Time: " + ride.travelTime);
                }
            }
        } else {
            System.out.println("Employee not found.");
        }
    }

    public void cancelRide(int rideId, String employeeId) {
        Ride ride = findRideById(rideId);
        Employee employee = findEmployeeById(employeeId);

        if (ride == null || employee == null || !employee.role.equals("Rider")) {
            System.out.println("Invalid ride ID or employee role.");
            return;
        }

        if (ride.matchedRiders.remove(employee.name)) {
            ride.availableSeats += 1;
            System.out.println("Ride cancellation successful.");
        } else {
            System.out.println("No matching ride found for cancellation.");
        }
    }

    public void rescheduleRide(int rideId, Date newTime) {
        Ride ride = findRideById(rideId);
        if (ride != null) {
            ride.travelTime = newTime;
            System.out.println("Ride rescheduled to: " + newTime);
        } else {
            System.out.println("Ride not found.");
        }
    }

    public void displayNotifications() {
        for (String notification : notifications) {
            System.out.println("Notification: " + notification);
        }
        notifications.clear();
    }

    private Ride findRideById(int rideId) {
        for (Ride ride : rides) {
            if (ride.rideId == rideId) {
                return ride;
            }
        }
        return null;
    }
}

class Ride {
    int rideId;
    String driverId;
    String origin;
    String destination;
    Date travelTime;
    int availableSeats;
    String vehicleNo;
    String driver;
    List<String> matchedRiders;
    double cost;

    public Ride(int rideId, String driverId, String origin, String destination, Date travelTime, int availableSeats, String vehicleNo, String driver) {
        this.rideId = rideId;
        this.driverId = driverId;
        this.origin = origin;
        this.destination = destination;
        this.travelTime = travelTime;
        this.availableSeats = availableSeats;
        this.vehicleNo = vehicleNo;
        this.driver = driver;
        this.matchedRiders = new ArrayList<>();
        this.cost = 300 + Math.random() * 1200; // Sample cost calculation in INR
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RideShareApplication app = new RideShareApplication();

        // Register employees
        app.registerEmployee("101", "Alisha Banu", "alishabanu@gmail.com", "password123", "Driver");
        app.registerEmployee("102", "Asma Nowmin", "asmanowminAgmail.com", "password123", "Rider");

        // Main application loop
        while (true) {
            System.out.println("Are you a Rider or Driver? (Enter 'Rider' or 'Driver')");
            String role = scanner.nextLine();

            if (!role.equalsIgnoreCase("Rider") && !role.equalsIgnoreCase("Driver")) {
                System.out.println("Invalid role. Please enter 'Rider' or 'Driver'.");
                continue;
            }

            System.out.println("Enter your Employee ID:");
            String employeeId = scanner.nextLine();
            System.out.println("Enter your password:");
            String password = scanner.nextLine();

            if (!app.authenticateUser(employeeId, password)) {
                System.out.println("Authentication failed.");
                continue;
            }

            if (role.equalsIgnoreCase("Driver")) {
                System.out.println("Enter the origin of the ride:");
                String origin = scanner.nextLine();
                System.out.println("Enter the destination of the ride:");
                String destination = scanner.nextLine();
                System.out.println("Enter the number of available seats:");
                int availableSeats = scanner.nextInt();
                scanner.nextLine(); // consume newline
                System.out.println("Enter the vehicle number:");
                String vehicleNo = scanner.nextLine();
                Date travelTime = new Date(); // Current time for simplicity

                app.postRide(employeeId, origin, destination, travelTime, availableSeats, vehicleNo);
            } else if (role.equalsIgnoreCase("Rider")) {
                System.out.println("Enter the origin location:");
                String origin = scanner.nextLine();
                System.out.println("Enter the destination location:");
                String destination = scanner.nextLine();

                app.searchRides(origin, destination);

                System.out.println("Enter the Ride ID you want to request:");
                int rideId = scanner.nextInt();
                scanner.nextLine(); // consume newline

                if (app.requestRide(rideId, employeeId)) {
                    app.confirmRideDetails(rideId);
                    app.processPayment(rideId);

                    System.out.println("Please provide feedback for the ride:");
                    String feedback = scanner.nextLine();
                    app.collectFeedback(employeeId, feedback);

                    app.showRideHistory(employeeId);

                    System.out.println("Do you want to cancel any ride? (yes/no)");
                    String cancel = scanner.nextLine();
                    if (cancel.equalsIgnoreCase("yes")) {
                        System.out.println("Enter the Ride ID to cancel:");
                        int cancelRideId = scanner.nextInt();
                        scanner.nextLine(); // consume newline
                        app.cancelRide(cancelRideId, employeeId);
                    }

                    System.out.println("Do you want to reschedule any ride? (yes/no)");
                    String reschedule = scanner.nextLine();
                    if (reschedule.equalsIgnoreCase("yes")) {
                        System.out.println("Enter the Ride ID to reschedule:");
                        int rescheduleRideId = scanner.nextInt();
                        scanner.nextLine(); // consume newline
                        System.out.println("Enter new date and time (e.g., 'Wed Aug 28 10:00:00 IST 2024'):");
                        String newDateTime = scanner.nextLine();
                        Date newTime = new Date(newDateTime);
                        app.rescheduleRide(rescheduleRideId, newTime);
                    }

                    app.displayNotifications();
                } else {
                    System.out.println("Ride request failed.");
                }
            }

            System.out.println("=== Session Ended. Please Run the code again ===");
        }
    }
}

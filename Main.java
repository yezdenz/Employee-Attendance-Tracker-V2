import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Company company = new Company();
        company.loadFromFile();

        while (true) {
            try {
                System.out.println("\n==========Employee Attendance Tracker==========");
                System.out.println("1. Add a new employee");
                System.out.println("2. Mark attendance");
                System.out.println("3. Calculate monthly salary");
                System.out.println("4. Generate attendance report");
                System.out.println("5. Exit");
                System.out.println("===============================================");
                System.out.print("Choose an option: ");

                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1: company.addEmployee(scanner); break;
                    case 2: company.markAttendance(scanner); break;
                    case 3: company.calculateSalary(scanner); break;
                    case 4: company.generateReport(); break;
                    case 5:
                        company.saveToFile();
                        System.out.println("Exiting program.");
                        System.exit(0);
                    default: System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Try again.");
            }
        }
    }
}

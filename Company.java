import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Company {
    private ArrayList<Employee> employees = new ArrayList<>();
    private HashMap<Integer, Employee> empMap = new HashMap<>();
    private final String fileName = "C:\\Users\\hoody\\OneDrive\\Documents\\NetBeansProjects\\LabFinal\\build\\classes\\employees.txt";

    public ArrayList<Employee> getEmployees() { return employees; }
    public HashMap<Integer, Employee> getEmployeeMap() { return empMap; }

    public void addEmployee(Employee emp) {
        try {
            employees.add(emp);
            empMap.put(emp.getEmployeeID(), emp);
        } catch (Exception e) {
            System.out.println("Error adding employee.");
        }
    }

    public Employee getEmployee(int id) {
        try {
            return empMap.get(id);
        } catch (Exception e) {
            System.out.println("Error getting employee.");
            return null;
        }
    }

        public void saveToFile() {
        try {
            File file = new File(fileName);
            file.getParentFile().mkdirs();

            try (FileWriter writer = new FileWriter(file)) {
                for (Employee emp : employees) {
                    writer.write("Employee ID: " + emp.getEmployeeID() + "\n");
                    writer.write("Employee Name: " + emp.getName() + "\n");
                    writer.write("Employee Daily Salary: " + String.format("%.2f", emp.getSalaryPerDay()) + "\n");

                    for (AttendanceRecord a : emp.getAttendanceList()) {
                        writer.write("(" + String.format("%04d-%02d", a.getYear(), a.getMonth()) + "), "
                                + "Day " + a.getDay() + ", " + a.getStatus() + "\n");
                    }
                    writer.write("\n");
                }
            }
            System.out.println("Data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving file.");
        }
    }

    public void loadFromFile() {
        try (FileReader reader = new FileReader(fileName);
             Scanner sc = new Scanner(reader)) {

            employees.clear();
            empMap.clear();
            Employee emp = null;

            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("Employee ID:")) {
                    if (emp != null) addEmployee(emp);
                    int id = Integer.parseInt(line.substring(13).trim());
                    emp = new Employee(id, "", 0);
                } else if (line.startsWith("Employee Name:")) {
                    emp = new Employee(emp.getEmployeeID(), line.substring(15).trim(), emp.getSalaryPerDay());
                } else if (line.startsWith("Employee Daily Salary:")) {
                    emp = new Employee(emp.getEmployeeID(), emp.getName(),
                            Double.parseDouble(line.substring(22).trim()));
                } else if (line.startsWith("(") && line.contains("Day")) {
                    String[] parts = line.split(",");
                    String ym = parts[0].trim().replace("(", "").replace(")", "");
                    int year = Integer.parseInt(ym.split("-")[0]);
                    int month = Integer.parseInt(ym.split("-")[1]);
                    int day = Integer.parseInt(parts[1].trim().split(" ")[1]);
                    String status = parts[2].trim();
                    boolean isPresent = status.equals("P");

                    boolean exists = false;
                    for (AttendanceRecord record : emp.getAttendanceList()) {
                        if (record.getYear() == year && record.getMonth() == month && record.getDay() == day) {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists) {
                        emp.addAttendance(new AttendanceRecord(day, isPresent, month, year, status));
                    }
                }
            }

            if (emp != null) addEmployee(emp);
            System.out.println("Data loaded successfully.");

        } catch (IOException e) {
            System.out.println("File not found. Starting fresh.");
        } catch (Exception e) {
            System.out.println("Error loading file.");
        }
    }
    
    public void addEmployee(Scanner scanner) {
        try {
            System.out.print("\nEnter Employee ID: ");
            int id = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter Name: ");
            String name = scanner.nextLine();
            System.out.print("Enter Salary per Day: ");
            double sal = Double.parseDouble(scanner.nextLine());

            if (empMap.containsKey(id)) {
                System.out.println("Employee already exists.");
                return;
            }
            addEmployee(new Employee(id, name, sal));
            System.out.println("Employee successfully added.");
        } catch (Exception e) {
            System.out.println("There was an error in adding the employee. Try again.");
        }
    }

    public void markAttendance(Scanner scanner) {
        try {
            System.out.print("\nEnter Employee ID: ");
            int attendanceID = Integer.parseInt(scanner.nextLine());
            Employee emp = getEmployee(attendanceID);
            if (emp == null) { 
                System.out.println("Employee was not found."); 
                return; 
            }

            int year = 0, month = 0;
            while (true) {
                try {
                    System.out.print("Enter Year: ");
                    year = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter Month (1-12): ");
                    month = Integer.parseInt(scanner.nextLine());
                    if (month < 1 || month > 12) { System.out.println("Invalid month."); continue; }
                    break;
                } catch (Exception e) { System.out.println("Invalid input."); }
            }

            boolean alreadyRecorded = false;
            for (AttendanceRecord record : emp.getAttendanceList()) {
                if (record.getMonth() == month && record.getYear() == year) {
                    alreadyRecorded = true;
                    break;
                }
            }
            if (alreadyRecorded) {
                System.out.println("Attendance for " + year + "-" + String.format("%02d", month) + " already exists.");
                return;
            }

            int daysInMonth = 31;
            switch (month) {
                case 4: case 6: case 9: case 11: daysInMonth = 30; break;
                case 2:
                    if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) daysInMonth = 29;
                    else daysInMonth = 28;
                    break;
            }

            System.out.println("Mark attendance for " + daysInMonth + " days:");
            System.out.println("Enter 'P' for Present, 'A' for Absent, 'W' for Weekdays.");
            for (int day = 1; day <= daysInMonth; day++) {
                while (true) {
                    try {
                        System.out.print("Day " + day + ": ");
                        String status = scanner.nextLine().toUpperCase();
                        if (!status.equals("P") && !status.equals("A") && !status.equals("W")) {
                            System.out.println("Invalid input."); 
                            continue;
                        }
                        boolean isPresent = status.equals("P");
                        emp.addAttendance(new AttendanceRecord(day, isPresent, month, year, status));
                        break;
                    } catch (Exception e) { System.out.println("Error recording day."); }
                }
            }
            System.out.println("Attendance recorded successfully for " + year + "-" + String.format("%02d", month));
        } catch (Exception e) {
            System.out.println("There was an error in marking the attendance. Try again.");
        }
    }

    public void calculateSalary(Scanner scanner) {
        try {
            System.out.print("\nEnter Employee ID: ");
            int empSalaryID = Integer.parseInt(scanner.nextLine());
            Employee empSalary = getEmployee(empSalaryID);
            if (empSalary == null) { 
                System.out.println("Employee was not found."); 
                return; 
            }

            int year = 0, month = 0;
            while (true) {
                try {
                    System.out.print("Enter Year: ");
                    year = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter Month (1-12): ");
                    month = Integer.parseInt(scanner.nextLine());
                    if (month < 1 || month > 12) { System.out.println("Invalid month."); continue; }
                    break;
                } catch (Exception e) { System.out.println("Invalid input."); }
            }

            int present = 0;
            for (AttendanceRecord attendance : empSalary.getAttendanceList()) {
                if (attendance.getMonth() == month && attendance.getYear() == year 
                    && attendance.getStatus().equals("P")) {
                    present++;
                }
            }
            System.out.println("\nEmployee Name: " + empSalary.getName());
            double salary = present * empSalary.getSalaryPerDay();
            System.out.println("Monthly Salary for " + year + "-" + String.format("%02d", month) + " = " + salary);
        } catch (Exception e) {
            System.out.println("There was an error in calculating the salary. Try again.");
        }
    }

    public void generateReport() {
        try {
            for (Employee employee : empMap.values()) {
                System.out.println("\nEmployee Name: " + employee.getName());
                System.out.println("Employee ID: " + employee.getEmployeeID());

                int month = 0;
                int year = 0;
                int presentCount = 0;
                int absentCount = 0;
                int weekdayCount = 0;

                for (AttendanceRecord record : employee.getAttendanceList()) {
                    if (record.getYear() != year || record.getMonth() != month) {
                        if (year != 0) {
                            System.out.println("Month: " + year + "-" + (month < 10 ? "0" : "") + month);
                            System.out.println("Present = " + presentCount);
                            System.out.println("Absent = " + absentCount);
                            System.out.println("Weekdays = " + weekdayCount);
                        }
                        year = record.getYear();
                        month = record.getMonth();
                        presentCount = 0;
                        absentCount = 0;
                        weekdayCount = 0;
                    }

                    switch (record.getStatus()) {
                        case "P": presentCount++; break;
                        case "A": absentCount++; break;
                        case "W": weekdayCount++; break;
                    }
                }

                if (year != 0) {
                    System.out.println("Month: " + year + "-" + (month < 10 ? "0" : "") + month);
                    System.out.println("Present = " + presentCount);
                    System.out.println("Absent = " + absentCount);
                    System.out.println("Weekdays = " + weekdayCount);
                }
            }
        } catch (Exception e) {
            System.out.println("There was an error in generating the report. Try again.");
        }
    }


}

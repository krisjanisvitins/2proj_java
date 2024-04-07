// 231RDB085 Krišjānis Vītiņš 14. grupa
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.time.format.DateTimeFormatter;

public class Main {
    static String filename = "db.csv";
    static List<String> dbData;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        dbData = readDataFromFile();

        loop: while (true) {
            System.out.print("Enter command (print, add, del, edit, sort, find, avg, exit): ");
            String choice = sc.nextLine().trim().toLowerCase();

            if (choice.equals("exit")) {
                saveData();
                break;
            }

            if (choice.startsWith("add")) {
                addData(sc, choice);
            }

            else if (choice.startsWith("del")) {
                String dzesamaisID = choice.substring(4).trim();
                dzestID(dzesamaisID);
            }
            else if (choice.startsWith("find")) {
                String findInput = choice.substring(5).trim();
                findData(findInput);
            }
            else if (choice.startsWith("edit")) {
                String editDetails = choice.substring(5).trim();
                editData(editDetails);
            }
            else {
                switch (choice) {
                    case "print":
                        printData(dbData);
                        break;
                    case "sort":
                        sortData();
                        System.out.println("sorted");
                        break;
                    case "avg":
                        avgData();
                        break;
                        
                    default:
                        System.out.println("wrong command.");
            }
            }
        }    
    }

    public static List<String> readDataFromFile() {
        List<String> data = new ArrayList<>();
        try (FileReader fr = new FileReader(filename)) {
            Scanner sc = new Scanner(fr);
            while (sc.hasNextLine()) {
                data.add(sc.nextLine());
            }
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return data;
    }

    public static void printData(List<String> dbData) {
        System.out.println("------------------------------------------------------------");
        System.out.printf("%-3s %-20s %-11s %5s %9s %-8s%n","ID", "City", "Date", "Days", "Price", "Vehicle");
        System.out.println("------------------------------------------------------------");
        for (String entry : dbData) {
            String [] laukumi = entry.split(";");
            if (laukumi.length == 6) {
                System.out.printf("%-3s %-20s %-11s %5s %9s %-8s%n",
                    laukumi[0], laukumi[1], laukumi[2], laukumi[3], laukumi[4], laukumi[5]);
            }
            else {
                System.out.println("invalid data");
            }
        }
        System.out.println("------------------------------------------------------------");
    }

    public static void addData(Scanner sc, String input) {
        String tripDetails = input.substring(4).trim();
        String[] laukumi = tripDetails.split(";");
        if (laukumi.length != 6) {
            System.out.println("wrong field count");
            return;
    }

        String id = laukumi[0];
        String city = capitalize(laukumi[1]);
        String date = laukumi[2];
        String daysString = laukumi[3];
        String priceString = laukumi[4];
        String vehicle = laukumi[5].toUpperCase();

        if (!id.matches("\\d{3}")) {
            System.out.println("wrong id");
            return;
        }
        boolean idEksiste = false;
        for (String entry : dbData) {
            if (entry.startsWith(id + ";")) {
                idEksiste = true;
                break;
            }
        }
        if (idEksiste) {
            System.out.println("wrong id");
            return;
        }
    

        if (!pareizsDatums(date)) {
            System.out.println("wrong date");
            return;
        }

        try {
            int days = Integer.parseInt(daysString);
            if (days < 0) {
                System.out.println("wrong day count");
                return;
            }
        } catch (NumberFormatException ex) {
            System.out.println("wrong day count");
            return;
        }
        try {
            double price = Double.parseDouble(priceString);
            if (price < 0) {
                System.out.println("wrong price");
                return;
            }
        }
        catch (NumberFormatException ex) {
            System.out.println("wrong price");
            return;
        }
        if (!vehicle.matches("TRAIN|PLANE|BUS|BOAT")) {
            System.out.println("wrong vehicle");
            return;
        }

        String doublePrice = String.format("%.2f", Double.parseDouble(priceString));


        String newEntry = String.format("%s;%s;%s;%s;%s;%s", id, city, date, daysString, doublePrice, vehicle);
        dbData.add(newEntry);
        System.out.println("added");
    }

    public static String capitalize(String city) {
        StringBuilder result = new StringBuilder();
        boolean nextCapital = true;
        for (char c : city.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextCapital = true;
            }
            else if (nextCapital) {
                c = Character.toTitleCase(c);
                nextCapital = false;
            }
            else {
                c = Character.toLowerCase(c);
            }
            result.append(c);
            }
        
        return result.toString();
    }

public static boolean pareizsDatums(String date){
    String[] parts = date.split("/");
    if (parts.length != 3)
        return false;
    
    try {
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        if (day < 1 || day > 31 || month < 1 || month > 12)
            return false;

        return true;
        
    }
    catch (NumberFormatException ex) {
        return false;
    }
}
public static void dzestID(String id) {
    if(!id.matches("\\d{3}")) {
        System.out.println("wrong id");
        return;
    }

    boolean idEksiste = false;
    Iterator<String> iterator = dbData.iterator();
    while (iterator.hasNext()) {
        String entry = iterator.next();
        if (entry.startsWith(id + ";")) {
            iterator.remove();
            idEksiste = true;
            break;
    }
    }
    if (idEksiste) {
        System.out.println("deleted");
    }
    else {
        System.out.println("wrong id");
}
}
public static void sortData() {
    Collections.sort(dbData, new Comparator<String>() {
        public int compare(String p1, String p2) {
            String[] parts1 = p1.split(";");
            String[] parts2 = p2.split(";");
            String[] dateParts1 = parts1[2].split("/");
            String[] dateParts2 = parts2[2].split("/");

            int yearComparison = Integer.compare(Integer.parseInt(dateParts1[2]), Integer.parseInt(dateParts2[2]));
            if (yearComparison != 0)
                return yearComparison;

            int monthComparison = Integer.compare(Integer.parseInt(dateParts1[1]), Integer.parseInt(dateParts2[1]));
            if (monthComparison != 0)
                return monthComparison;

            return Integer.compare(Integer.parseInt(dateParts1[0]), Integer.parseInt(dateParts2[0]));
        }
    });
    try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
        for (String entry : dbData) {
            writer.println(entry);
        }
    }
    catch (IOException e) {
        System.out.println(e.getMessage());
    }
}

public static void avgData() {
    double total = 0.0;
    int size = dbData.size();
    for (String entry : dbData) {
        String[] parts = entry.split(";");
        double price = Double.parseDouble(parts[4]);
        total += price;
    }
    double result = (total/size);
    System.out.printf("average=%.2f%n", result);
    }
    
public static void findData(String input) {
    double limit;
    try {
        limit = Double.parseDouble(input);
    }
    catch (NumberFormatException e) {
        System.out.println("wrong price");
        return;
    }
    if (limit<0) {
        System.out.println("wrong price");
        return;
    }

    System.out.println("------------------------------------------------------------");
    System.out.printf("%-3s %-20s %-11s %5s %9s %-8s%n", "ID", "City", "Date", "Days", "Price", "Vehicle");
    System.out.println("------------------------------------------------------------");

    for (String entry : dbData) {
        String[] parts = entry.split(";");
        double price = Double.parseDouble(parts[4]);
        if (price <= limit) {
            System.out.printf("%-3s %-20s %-11s %5s %9s %-8s%n",parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
        }
    }
    System.out.println("------------------------------------------------------------");
}
public static void editData(String input) {
    String[] laukumi = input.split(";", -1);
    if (laukumi.length < 1 || laukumi.length > 6) {
        System.out.println("wrong field count");
        return;
    }

    String id = laukumi[0];

    if (!id.matches("\\d{3}")) {
        System.out.println("wrong id");
        return;
    }
    boolean idEksiste = false;
    for (String entry : dbData) {
        if (entry.startsWith(id + ";")) {
            idEksiste = true;
            break;
        }
    }
    if (!idEksiste) {
        System.out.println("wrong id");
        return;
    }






    String city = null;
    if (laukumi.length > 1 && !laukumi[1].isEmpty()) {
        city = capitalize(laukumi[1]);
    }

    String date = null;
    if (laukumi.length > 2 && !laukumi[2].isEmpty()) {
        date = laukumi[2];
    }

    String daysString = null;
    if (laukumi.length > 3 && !laukumi[3].isEmpty()) {
        daysString = laukumi[3];
        try {
            int days = Integer.parseInt(daysString);
            if (days <= 0) {
                System.out.println("wrong day count");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("wrong day count");
            return;
        }
    }

    String priceString = null;
    if (laukumi.length > 4 && !laukumi[4].isEmpty()) {
        priceString = laukumi[4];
        try {
            double price = Double.parseDouble(priceString);
            priceString = String.format("%.2f", price);
        }
        catch (NumberFormatException e) {
            System.out.println("wrong price");
            return;
        }
    }

    String vehicle = null;
    if (laukumi.length > 5 && !laukumi[5].isEmpty()) {
        vehicle = laukumi[5].toUpperCase();
    }






    if (city != null && city.isEmpty()) {
        System.out.println("wrong city");
        return;
    }
    if (date != null && !pareizsDatums(date)) {
        System.out.println("wrong date");
        return;
    }



    if (daysString != null && !daysString.isEmpty()) {
        int days = Integer.parseInt(daysString);
        if (days <=0) {
            System.out.println("wrong day count");
            return;
        }
    }


    if (priceString != null && !priceString.isEmpty()) {
        double price = Double.parseDouble(priceString);
        if (price <= 0) {
            System.out.println("wrong price");
            return;
        }
    }
    if (vehicle != null && !vehicle.matches("TRAIN|PLANE|BUS|BOAT")) {
        System.out.println("wrong vehicle");
        return;
    }

    for (int i=0; i <dbData.size(); i++) {
        String entry = dbData.get(i);
        String[] parts = entry.split(";");
        if (parts[0].trim().equals(id.trim())) {
            if (city != null) parts[1] = city;
            if (date != null) parts[2] = date;
            if (daysString != null) parts[3] = daysString;
            if (priceString != null) parts[4] = priceString;
            if (vehicle != null) parts[5] = vehicle;
            dbData.set(i, String.join(";", parts));
            System.out.println("changed");
            return;

    }
}
}
public static void saveData() {
    try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
        for (String entry : dbData) {
            writer.println(entry);
        }
    }
    catch (IOException e) {
        System.out.println(e.getMessage());
    }
}
}




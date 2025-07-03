import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SYNR {
    static Map<String, Process> processMap = new HashMap<>();
    static Map<String, List<String>> outputLog = new HashMap<>();
    
    // Color constants for modern styling
    static final String RESET = "\u001B[0m";
    static final String BOLD = "\u001B[1m";
    static final String DIM = "\u001B[2m";
    static final String ITALIC = "\u001B[3m";
    static final String UNDERLINE = "\u001B[4m";
    static final String BLINK = "\u001B[5m";
    static final String REVERSE = "\u001B[7m";
    
    // RGB colors for modern design
    static final String CYBER_BLUE = "\u001B[38;2;0;255;255m";
    static final String NEON_GREEN = "\u001B[38;2;57;255;20m";
    static final String ELECTRIC_PURPLE = "\u001B[38;2;191;64;191m";
    static final String HOT_PINK = "\u001B[38;2;255;20;147m";
    static final String ORANGE_RED = "\u001B[38;2;255;69;0m";
    static final String GOLD = "\u001B[38;2;255;215;0m";
    static final String SILVER = "\u001B[38;2;192;192;192m";
    static final String DARK_GRAY = "\u001B[38;2;64;64;64m";
    static final String LIGHT_GRAY = "\u001B[38;2;128;128;128m";
    static final String WHITE = "\u001B[38;2;255;255;255m";
    
    // Background colors
    static final String BG_DARK = "\u001B[48;2;20;20;20m";
    static final String BG_ACCENT = "\u001B[48;2;40;40;60m";

    static void logoSYNR() {
    String[] logo = {
        "  ███████╗██╗   ██╗███╗   ██╗██████╗ ",
        "  ██╔════╝╚██╗ ██╔╝████╗  ██║██╔══██╗",
        "  ███████╗ ╚████╔╝ ██╔██╗ ██║██████╔╝",
        "  ╚════██║  ╚██╔╝  ██║╚██╗██║██╔══██╗",
        "  ███████║   ██║   ██║ ╚████║██║  ██║",
        "  ╚══════╝   ╚═╝   ╚═╝  ╚═══╝╚═╝  ╚═╝"
    };

    for (String line : logo) {
        for (int i = 0; i < line.length(); i++) {
            double progress = (double) i / line.length();
            String color;

            if (progress < 0.3) {
                color = CYBER_BLUE;
            } else if (progress < 0.6) {
                color = ELECTRIC_PURPLE;
            } else {
                color = HOT_PINK;
            }

            char ch = line.charAt(i);
            if (ch != ' ') {
                System.out.print(BOLD + color + ch + RESET);
            } else {
                System.out.print(" ");
            }
        }
        System.out.println();
    }

    System.out.println();
    System.out.println(ITALIC + SILVER + "Synchronized Yield and Runtime" + RESET);
    System.out.println();
}


    static void printHeader(String title) {
        int width = 55;
        String paddedTitle = " " + title + " ";
        int padding = (width - paddedTitle.length()) / 2;
        
        System.out.println(CYBER_BLUE + "╔" + "═".repeat(width) + "╗" + RESET);
        System.out.println(CYBER_BLUE + "║" + " ".repeat(padding) + BOLD + WHITE + paddedTitle + RESET + " ".repeat(width - padding - paddedTitle.length()) + CYBER_BLUE + "║" + RESET);
        System.out.println(CYBER_BLUE + "╚" + "═".repeat(width) + "╝" + RESET);
        System.out.println();
    }

    static void printSeparator() {
        System.out.println(DIM + DARK_GRAY + "─".repeat(55) + RESET);
    }

    static void printMenuItem(String key, String description, String icon) {
        System.out.println("  " + BOLD + CYBER_BLUE + "[" + key + "]" + RESET + " " + 
                          icon + " " + NEON_GREEN + description + RESET);
    }

    static void printStatus(String message, String type) {
        String icon, color;
        switch (type.toLowerCase()) {
            case "success":
                icon = "✅";
                color = NEON_GREEN;
                break;
            case "error":
                icon = "❌";
                color = ORANGE_RED;
                break;
            case "warning":
                icon = "⚠️";
                color = GOLD;
                break;
            case "info":
                icon = "ℹ️";
                color = CYBER_BLUE;
                break;
            default:
                icon = "•";
                color = WHITE;
        }
        System.out.println("  " + icon + " " + color + message + RESET);
    }

    static int[] interpolate(int[] start, int[] end, double t) {
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = (int) (start[i] + t * (end[i] - start[i]));
        }
        return rgb;
    }

    static void checkOrCreateMainConfig() {
        File configFile = new File("./.mainconfig");
        if (!configFile.exists()) {
            try {
                Scanner scanner = new Scanner(System.in);
                printHeader("INITIAL SETUP");
                printStatus("First time setup detected", "info");
                
                String directory = promptWithBox("📁 Enter Directory Path for SYNR Data", scanner, "🔧");
                String content = "directory=" + directory + "\n";

                try (FileWriter writer = new FileWriter(configFile)) {
                    writer.write(content);
                }

                printStatus("Configuration saved successfully!", "success");
                clearScreenAndScrollback();
            } catch (IOException e) {
                printStatus("Error creating configuration: " + e.getMessage(), "error");
                clearScreenAndScrollback();
            }
        }
    }

    static void createFolderStructureFromConfig() {
        File configFile = new File("./.mainconfig");
        if (!configFile.exists()) {
            printStatus("Configuration file not found", "error");
            return;
        }

        try (Scanner scanner = new Scanner(configFile)) {
            String directoryPath = null;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("directory=")) {
                    directoryPath = line.substring("directory=".length());
                    break;
                }
            }

            if (directoryPath == null || directoryPath.isEmpty()) {
                printStatus("Directory not specified in configuration", "error");
                return;
            }

            File panalDir = new File(directoryPath, "SYNR/panal");
            if (!panalDir.exists()) {
                if (panalDir.mkdirs()) {
                    printStatus("Created directory structure: " + panalDir.getAbsolutePath(), "success");
                } else {
                    printStatus("Failed to create directory structure", "error");
                    return;
                }
            }

            File databaseFile = new File(panalDir, "panaldatabase.txt");
            if (!databaseFile.exists()) {
                if (databaseFile.createNewFile()) {
                    printStatus("Database file created successfully", "success");
                } else {
                    printStatus("Failed to create database file", "error");
                }
            }

        } catch (IOException e) {
            printStatus("Error during setup: " + e.getMessage(), "error");
        }
    }

    public static void clearScreenAndScrollback() {
        System.out.print("\033[H\033[2J\033[3J");
        System.out.flush();
    }

    static void boardMain() {
        printHeader("MAIN CONTROL CENTER");
        System.out.println();
        printMenuItem("P", "Process Panel Manager", "🚀");
        printMenuItem("Q", "Exit System", "🚪");
        System.out.println();
        printSeparator();
        
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String command = promptWithBox("Enter Command", scanner, "⚡");
            
            switch (command.toUpperCase()) {
                case "P":
                    clearScreenAndScrollback();
                    boardpanal();
                    continue;
                case "Q":
                    System.out.println();
                    printStatus("Preparing to exit...", "warning");
                    String confirm = promptWithBox("Type 'YES' to confirm exit", scanner, "🔐");
                    if (confirm.equalsIgnoreCase("YES")) {
                        System.out.println();
                        printStatus("Goodbye! Thanks for using SYNR 🌟", "success");
                        System.out.println();
                        scanner.close();
                        return;
                    } else {
                        printStatus("Exit cancelled", "info");
                        clearScreenAndScrollback();
                        boardMain();
                        return;
                    }
                default:
                    printStatus("Invalid command. Please try again.", "error");
                    break;
            }
        }
    }

    public static String promptWithBox(String label, Scanner scanner, String icon) {
        int minWidth = 50;
        int labelWidth = label.length() + icon.length() + 3;
        int boxWidth = Math.max(minWidth, labelWidth + 10);
        
        // Modern box design with rounded corners
        String top = CYBER_BLUE + "╭" + "─".repeat(boxWidth) + "╮" + RESET;
        String middle = CYBER_BLUE + "│" + RESET + " " + icon + "  " + BOLD + WHITE + label + RESET + 
                       " ".repeat(boxWidth - labelWidth - 1) + CYBER_BLUE + "│" + RESET;
        String inputLine = CYBER_BLUE + "╰" + "─".repeat(boxWidth/2) + "▶ " + RESET;
        
        System.out.println(top);
        System.out.println(middle);
        System.out.print(inputLine + NEON_GREEN);
        
        String input = scanner.nextLine();
        System.out.print(RESET);
        return input;
    }

    // Overloaded method for backward compatibility
    public static String promptWithBox(String label, Scanner scanner) {
        return promptWithBox(label, scanner, "💫");
    }

    static void saveControlpanal(String name, String path, String command) {
        File configFile = new File("./.mainconfig");
        if (!configFile.exists()) {
            printStatus("Configuration file not found", "error");
            return;
        }

        try (Scanner scanner = new Scanner(configFile)) {
            String baseDirectory = null;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("directory=")) {
                    baseDirectory = line.substring("directory=".length());
                    break;
                }
            }

            if (baseDirectory == null || baseDirectory.isEmpty()) {
                printStatus("Directory not configured", "error");
                return;
            }

            File panalDir = new File(baseDirectory, "SYNR/panal");
            panalDir.mkdirs();

            File databaseFile = new File(panalDir, "panaldatabase.txt");

            try (FileWriter writer = new FileWriter(databaseFile, true)) {
                writer.write("name=" + name + "\n");
                writer.write("path=" + path + "\n");
                writer.write("command=" + command + "\n");
                writer.write("----\n");
            }

            printStatus("Control panel saved: " + name, "success");

        } catch (IOException e) {
            printStatus("Error saving control panel: " + e.getMessage(), "error");
        }
    }

    static void updateControlPanel(String name, String newPath, String newCommand) {
        File configFile = new File("./.mainconfig");
        if (!configFile.exists()) {
            printStatus("Configuration file not found", "error");
            return;
        }

        String baseDirectory = null;
        try (Scanner scanner = new Scanner(configFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("directory=")) {
                    baseDirectory = line.substring("directory=".length()).trim();
                    break;
                }
            }
        } catch (IOException e) {
            printStatus("Error reading configuration: " + e.getMessage(), "error");
            return;
        }

        if (baseDirectory == null || baseDirectory.isEmpty()) {
            printStatus("Directory not configured", "error");
            return;
        }

        File dbFile = new File(baseDirectory, "SYNR/panal/panaldatabase.txt");
        if (!dbFile.exists()) {
            printStatus("Database file not found", "error");
            return;
        }

        List<String> lines = new ArrayList<>();
        try (Scanner scanner = new Scanner(dbFile)) {
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
        } catch (IOException e) {
            printStatus("Error reading database: " + e.getMessage(), "error");
            return;
        }

        List<String> newLines = new ArrayList<>();
        boolean updated = false;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().startsWith("name=")) {
                String currentName = line.substring("name=".length()).trim();
                if (currentName.equalsIgnoreCase(name.trim())) {
                    printStatus("Updating entry: " + currentName, "info");
                    newLines.add("name=" + currentName);
                    newLines.add("path=" + newPath);
                    newLines.add("command=" + newCommand);
                    newLines.add("----");
                    i += 3;
                    updated = true;
                    continue;
                }
            }
            newLines.add(line);
        }

        if (!updated) {
            printStatus("No matching entry found", "error");
            return;
        }

        try (FileWriter writer = new FileWriter(dbFile, false)) {
            for (String l : newLines) {
                writer.write(l + "\n");
            }
        } catch (IOException e) {
            printStatus("Error writing to database: " + e.getMessage(), "error");
            return;
        }

        printStatus("Control panel updated: " + name, "success");
    }

    static void addControlpanal(String name, String path, String command) {
        String finalName = name.isEmpty() ? ITALIC + DARK_GRAY + "(not set)" + RESET : NEON_GREEN + name + RESET;
        String finalPath = path.isEmpty() ? ITALIC + DARK_GRAY + "(not set)" + RESET : CYBER_BLUE + path + RESET;
        String finalCommand = command.isEmpty() ? ITALIC + DARK_GRAY + "(not set)" + RESET : ELECTRIC_PURPLE + command + RESET;

        printHeader("ADD NEW CONTROL PANEL");
        System.out.println();
        
        System.out.println("  " + BOLD + GOLD + "[1]" + RESET + " 🏷️  Name     : " + finalName);
        System.out.println("  " + BOLD + GOLD + "[2]" + RESET + " 📁 Path     : " + finalPath);
        System.out.println("  " + BOLD + GOLD + "[3]" + RESET + " ⚡ Command  : " + finalCommand);
        System.out.println();
        printSeparator();
        System.out.println("  " + BOLD + NEON_GREEN + "[S]" + RESET + " 💾 Save Configuration");
        System.out.println("  " + BOLD + ORANGE_RED + "[C]" + RESET + " ❌ Cancel");
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String select = promptWithBox("Select Option", scanner, "🎯");
            switch (select.toUpperCase()) {
                case "1":
                    name = promptWithBox("Enter Name", scanner, "🏷️");
                    clearScreenAndScrollback();
                    addControlpanal(name, path, command);
                    return;
                case "2":
                    path = promptWithBox("Enter Path", scanner, "📁");
                    clearScreenAndScrollback();
                    addControlpanal(name, path, command);
                    return;
                case "3":
                    command = promptWithBox("Enter Command", scanner, "⚡");
                    clearScreenAndScrollback();
                    addControlpanal(name, path, command);
                    return;
                case "S":
                    printStatus("Saving control panel...", "info");
                    saveControlpanal(name, path, command);
                    clearScreenAndScrollback();
                    listControlpanals();
                    return;
                case "C":
                    clearScreenAndScrollback();
                    boardpanal();
                    return;
                default:
                    printStatus("Invalid option. Please try again.", "error");
                    break;
            }
        }
    }

    static void runInBackground(String name, String path, String command) {
        try {
            printStatus("Starting process: " + name, "info");
            
            String[] parts = command.trim().split("\\s+");
            ProcessBuilder builder = new ProcessBuilder(parts);
            builder.directory(new File(path));
            builder.redirectErrorStream(true);

            Process process = builder.start();
            processMap.put(name, process);
            outputLog.put(name, new ArrayList<>());

            new Thread(() -> {
                try (Scanner s = new Scanner(process.getInputStream())) {
                    while (s.hasNextLine()) {
                        String line = s.nextLine();
                        synchronized (outputLog) {
                            outputLog.get(name).add(line);
                        }
                    }
                } catch (Exception e) {
                    printStatus("Error reading process output: " + e.getMessage(), "error");
                }
            }).start();

            printStatus("Process started successfully: " + name, "success");

        } catch (IOException e) {
            printStatus("Error starting process: " + e.getMessage(), "error");
        }
    }

    static void monitorCommand(String name) {
        if (!outputLog.containsKey(name)) {
            printStatus("No output found for: " + name, "error");
            return;
        }

        printHeader("PROCESS MONITOR - " + name);
        System.out.println("  " + ITALIC + SILVER + "Press ENTER to exit monitoring..." + RESET);
        printSeparator();

        Scanner inputScanner = new Scanner(System.in);
        int lastIndex = 0;

        Thread inputThread = new Thread(() -> {
            inputScanner.nextLine();
        });
        inputThread.start();

        while (inputThread.isAlive()) {
            List<String> logs;
            synchronized (outputLog) {
                logs = new ArrayList<>(outputLog.get(name));
            }

            for (; lastIndex < logs.size(); lastIndex++) {
                System.out.println("  " + DIM + LIGHT_GRAY + "│" + RESET + " " + logs.get(lastIndex));
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        printStatus("Monitoring stopped", "info");
    }

    static void stopCommand(String name) {
        Process p = processMap.get(name);
        if (p != null && p.isAlive()) {
            p.destroy();
            processMap.remove(name);
            outputLog.remove(name);
            printStatus("Process stopped: " + name, "success");
        } else {
            printStatus("No running process found: " + name, "warning");
        }
    }

    static void editControlPanel(String name, String currentPath, String currentCommand) {
        Scanner scanner = new Scanner(System.in);
        String newPath = currentPath;
        String newCommand = currentCommand;

        while (true) {
            printHeader("EDIT CONTROL PANEL - " + name);
            System.out.println();
            System.out.println("  " + BOLD + GOLD + "[1]" + RESET + " 📁 Path    : " + CYBER_BLUE + newPath + RESET);
            System.out.println("  " + BOLD + GOLD + "[2]" + RESET + " ⚡ Command : " + ELECTRIC_PURPLE + newCommand + RESET);
            System.out.println();
            printSeparator();
            System.out.println("  " + BOLD + NEON_GREEN + "[S]" + RESET + " 💾 Save Changes");
            System.out.println("  " + BOLD + ORANGE_RED + "[C]" + RESET + " ❌ Cancel");
            System.out.println();

            String input = promptWithBox("Select Option", scanner, "🎯");

            switch (input.toUpperCase()) {
                case "1":
                    newPath = promptWithBox("Enter New Path", scanner, "📁");
                    clearScreenAndScrollback();
                    break;
                case "2":
                    newCommand = promptWithBox("Enter New Command", scanner, "⚡");
                    clearScreenAndScrollback();
                    break;
                case "S":
                    clearScreenAndScrollback();
                    updateControlPanel(name, newPath, newCommand);
                    showPanelActions(name, newPath, newCommand);
                    return;
                case "C":
                    printStatus("Edit cancelled", "info");
                    return;
                default:
                    printStatus("Invalid option. Please try again.", "error");
            }
        }
    }

    static void deleteControlPanel(String name) {
        printHeader("DELETE CONTROL PANEL");
        printStatus("⚠️  This action cannot be undone!", "warning");
        System.out.println();
        
        Scanner scanner = new Scanner(System.in);
        String confirmation = promptWithBox("Type 'YES' to confirm deletion", scanner, "🔐");
        
        if (confirmation.equalsIgnoreCase("YES")) {
            File configFile = new File("./.mainconfig");
            if (!configFile.exists()) {
                printStatus("Configuration file not found", "error");
                return;
            }

            try (Scanner configScanner = new Scanner(configFile)) {
                String baseDirectory = null;

                while (configScanner.hasNextLine()) {
                    String line = configScanner.nextLine().trim();
                    if (line.startsWith("directory=")) {
                        baseDirectory = line.substring("directory=".length());
                        break;
                    }
                }

                if (baseDirectory == null || baseDirectory.isEmpty()) {
                    printStatus("Directory not configured", "error");
                    return;
                }

                File panalDir = new File(baseDirectory, "SYNR/panal");
                File databaseFile = new File(panalDir, "panaldatabase.txt");

                if (!databaseFile.exists()) {
                    printStatus("Database file not found", "error");
                    return;
                }

                List<String> lines = new ArrayList<>();
                try (Scanner fileScanner = new Scanner(databaseFile)) {
                    while (fileScanner.hasNextLine()) {
                        lines.add(fileScanner.nextLine());
                    }
                }

                List<String> newLines = new ArrayList<>();
                boolean skipBlock = false;

                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);

                    if (line.startsWith("name=")) {
                        if (line.substring(5).trim().equals(name)) {
                            skipBlock = true;
                            i += 3;
                            continue;
                        } else {
                            skipBlock = false;
                        }
                    }

                    if (!skipBlock) {
                        newLines.add(line);
                    }
                }

                try (FileWriter writer = new FileWriter(databaseFile, false)) {
                    for (String l : newLines) {
                        writer.write(l + "\n");
                    }
                }
                
                clearScreenAndScrollback();
                printStatus("Control panel deleted: " + name, "success");
                stopCommand(name);
                listControlpanals();

            } catch (IOException e) {
                printStatus("Error deleting control panel: " + e.getMessage(), "error");
            }
        } else {
            clearScreenAndScrollback();
            printStatus("Deletion cancelled", "info");
            listControlpanals();
        }
    }

    static void showPanelActions(String name, String path, String command) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printHeader("CONTROL PANEL - " + name);
            System.out.println();
            System.out.println("  " + BOLD + "📁 Path:" + RESET + "     " + CYBER_BLUE + path + RESET);
            System.out.println("  " + BOLD + "⚡ Command:" + RESET + "  " + ELECTRIC_PURPLE + command + RESET);
            System.out.println();
            printSeparator();
            System.out.println();
            
            printMenuItem("1", "Run Process", "🚀");
            printMenuItem("2", "Stop Process", "⏹️");
            printMenuItem("3", "Monitor Output", "📊");
            printMenuItem("4", "Edit Panel", "✏️");
            printMenuItem("5", "Delete Panel", "🗑️");
            printMenuItem("B", "Back to List", "⬅️");
            System.out.println();

            String action = promptWithBox("Select Action", scanner, "🎯");

            switch (action.toUpperCase()) {
                case "1":
                    runInBackground(name, path, command);
                    monitorCommand(name);
                    clearScreenAndScrollback();
                    break;
                case "2":
                    stopCommand(name);
                    break;
                case "3":
                    monitorCommand(name);
                    clearScreenAndScrollback();
                    break;
                case "4":
                    editControlPanel(name, path, command);
                    clearScreenAndScrollback();
                    break;
                case "5":
                    deleteControlPanel(name);
                    return;
                case "B":
                    clearScreenAndScrollback();
                    listControlpanals();
                    return;
                default:
                    printStatus("Invalid option. Please try again.", "error");
            }
        }
    }

    static void listControlpanals() {
        File configFile = new File("./.mainconfig");
        if (!configFile.exists()) {
            printStatus("Configuration file not found", "error");
            return;
        }

        try (Scanner scanner = new Scanner(configFile)) {
            String baseDir = null;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("directory=")) {
                    baseDir = line.substring("directory=".length());
                    break;
                }
            }

            if (baseDir == null || baseDir.isEmpty()) {
                printStatus("Directory not configured", "error");
                return;
            }

            File dbFile = new File(baseDir, "SYNR/panal/panaldatabase.txt");
            if (!dbFile.exists()) {
                printHeader("CONTROL PANELS");
                printStatus("No control panels found", "info");
                System.out.println();
                printMenuItem("B", "Back to Panel Menu", "⬅️");
                
                Scanner input = new Scanner(System.in);
                String selected = promptWithBox("Press B to go back", input, "🎯");
                
                if (selected.equalsIgnoreCase("B")) {
                    clearScreenAndScrollback();
                    boardpanal();
                }
                return;
            }

            ArrayList<String[]> panels = new ArrayList<>();

            try (Scanner fileScanner = new Scanner(dbFile)) {
                String name = "Unnamed", path = "", command = "";

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine().trim();
                    if (line.startsWith("name=")) {
                        name = line.substring("name=".length());
                    } else if (line.startsWith("path=")) {
                        path = line.substring("path=".length());
                    } else if (line.startsWith("command=")) {
                        command = line.substring("command=".length());
                    } else if (line.equals("----")) {
                        panels.add(new String[] { name, path, command });
                        name = "Unnamed";
                        path = "";
                        command = "";
                    }
                }

                if (panels.isEmpty()) {
                    printHeader("CONTROL PANELS");
                    printStatus("No control panels configured", "info");
                    System.out.println();
                    printMenuItem("B", "Back to Panel Menu", "⬅️");
                    
                    Scanner input = new Scanner(System.in);
                    String selected = promptWithBox("Press B to go back", input, "🎯");
                    
                    if (selected.equalsIgnoreCase("B")) {
                        clearScreenAndScrollback();
                        boardpanal();
                    }
                    return;
                }

                // Enhanced panel listing with better formatting
                printHeader("CONTROL PANELS");
                System.out.println();
                
                for (int i = 0; i < panels.size(); i++) {
                    String[] panel = panels.get(i);
                    String status = processMap.containsKey(panel[0]) && processMap.get(panel[0]).isAlive() ? 
                                   NEON_GREEN + "🟢 RUNNING" + RESET : 
                                   DARK_GRAY + "⚫ STOPPED" + RESET;
                    
                    System.out.println("  " + BOLD + CYBER_BLUE + "[" + (i + 1) + "]" + RESET + " " + 
                                      BOLD + WHITE + panel[0] + RESET + " " + status);
                    System.out.println("      " + DIM + LIGHT_GRAY + "📁 " + panel[1] + RESET);
                    System.out.println("      " + DIM + LIGHT_GRAY + "⚡ " + panel[2] + RESET);
                    System.out.println();
                }
                
                printSeparator();
                System.out.println();
                printMenuItem("B", "Back to Panel Menu", "⬅️");
                System.out.println();

                Scanner input = new Scanner(System.in);
                String selected = promptWithBox("Enter panel number or B for back", input, "🎯");

                if (selected.equalsIgnoreCase("B")) {
                    clearScreenAndScrollback();
                    boardpanal();
                    return;
                }

                try {
                    int panelIndex = Integer.parseInt(selected) - 1;
                    if (panelIndex >= 0 && panelIndex < panels.size()) {
                        String[] selectedPanel = panels.get(panelIndex);
                        clearScreenAndScrollback();
                        showPanelActions(selectedPanel[0], selectedPanel[1], selectedPanel[2]);
                    } else {
                        printStatus("Invalid panel number", "error");
                        Thread.sleep(1000);
                        clearScreenAndScrollback();
                        listControlpanals();
                    }
                } catch (NumberFormatException e) {
                    printStatus("Please enter a valid number", "error");
                    
                    clearScreenAndScrollback();
                    listControlpanals();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            } catch (IOException e) {
                printStatus("Error reading panel database: " + e.getMessage(), "error");
            }

        } catch (IOException e) {
            printStatus("Error reading configuration: " + e.getMessage(), "error");
        }
    }

    static void boardpanal() {
        printHeader("PROCESS PANEL MANAGER");
        System.out.println();
        printMenuItem("A", "Add New Control Panel", "➕");
        printMenuItem("L", "List All Panels", "📋");
        printMenuItem("B", "Back to Main Menu", "⬅️");
        System.out.println();
        printSeparator();
        
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String command = promptWithBox("Select Option", scanner, "🎯");
            
            switch (command.toUpperCase()) {
                case "A":
                    clearScreenAndScrollback();
                    addControlpanal("", "", "");
                    break;
                case "L":
                    clearScreenAndScrollback();
                    listControlpanals();
                    break;
                case "B":
                    clearScreenAndScrollback();
                    boardMain();
                    return;
                default:
                    printStatus("Invalid option. Please try again.", "error");
                    break;
            }
        }
    }

    // Enhanced startup sequence
    static void startupSequence() {
        clearScreenAndScrollback();
        
        // Loading animation
        System.out.println();
        System.out.println();
        System.out.print("  " + CYBER_BLUE + "Initializing SYNR");
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(200);
                System.out.print(".");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println(" " + NEON_GREEN + "✓" + RESET);
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        clearScreenAndScrollback();
        logoSYNR();
        
        // Welcome message
        System.out.println("  " + BOLD + CYBER_BLUE + "Welcome to SYNR" + RESET + " - " + 
                          ITALIC + SILVER + "Your Advanced Process Control System" + RESET);
        System.out.println();
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        clearScreenAndScrollback();
    }

    public static void main(String[] args) {
        startupSequence();
        checkOrCreateMainConfig();
        createFolderStructureFromConfig();
        logoSYNR();
        boardMain();
    }
}
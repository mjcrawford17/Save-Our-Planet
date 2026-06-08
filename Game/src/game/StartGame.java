package game;

import java.util.Scanner;

/**
 * Entry point for the Save Our Planet game.
 *  * <p>
 *  * Handles the initial startup sequence: displaying the title, collecting the number
 *  * of players and their names, printing the rules, and launching the game loop via
 *  * {@link GameManager#playGame(Scanner)}.
 *  * </p>
 */
public class StartGame {

    private static final Scanner sc = new Scanner(System.in);
    private static final GameManager gameManager = new GameManager();

    /**
     * Application entry point. Clears the screen, runs the title sequence,
     * and starts the game setup flow.
     */
    public static void main(String[] args) throws InterruptedException {
        Settings.clearScreen();
        titleSequence();

        startGame();
    }

    /**
     * Orchestrates the full game startup: collects player count and names,
     * prints the rules, then hands control to the game loop.
     */
    private static void startGame() throws InterruptedException {

        getPlayers();
        setPlayerNames();
        System.out.printf("All players registered successfully!%n%n");

        //Play and End Game
        gameManager.printRules();
        gameManager.playGame(sc);
    }

    /**
     * Displays the ASCII art title screen with a staggered line-by-line animation.
     * <p>
     * Title generated using TAAG (patorjk.com) with the "Slant" font.
     * </p>
     */
    private static void titleSequence() {
        try {
            //https://patorjk.com/software/taag/#p=display&f=Slant&t=Save+Our+Planet&x=none&v=4&h=4&w=80&we=false
            Thread.sleep(250);
            System.out.println("   _____                     ____                ____  __                 __ ");
            Thread.sleep(250);
            System.out.println("  / ___/____ __   _____     / __ \\__  _______   / __ \\/ /___ _____  ___  / /_");
            Thread.sleep(250);
            System.out.println("  \\__ \\/ __ `/ | / / _ \\   / / / / / / / ___/  / /_/ / / __ `/ __ \\/ _ \\/ __/");
            Thread.sleep(250);
            System.out.println(" ___/ / /_/ /| |/ /  __/  / /_/ / /_/ / /     / ____/ / /_/ / / / /  __/ /_  ");
            Thread.sleep(250);
            System.out.println("/____/\\__,_/ |___/\\___/   \\____/\\__,_/_/     /_/   /_/\\__,_/_/ /_/\\___/\\__/  ");
            Thread.sleep(1000);
            System.out.println();
            System.out.println();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Prompts the user to enter the number of players and validates their input.
     * <p>
     * Repeats until a valid integer within {@link Settings#MIN_PLAYERS} and
     * {@link Settings#MAX_PLAYERS} is entered. Stores the result in {@link Settings#totalPlayers}.
     * </p>
     */
    private static void getPlayers() {
        System.out.printf("How many players will be playing? Enter a number between %d and %d: %n", Settings.MIN_PLAYERS, Settings.MAX_PLAYERS);
        boolean validPlayerCount = false;
        while (!validPlayerCount) {
            try {
                String input = sc.nextLine();
                Settings.totalPlayers = Integer.parseInt(input.trim());

                if (Settings.totalPlayers <= 0) {
                    System.out.println("Please enter a number greater than 0. ");
                    System.out.println("Try again: ");
                    continue;
                }
                if (Settings.totalPlayers < Settings.MIN_PLAYERS) {
                    System.out.printf("This game requires more than %d players to begin. %n", Settings.MIN_PLAYERS);
                    System.out.println("Try again: ");
                    continue;
                }
                if (Settings.totalPlayers > Settings.MAX_PLAYERS) {
                    System.out.printf("This game requires less than %d players to begin. %n", Settings.MAX_PLAYERS);
                    System.out.println("Try again: ");
                    continue;
                }

                // if got passed all these checks then set to true
                validPlayerCount = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a whole number (e.g., 4).");
                System.out.println("Try again: ");
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                System.out.println("Try again: ");
            }
        }
    }

    /**
     * Prompts each player in sequence to enter their name.
     * <p>
     * Delegates validation and uniqueness checking to {@link GameManager#addPlayer(String)}.
     * Re-prompts on any {@link IllegalArgumentException} until a valid, unique name is provided.
     * </p>
     */
    private static void setPlayerNames() {
        for (int i = 1; i <= Settings.totalPlayers; i++) {
            boolean success = false;
            while (!success) {
                System.out.printf("Enter name for Player %d: %n", i);
                String input = sc.nextLine();
                try {
                    gameManager.addPlayer(input); // This handles validation and uniqueness
                    success = true;
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                    System.out.println("Please try a different name.");
                }
            }
        }
    }

    public static void gameOverSequence() {
        try {
            Thread.sleep(250);
            System.out.println("   _____                        ____                 ");
            Thread.sleep(250);
            System.out.println("  / ____|                      / __ \\                ");
            Thread.sleep(250);
            System.out.println(" | |  __  __ _ _ __ ___   ___ | |  | |_   _____ _ __ ");
            Thread.sleep(250);
            System.out.println(" | | |_ |/ _` | '_ ` _ \\ / _ \\| |  | \\ \\ / / _ \\ '__|");
            Thread.sleep(250);
            System.out.println(" | |__| | (_| | | | | | |  __/| |__| |\\ V /  __/ |   ");
            Thread.sleep(250);
            System.out.println("  \\_____|\\__,_|_| |_| |_|\\___| \\____/  \\_/ \\___|_|   ");
            Thread.sleep(800);
            System.out.println();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}

package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Controls the core gameplay loop for the Save Our Planet game.
 * <p>
 * Responsible for managing players, coordinating turns, handling dice rolls,
 * processing purchases and investments, and determining when the game ends.
 * Acts as the central class between the {@link GameBoard}, {@link Player}s,
 * and all {@link Square} interactions.
 * </p>
 */
public class GameManager {

    /**
     * Represents the outcome of a single turn action, used to drive the prompt loop.
     */
    private enum TurnActionResult {
        /** The turn action was valid but the turn is not yet over, re-prompt the player. */
        CONTINUE_PROMPT,
        /** The player has completed their turn advance to the next player. */
        END_TURN,
        /** The game should be terminated immediately. */
        END_GAME
    }

    private final List<Player> players;
    private final GameBoard gameBoard = new GameBoard();
    private final static Random rand = new Random();

    /**
     * Constructs a GameManager with an empty player list.
     */
    public GameManager() {
        this.players = new ArrayList<>();
    }

    /**
     * Adds a new player to the game with the given name.
     * <p>
     * The name is trimmed before validation. Throws an exception if the name
     * is null, already taken (case-insensitive), or if the player limit has been reached.
     * </p>
     *
     * @param name the desired player name
     * @throws IllegalArgumentException if the name is null, duplicate, or the game is full
     */
    public void addPlayer(String name) {

        if (name == null) {
            throw new IllegalArgumentException("Name is detected as null.");
        }

        String cleanName = name.trim();

        if (isNameTaken(cleanName)) {
            throw new IllegalArgumentException("The name '" + cleanName + "' is already taken.");
        }

        if (players.size() >= Settings.MAX_PLAYERS) {
            throw new IllegalArgumentException("The game has reached the maximum number of players.");
        }

        // Create and add the player
        Player newPlayer = new Player(cleanName, players.size());
        players.add(newPlayer);
    }

    /**
     * Checks whether a given name is already in use by an existing player (case-insensitive).
     *
     * @param name the name to check
     * @return {@code true} if the name is already taken
     */
    private boolean isNameTaken(String name) {
        for (Player p : players) {
            if (p.getCleanName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Runs the main game loop, cycling through players in order until the game ends.
     * <p>
     * On each turn, the board is displayed, the current player's status is shown,
     * and they are prompted to roll, invest, view ownership, or quit. The loop ends
     * when a player goes bankrupt, quits, or {@link Settings#gameRunning} is set to false.
     * </p>
     *
     * @param sc the {@link Scanner} used to read player input
     */
    public void playGame(Scanner sc) throws InterruptedException {
        List<Player> players = getPlayers();
        int currentIndex = 0;

        while (Settings.gameRunning) {
            Player currentPlayer = players.get(currentIndex);

            gameBoard.displayBoard(players);

            System.out.println("\n===== " + currentPlayer.getName() + "'s Turn =====");
            System.out.println();
            System.out.println(currentPlayer.getStatus(gameBoard));
            System.out.println();

            if (!rollOrQuit(sc, currentPlayer)) {
                Settings.gameRunning = false;
                break;
            }

            if (currentPlayer.isBankrupt()) {
                Settings.gameRunning = false;
                break;
            }

            currentIndex = (currentIndex + 1) % players.size();
        }

        endGame();
    }

    /**
     * Repeatedly prompts the current player for a turn action until they roll or quit.
     *
     * @param sc the {@link Scanner} for input
     * @param player the player whose turn it is
     * @return {@code true} if the turn ended normally (roll), {@code false} if the game should end
     */
    private boolean rollOrQuit(Scanner sc, Player player) throws InterruptedException {
        while (true) {
            String response = promptTurnAction(sc, player);
            TurnActionResult actionResult = handleTurnAction(response, sc, player);

            if (actionResult == TurnActionResult.END_TURN) {
                return true;
            }
            if (actionResult == TurnActionResult.END_GAME) {
                return false;
            }
        }
    }

    /**
     * Displays the turn action menu and reads the player's choice.
     *
     * @param sc the {@link Scanner} for input
     * @param player the player whose turn it is
     * @return the trimmed input string from the player
     */
    private String promptTurnAction(Scanner sc, Player player) {
        System.out.println(player.getName() + ", what would you like to do?");
        System.out.print("\tR : Roll dice\n\tI : Invest in the efficiency of your squares\n\tO : Display area ownership by field\n\tQ : Quit \n");
        return sc.nextLine().trim();
    }

    /**
     * Processes the player's chosen turn action and returns the resulting state.
     *
     * @param response the player's input string
     * @param sc the {@link Scanner} for input
     * @param player the active player
     * @return a {@link TurnActionResult} indicating how the turn should proceed
     */
    private TurnActionResult handleTurnAction(String response, Scanner sc, Player player) throws InterruptedException {
        if (response.equalsIgnoreCase("R")) {
            executeRollTurn(sc, player);
            return TurnActionResult.END_TURN;
        }
        if (response.equalsIgnoreCase("I")) {
            runInvestmentMode(sc, player);
            return TurnActionResult.CONTINUE_PROMPT;
        }
        if (response.equalsIgnoreCase("O")) {
            gameBoard.displayFieldOwnershipSummary();
            return TurnActionResult.CONTINUE_PROMPT;
        }
        if (response.equalsIgnoreCase("Q")) {
            return quitGame(sc, player) ? TurnActionResult.CONTINUE_PROMPT : TurnActionResult.END_GAME;
        }

        System.out.println("Please enter R, I or Q.");
        return TurnActionResult.CONTINUE_PROMPT;
    }

    /**
     * Executes the dice roll phase of a player's turn.
     * <p>
     * Rolls two dice, moves the player, checks for passing the start square,
     * triggers the landed-on effect, and offers a purchase if the square is unowned.
     * </p>
     *
     * @param sc the {@link Scanner} for input (used if a purchase prompt follows)
     * @param player the active player
     */
    private void executeRollTurn(Scanner sc, Player player) throws InterruptedException {
        int oldPosition = player.getPosition();
        int roll = rollDice();
        player.setPosition(player.getPosition() + roll, gameBoard);

        Square currentSquare = gameBoard.getSquare(player.getPosition());
        checkPassGo(oldPosition, player.getPosition(), player);
        currentSquare.landedOn(player);

        if (currentSquare instanceof AreaSquare area && !area.isOwned()) {
            buyOptions(sc, player, area);
            Thread.sleep(300);
        }
    }

    /**
     * Opens investment mode, allowing the player to apply efficiency upgrades
     * to any area squares they are eligible to develop.
     *
     * @param sc the {@link Scanner} for input
     * @param player the active player
     */
    private void runInvestmentMode(Scanner sc, Player player) {
        System.out.println(Settings.YELLOW_TEXT + "INVESTMENT MODE OPENED");
        List<AreaSquare> developableSquares = gameBoard.getDevelopableSquaresForPlayer(player.getName());

        if (developableSquares.isEmpty()) {
            System.out.println(Settings.YELLOW_TEXT + "You have no fields where you own all areas to develop" + Settings.DEFAULT_TEXT_COLOUR);
            return;
        }

        boolean continueDeveloping = true;
        while (continueDeveloping) {
            String investOption = promptInvestmentOption(sc, developableSquares);
            continueDeveloping = handleInvestmentChoice(sc, player, developableSquares, investOption);
        }
    }

    /**
     * Displays a numbered list of developable squares and reads the player's selection.
     *
     * @param sc the {@link Scanner} for input
     * @param developableSquares the list of squares eligible for development
     * @return the trimmed input string from the player
     */
    private String promptInvestmentOption(Scanner sc, List<AreaSquare> developableSquares) {
        System.out.println(Settings.YELLOW_TEXT + "Which area would you like to invest in? (Type corresponding number, O for ownership summary, or I to exit investment mode): ");
        for (int i = 0; i < developableSquares.size(); i++) {
            AreaSquare currentSquare = developableSquares.get(i);
            String costText;
            if (Settings.MAX_EFFICIENCY_LEVEL <= currentSquare.getEfficiencyLevel()) {
                costText = ", MAX LEVEL REACHED";
            } else if (currentSquare.getEfficiencyLevel() == Settings.MAX_MINOR_DEVELOPMENTS) {
                costText = ", Major development cost: " + currentSquare.getMajorDevCost();
            } else {
                costText = ", Upgrade cost: " + currentSquare.getDevelopmentCost();
            }
            System.out.println(Settings.YELLOW_TEXT + "[" + i + "] " + currentSquare.getName() + Settings.YELLOW_TEXT + ", Current efficiency level: " + currentSquare.getEfficiencyLevel() + costText);
        }
        return sc.nextLine().trim();
    }

    /**
     * Handles the player's choice within investment mode.
     *
     * @param sc the {@link Scanner} for input
     * @param player the active player
     * @param developableSquares the list of squares eligible for investment
     * @param investOption the player's input
     * @return {@code true} to keep investment mode open, {@code false} to close it
     */
    private boolean handleInvestmentChoice(Scanner sc, Player player, List<AreaSquare> developableSquares, String investOption) {
        if (investOption.equalsIgnoreCase("I")) {
            System.out.println(Settings.YELLOW_TEXT + "INVESTMENT MODE CLOSED" + Settings.DEFAULT_TEXT_COLOUR);
            return false;
        }
        if (investOption.equalsIgnoreCase("O")) {
            gameBoard.displayFieldOwnershipSummary();
            return true;
        }
        if (!isNumericInRange(investOption, developableSquares.size())) {
            System.out.println("Please type a valid option, O for ownership summary, or I to exit Invest mode: " + validIndexList(developableSquares.size()));
            return true;
        }

        int investIndex = Integer.parseInt(investOption);
        AreaSquare investSquare = developableSquares.get(investIndex);
        return processInvestmentUpgrade(sc, player, investSquare);
    }

    /**
     * Processes a single efficiency upgrade attempt on the selected area square.
     * <p>
     * Validates affordability and current development level, confirms the investment
     * with the player, and applies the upgrade if confirmed.
     * </p>
     *
     * @param sc the {@link Scanner} for confirmation input
     * @param player the active player
     * @param investSquare the area square selected for investment
     * @return {@code true} to remain in investment mode, {@code false} to exit
     */
    private boolean processInvestmentUpgrade(Scanner sc, Player player, AreaSquare investSquare) {
        int currentEfficiencyLevel = investSquare.getEfficiencyLevel();
        int newEfficiencyLevel = currentEfficiencyLevel + 1;
        boolean isMajorDevelopment = currentEfficiencyLevel == Settings.MAX_MINOR_DEVELOPMENTS;
        int upgradeCost = isMajorDevelopment ? investSquare.getMajorDevCost() : investSquare.getDevelopmentCost();
        int affordability = Settings.MAX_RESOURCES - player.getResources();

        if (currentEfficiencyLevel == Settings.MAX_EFFICIENCY_LEVEL) {
            System.out.println("This area has already reached max efficiency");
            return true;
        }
        if (!player.canAfford(upgradeCost)) {
            System.out.println("You cannot afford this investment, you have left to spend: " + affordability);
            return true;
        }

        String developmentLabel = isMajorDevelopment ? "major development" : "efficiency upgrade";
        System.out.println("You have chosen to invest in a " + developmentLabel + " of " + investSquare.getName() + " from level " + currentEfficiencyLevel + " to level " + newEfficiencyLevel);
        System.out.println("Cost: " + upgradeCost + ", You can afford: " + affordability + " (your Balance: " + player.getResources() + ", Max CO2 allowed: " + Settings.MAX_RESOURCES + ")");

        if (!confirmInvestment(sc)) {
            return true;
        }

        applyInvestmentUpgrade(player, investSquare, newEfficiencyLevel, upgradeCost);
        return askToContinueDeveloping(sc);
    }

    /**
     * Prompts the player to confirm their chosen investment with a Y/N prompt.
     *
     * @param sc the {@link Scanner} for input
     * @return {@code true} if the player confirmed with "Y", {@code false} otherwise
     */
    private boolean confirmInvestment(Scanner sc) {
        System.out.print("Would you like to continue with your investment? Y/N: ");
        String confirmInvestResponse = sc.nextLine().trim();
        return confirmInvestResponse.equalsIgnoreCase("Y");
    }

    /**
     * Applies a confirmed efficiency upgrade to an area square.
     * <p>
     * Displays an animated progress bar, updates the square's efficiency level,
     * and deducts the upgrade cost from the player's resources.
     * </p>
     *
     * @param player the player making the investment
     * @param investSquare the area square being upgraded
     * @param newEfficiencyLevel the new efficiency level to set
     * @param upgradeCost the CO2 cost of the upgrade
     */
    private void applyInvestmentUpgrade(Player player, AreaSquare investSquare, int newEfficiencyLevel, int upgradeCost) {
        try {
            for (int i = 0; i < 10; i++) {
                System.out.print("\r" + "=".repeat(i * 10) + "> " + (i * 10) + "%");
                Thread.sleep(50);
            }
            Thread.sleep(100);

            investSquare.setEfficiencyLevel(newEfficiencyLevel);
            System.out.println("\r" + " ".repeat(110) + "\rDone! " + investSquare.getName() + " upgraded to efficiency level " + newEfficiencyLevel);
            player.addResources(upgradeCost);
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Asks the player whether they wish to continue making further investments
     * before closing investment mode.
     *
     * @param sc the {@link Scanner} for input
     * @return {@code true} if the player wants to continue developing, {@code false} to exit
     */
    private boolean askToContinueDeveloping(Scanner sc) {
        System.out.print("Would you like to continue developing? Y/N: ");
        String continueResponse = sc.nextLine().trim();
        if (continueResponse.equalsIgnoreCase("Y")) {
            return true;
        }

        System.out.println("INVESTMENT MODE CLOSED" + Settings.DEFAULT_TEXT_COLOUR);
        return false;
    }

    /**
     * Checks whether a string is a valid integer within a given range.
     *
     * @param text the string to validate
     * @param upperExclusive the exclusive upper bound (valid range: 0 to upperExclusive - 1)
     * @return {@code true} if the string parses to an integer within the valid range
     */
    private boolean isNumericInRange(String text, int upperExclusive) {
        try {
            int value = Integer.parseInt(text);
            return value >= 0 && value < upperExclusive;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Returns a list of valid index strings for a given range, used in error messages.
     *
     * @param upperExclusive the exclusive upper bound
     * @return a list of string integers from "0" to "upperExclusive - 1"
     */
    private List<String> validIndexList(int upperExclusive) {
        List<String> validInputs = new ArrayList<>();
        for (int i = 0; i < upperExclusive; i++) {
            validInputs.add(Integer.toString(i));
        }
        return validInputs;
    }

    /**
     * Simulates rolling two six-sided dice and prints each result along with the total.
     *
     * @return the sum of both dice rolls (range: 2–12)
     */
    private static int rollDice() {
        int dice1 = rand.nextInt(6) + 1;
        int dice2 = rand.nextInt(6) + 1;

        int diceTotal = dice1 + dice2;
        System.out.printf("Dice 1: %d%nDice 2: %d%nTotal: %d%n", dice1, dice2, diceTotal);

        return diceTotal;
    }

    /**
     * Checks whether a player has passed the start square during their move and
     * applies the pass start bonus if so.
     * <p>
     * Passing start is detected when the new position is less than or equal to the old
     * position, indicating the player has wrapped around the board.
     * </p>
     *
     * @param oldPosition the player's position before rolling
     * @param newPosition the player's position after rolling
     * @param player the player to reward if they passed start
     */
    private void checkPassGo(int oldPosition, int newPosition, Player player) {
        if (oldPosition >= newPosition) {
            if (newPosition != 0) {
                StartSquare start = (StartSquare) gameBoard.getSquare(0);
                start.passedOver(player);
            }
        }
    }

    /**
     * Handles the quit flow for a player, prompting for confirmation.
     *
     * @param sc the {@link Scanner} for input
     * @param player the player attempting to quit
     * @return {@code true} if the player chose not to quit (returned to game),
     *         {@code false} if they confirmed the quit
     */
    private boolean quitGame(Scanner sc, Player player) {
        while (true) {
            System.out.println("Would you like to quit? Y/N");
            String quitResponse = sc.nextLine().trim();

            if (quitResponse.equalsIgnoreCase("Y")) {
                System.out.println(player.getName() + " has quit. The game is over!");
                return false;
            } else if (quitResponse.equalsIgnoreCase("N")) {
                return true;
            } else {
                System.out.println("Please enter Y or N.");
            }
        }
    }

    /**
     * Handles the purchase flow when a player lands on an unowned area square.
     * <p>
     * The landing player is first offered the chance to buy. If they decline or cannot
     * afford it, the option is passed to other players via {@link #passPurchaseOption}.
     * </p>
     *
     * @param sc the {@link Scanner} for input
     * @param player the player who landed on the square
     * @param square the unowned {@link AreaSquare} available for purchase
     */
    private void buyOptions(Scanner sc, Player player, AreaSquare square) {
        boolean running = true;
        while (running) {
            System.out.println("Would you like to purchase " + square.getName() + "? Y/N");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("Y")) {
                if (square.purchase(player)) {
                    running = false;
                } else {
                    List<Player> possibleBuyers = new ArrayList<>(players);
                    possibleBuyers.remove(player);
                    passPurchaseOption(sc, possibleBuyers, square);
                    running = false;
                }
            } else if (input.equalsIgnoreCase("N")) {
                System.out.println("Who wants it?");
                List<Player> possibleBuyers = new ArrayList<>(players);
                possibleBuyers.remove(player);
                passPurchaseOption(sc, possibleBuyers, square);
                running = false;
            } else {
                System.out.println("Please enter Y or N.");
            }
        }

    }

    /**
     * Offers an unowned square to other players in random order until one buys it
     * or all decline.
     *
     * @param sc the {@link Scanner} for input
     * @param possibleBuyers the list of players eligible to purchase (excluding the lander)
     * @param square the {@link AreaSquare} being offered
     */
    private void passPurchaseOption(Scanner sc, List<Player> possibleBuyers, AreaSquare square) {
        do {
            int random = rand.nextInt(possibleBuyers.size());
            Player player = possibleBuyers.get(random);

            System.out.println(player.getName() + ": Would you like to purchase " + square.getName() + "? Y/N");
            String input = sc.nextLine().trim();
            if (input.equalsIgnoreCase("Y")) {
                if (square.purchase(player)) {
                    break;
                } else {
                    possibleBuyers.remove(player);
                }
            } else if (input.equalsIgnoreCase("N")) {
                System.out.println(player.getName() + " doesn't want " + square.getName() + ".");
                possibleBuyers.remove(player);
            } else {
                System.out.println("Please enter Y or N.");
            }
        } while (!possibleBuyers.isEmpty());
    }

    /**
     * Ends the game and displays the final leaderboard.
     * <p>
     * Prints each player's final CO2 balance and announces the winner as
     * the player with the lowest CO2 total.
     * </p>
     */
    void endGame() {
        StartGame.gameOverSequence();

        // final results
        System.out.printf("%-15s | %-15s%n", "Player Name", "Final CO2");
        System.out.println("-------------------------------------------");

        Player winner = players.get(0);

        for (Player p : players) {
            System.out.printf("%-15s | %-15d%n", p.getName(), p.getResources());
            if (p.getResources() < winner.getResources()) {
                winner = p;
            }
        }
        System.out.println("-------------------------------------------");
        System.out.println("WINNER: " + winner.getName());
        System.out.println("-------------------------------------------\n");
    }

    /**
     * Returns the list of all players in the current game.
     *
     * @return the list of {@link Player} objects
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Prints the full game rules to the console with colour formatting and timed delays.
     * <p>
     * Covers the goal, setup, turn mechanics, field development system, and end conditions.
     * </p>
     */
    public void printRules() {
        try {
            System.out.printf("%s%s%s Save Our Planet - Rules %s%s%n%n", Settings.BOLD_TEXT, Settings.GREEN_TEXT, Settings.EMOJI_GLOBE, Settings.EMOJI_GLOBE, Settings.DEFAULT_TEXT_COLOUR);

            System.out.printf("%s%s The Goal:%s%n", Settings.YELLOW_TEXT, Settings.EMOJI_TARGET, Settings.DEFAULT_TEXT_COLOUR);
            System.out.printf("You're trying to get your CO₂ score as low as possible, ideally down to zero.%nThink of it as being the most eco-friendly player. Lower is better!%n%n");
            Thread.sleep(700);

            System.out.printf("%s%s Getting Started:%s%n", Settings.YELLOW_TEXT, Settings.EMOJI_FLAG, Settings.DEFAULT_TEXT_COLOUR);
            System.out.printf("2-4 players each start with %s120 CO₂%s points but are capped at %s180 CO₂%s as this is dangerously high and may cause global warming.%nEveryone registers their name and takes turns in order.%n%n",
                    Settings.GREEN_TEXT, Settings.DEFAULT_TEXT_COLOUR,
                    Settings.RED_TEXT, Settings.DEFAULT_TEXT_COLOUR);
            Thread.sleep(700);

            System.out.printf("%s%s On Your Turn:%s%n", Settings.YELLOW_TEXT, Settings.EMOJI_DICE, Settings.DEFAULT_TEXT_COLOUR);
            System.out.printf("You roll two dice (giving you a total between 2 and 12) and move that many spaces around the 12-square board.%nDepending on where you land, different things happen:%n");
            System.out.printf("  %s%s  Sustainability Hub (Start)%s — You lose CO₂ points automatically whenever you land on or pass this square (that's a good thing!).%n",
                    Settings.GREEN_TEXT, Settings.EMOJI_RECYCLE, Settings.DEFAULT_TEXT_COLOUR);
            System.out.printf("  %s%s Carbon Neutral Zone%s — Nothing happens, just think about how you are helping the environment.%n",
                    Settings.CYAN_TEXT, Settings.EMOJI_NO_ENTRY, Settings.DEFAULT_TEXT_COLOUR);
            System.out.printf("  %s%s Unowned Area%s — You add CO₂ points to take charge and build on it as this creates CO₂. If you don't want it, the other players get a chance to take it.%n",
                    Settings.BLUE_TEXT, Settings.EMOJI_SEEDLING, Settings.DEFAULT_TEXT_COLOUR);
            System.out.printf("  %s%s Area You Own%s — No cost, you're home.%n",
                    Settings.GREEN_TEXT, Settings.EMOJI_HOUSE, Settings.DEFAULT_TEXT_COLOUR);
            System.out.printf("  %s%s Opponent's Area%s — Your opponent (the owner) gets to hand over CO₂ points to the player who landed on the area (bad for you, good for them). The more developed the area, the more it adds to your total.%n%n",
                    Settings.RED_TEXT, Settings.EMOJI_WIND, Settings.DEFAULT_TEXT_COLOUR);
            Thread.sleep(700);

            System.out.printf("%s%s  Fields and Development:%s%n", Settings.YELLOW_TEXT, Settings.EMOJI_DIVIDERS, Settings.DEFAULT_TEXT_COLOUR);
            System.out.printf("The 10 area squares are grouped into 4 fields. Once you own every area in a field, you can start developing those areas on any turn, even if you're not standing on them.%nEach area can have up to 3 regular %s developments, and after that you can build a %s major development. Developing costs you CO₂ points, but it means opponents pay more when they land there.%n",
                    Settings.EMOJI_HAMMER, Settings.EMOJI_STAR);
            System.out.printf("  %sField 1%s - Waste Reduction (Focuses on everyday actions that reduce waste and emissions)%n",
                    Settings.GREEN_TEXT, Settings.DEFAULT_TEXT_COLOUR);
            System.out.printf("  %sField 2%s - Sustainable Transport (Represents low‑carbon mobility infrastructure)%n",
                    Settings.CYAN_TEXT, Settings.DEFAULT_TEXT_COLOUR);
            System.out.printf("  %sField 3%s - Renewable Energy (Large‑scale clean energy generation)%n",
                    Settings.BLUE_TEXT, Settings.DEFAULT_TEXT_COLOUR);
            System.out.printf("  %sField 4%s - Environmental Restoration (High‑impact, large‑scale environmental recovery projects)%n%n",
                    Settings.YELLOW_TEXT, Settings.DEFAULT_TEXT_COLOUR);
            Thread.sleep(700);

            System.out.printf("%s%s%s Game Over%s%n", Settings.BOLD_TEXT, Settings.RED_TEXT, Settings.EMOJI_CROSS, Settings.DEFAULT_TEXT_COLOUR);
            System.out.printf("The game ends if any player hits %s0 CO₂%s (or below), or if someone chooses to quit.%nEveryone's final CO₂ is shown, and the player who shed the most CO₂ wins %s%n",
                    Settings.RED_TEXT, Settings.DEFAULT_TEXT_COLOUR, Settings.EMOJI_TROPHY);
            System.out.printf("They've done the most to Save Our Planet!%n%n");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

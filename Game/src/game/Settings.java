package game;

import java.util.ArrayList;
import java.util.List;

/**
 * Central class for the Save Our Planet game.
 * <p>
 * Holds all shared constants and global states used across the game,
 * including player limits, resource values, development costs, emoji and
 * colour codes for console output. All fields are {@code static} so they
 * are accessible without instantiation.
 * </p>
 */
public class Settings {

    //End Game boolean
    public static boolean gameRunning = true;

    //Players
    // Min and Max players can be easily changed
    public final static int MIN_PLAYERS = 2;
    public final static int MAX_PLAYERS = 4;
    public static int totalPlayers;

    // Starting Resources and set resource amounts
    public final static int STARTING_RESOURCES = 120;
    public final static int MAX_RESOURCES = 180;
    public final static int PASS_START_MINUS_RESOURCES = 15;
    public final static int BASE_COST = 10;

    //Field Acquiring
    public final static int FIELD1_COST = 20;
    public final static int FIELD2_COST = 25;
    public final static int FIELD3_COST = 30;
    public final static int FIELD4_COST = 40;

    //Developments
    // 3 minor and 1 major
    public static final int MAX_MINOR_DEVELOPMENTS = 3;
    public static final int MAX_EFFICIENCY_LEVEL = 4;
    //Costs
    public static final int MAJOR_DEVELOPMENT_COST = 35;

    //Colours
    public static final String RED_TEXT = "\033[0;31m";
    public static final String GREEN_TEXT = "\033[0;32m";
    public static final String BLUE_TEXT = "\033[0;34m";
    public static final String YELLOW_TEXT = "\033[0;33m";
    public static final String CYAN_TEXT = "\033[0;36m";

    public static final String RED_TEXT_BRIGHT = "\033[0;91m";
    public static final String GREEN_TEXT_BRIGHT = "\033[0;92m";
    public static final String BLUE_TEXT_BRIGHT = "\033[0;94m";
    public static final String YELLOW_TEXT_BRIGHT = "\033[0;93m";
    public static final String CYAN_TEXT_BRIGHT = "\033[0;96m";

    public static final String BOLD_TEXT = "\033[1m";
    public static final String DIM_TEXT = "\033[2m";
    public static final String UNDERLINE_TEXT = "\033[4m";

    public static final String DEFAULT_TEXT_COLOUR = "\033[0m";

    public static final String[] PLAYER_COLOUR = {
            RED_TEXT_BRIGHT,
            GREEN_TEXT_BRIGHT,
            BLUE_TEXT_BRIGHT,
            YELLOW_TEXT_BRIGHT
    };

    //Emojis

    // 🌍 Globe
    public static final String EMOJI_GLOBE = "\uD83C\uDF0D";

    // 🎯 bullseye
    public static final String EMOJI_TARGET = "\uD83C\uDFAF";

    // 🏁 Chequered flag
    public static final String EMOJI_FLAG = "\uD83C\uDFC1";

    // 🎲 Game die
    public static final String EMOJI_DICE = "\uD83C\uDFB2";

    // ♻️ Recycling symbol
    public static final String EMOJI_RECYCLE = "\u267B\uFE0F";

    // 🚫 Prohibited sign
    public static final String EMOJI_NO_ENTRY = "\uD83D\uDEAB";

    // 🌱 Seedling
    public static final String EMOJI_SEEDLING = "\uD83C\uDF31";

    // 🏠 House
    public static final String EMOJI_HOUSE = "\uD83C\uDFE0";

    // 💨 Wind
    public static final String EMOJI_WIND = "\uD83D\uDCA8";

    // 🗂️ Folder
    public static final String EMOJI_DIVIDERS = "\uD83D\uDDC2\uFE0F";

    // 🔨 Hammer
    public static final String EMOJI_HAMMER = "\uD83D\uDD28";

    // ⭐ Star
    public static final String EMOJI_STAR = "\u2B50";

    // ❌ X
    public static final String EMOJI_CROSS = "\u274C";

    // 🏆 Trophy
    public static final String EMOJI_TROPHY = "\uD83C\uDFC6";

    //CLEAR SCREEN
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}

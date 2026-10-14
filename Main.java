
import engine.*;
import ui.*;

public class Main {

    public static void main(String[] args) {
        RestaurantGame game = new RestaurantGame();
        GameCLI cli = new GameCLI(game);

        cli.displayWelcome();

        while (!game.isGameOver()) {
            game.step1_processArrivals();
            cli.displayStartOfTurn();
            cli.promptStaffActions(); // Calls engine actions internally
            game.resolveTurnEndPhases(); // Steps 4, 5, 6
            cli.displayEndOfTurnSummary();
        }

        cli.displayGameResult(); // Victory or Defeat
    }
}

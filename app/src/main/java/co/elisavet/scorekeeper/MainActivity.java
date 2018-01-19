package co.elisavet.scorekeeper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //UI objects
    private TextView playerAView;
    private TextView playerBView;
    private TextView scoreAView;
    private TextView scoreBView;
    private TextView bingosAView;
    private TextView bingosBView;
    private EditText scoreAInput;
    private EditText scoreBInput;
    private ImageView playerATurnIcon;
    private ImageView playerBTurnIcon;

    //String constants for keys
    private static final String SCORE_CNT_A = "scorePlayerA";
    private static final String SCORE_CNT_B = "scorePlayerB";
    private static final String BINGOS_CNT_A = "bingosPlayerA";
    private static final String BINGOS_CNT_B = "bingosPlayerB";
    private static final String CURRENT_PLAYER_CNT = "currentPlayer";

    //Declare and initialize global vars
    private int scorePlayerA = 0;
    private int scorePlayerB = 0;
    private int bingosPlayerA = 0;
    private int bingosPlayerB = 0;
    private String currentPlayer = "";

    //Player Names
    public String playerAName = "";
    public String playerBName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize
        playerAView = (TextView) findViewById(R.id.player_a_name);
        playerBView = (TextView) findViewById(R.id.player_b_name);
        scoreAView = (TextView) findViewById(R.id.player_a_score);
        scoreBView = (TextView) findViewById(R.id.player_b_score);
        bingosAView = (TextView) findViewById(R.id.player_a_bingos);
        bingosBView = (TextView) findViewById(R.id.player_b_bingos);
        scoreAInput = (EditText) findViewById(R.id.player_a_add_score);
        scoreBInput = (EditText) findViewById(R.id.player_b_add_score);
        playerATurnIcon = (ImageView) findViewById(R.id.player_a_icon);
        playerBTurnIcon = (ImageView) findViewById(R.id.player_b_icon);

        // get the intent from StartGameActivity
        Intent intent = getIntent();
        playerAName = intent.getExtras().getString(StartGameActivity.PLAYER_A_NAME_EXTRA);
        playerBName = intent.getExtras().getString(StartGameActivity.PLAYER_B_NAME_EXTRA);

        //Player A plays first
        currentPlayer = playerAName;

        //Display initial global values
        displayForPlayerA(scorePlayerA);
        displayForPlayerB(scorePlayerB);
        displayBingosForPlayerA(bingosPlayerA);
        displayBingosForPlayerB(bingosPlayerB);
        displayCurrentPlayer(currentPlayer);
        displayPlayersNames(playerAName, playerBName);
    }

    // https://stackoverflow.com/questions/151777/saving-android-activity-state-using-save-instance-state
    //prevent the application from restarting when changing orientation. Saving global vars between activity states.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putInt(SCORE_CNT_A, scorePlayerA);
        savedInstanceState.putInt(SCORE_CNT_B, scorePlayerB);
        savedInstanceState.putInt(BINGOS_CNT_A, bingosPlayerA);
        savedInstanceState.putInt(BINGOS_CNT_B, bingosPlayerB);
        savedInstanceState.putString(CURRENT_PLAYER_CNT, currentPlayer);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        scorePlayerA = savedInstanceState.getInt(SCORE_CNT_A);
        scorePlayerB = savedInstanceState.getInt(SCORE_CNT_B);
        bingosPlayerA = savedInstanceState.getInt(BINGOS_CNT_A);
        bingosPlayerB = savedInstanceState.getInt(BINGOS_CNT_B);
        currentPlayer = savedInstanceState.getString(CURRENT_PLAYER_CNT);
        //Display saved global vars values
        displayForPlayerA(scorePlayerA);
        displayForPlayerB(scorePlayerB);
        displayBingosForPlayerA(bingosPlayerA);
        displayBingosForPlayerB(bingosPlayerB);
        displayCurrentPlayer(currentPlayer);
    }

    /**
     * Increase the score for Team A by points indicated in the EditText.
     */
    public void addPointsForPlayerA(View v) {

        if (currentPlayer.equals(playerAName)) {

            String scoreAInputText = scoreAInput.getText().toString();
            //if no input from EditText then Integer.parseInt will resolve to an exception and the App will terminate
            //lets catch the error and display a Toast if it occurs
            if (!scoreAInputText.equals("")) {
                //https://stackoverflow.com/questions/4903515/how-do-i-return-an-int-from-edittext-android
                int points = Integer.parseInt(scoreAInputText);
                scorePlayerA += points;
                displayForPlayerA(scorePlayerA);
                switchPlayer(playerAName);
            } else {
                displayToastMessage(getString(R.string.enter_number_message));
            }
        } else {
            displayToastMessage(getString(R.string.turn_message));
        }

    }

    /**
     * BINGO! If a player plays seven tiles on a turn, it's a Bingo.  Let’s add a premium of 50 points!
     * Increase the score for Player A by 50 points and increase the number of bingos by 1
     *
     * @param v
     */
    public void addBingoPointsForPlayerA(View v) {
        if (currentPlayer.equals(playerAName)) {
            bingosPlayerA += 1;
            displayBingosForPlayerA(bingosPlayerA);
            scorePlayerA += 50;
            displayForPlayerA(scorePlayerA);
            displayToastMessage(getString(R.string.bingo_message));
        } else {
            displayToastMessage(getString(R.string.turn_message));
        }
    }

    /**
     * PASS! You may use a turn to exchange all, some, or none of the letters.
     * This ends your turn and awards you 0 points.
     *
     * @param v
     */
    public void passForPlayerA(View v) {
        if (currentPlayer.equals(playerAName)) {
            switchPlayer(playerAName);
        } else {
            displayToastMessage(getString(R.string.turn_message));
        }
    }

    /**
     * Player A has used all of his or her letters,
     * the sum of the other players' unplayed letters is added to that player's score.
     * Other player's score is reduced by the sum of his or her unplayed letters.
     *
     * @param v
     */
    public void endGamePlayerA(View v) {
        int scoreBeforeTilesCountForPlayerA = scorePlayerA;
        int scoreBeforeTilesCountForPlayerB = scorePlayerB;
        //This player ended the game if this button is pressed
        currentPlayer = playerAName;
        String scoreAInputText = scoreAInput.getText().toString();
        //if no input from EditText then Integer.parseInt will resolve to an exception and the App will terminate
        //lets catch the error and display a Toast if it occurs
        if ((!scoreAInputText.equals(""))) {
            //https://stackoverflow.com/questions/4903515/how-do-i-return-an-int-from-edittext-android
            int points = Integer.parseInt(scoreAInputText);
            scorePlayerA += points;
            scorePlayerB -= points;
            displayForPlayerA(scorePlayerA);
            displayForPlayerB(scorePlayerB);
            displayWinner(scorePlayerA, scorePlayerB, scoreBeforeTilesCountForPlayerA, scoreBeforeTilesCountForPlayerB);

        } else {
            displayToastMessage(getString(R.string.end_game_message));
        }
    }

    /**
     * Increase the score for Player B by points indicated in the EditText.
     */
    public void addPointsForPlayerB(View v) {
        if (currentPlayer.equals(playerBName)) {
            String scoreBInputText = scoreBInput.getText().toString();
            //if no input from EditText then Integer.parseInt will resolve to an exception and the App will terminate
            //lets catch the error and display a Toast if it occurs
            if ((!scoreBInputText.equals(""))) {
                //https://stackoverflow.com/questions/4903515/how-do-i-return-an-int-from-edittext-android
                int points = Integer.parseInt(scoreBInputText);
                scorePlayerB += points;
                displayForPlayerB(scorePlayerB);
                switchPlayer(playerBName);
            } else {
                displayToastMessage(getString(R.string.enter_number_message));
            }
        } else {
            displayToastMessage(getString(R.string.turn_message));
        }
    }

    /**
     * Increase the score for Player A by 50 points and increase the number of bingos by 1
     */
    public void addBingoPointsForPlayerB(View v) {
        if (currentPlayer.equals(playerBName)) {
            bingosPlayerB += 1;
            displayBingosForPlayerB(bingosPlayerB);
            scorePlayerB += 50;
            displayForPlayerB(scorePlayerB);
            displayToastMessage(getString(R.string.bingo_message));
        } else {
            displayToastMessage(getString(R.string.turn_message));
        }
    }

    /**
     * PASS! You may use a turn to exchange all, some, or none of the letters.
     * This ends your turn and awards you 0 points.
     *
     * @param v
     */
    public void passForPlayerB(View v) {

        if (currentPlayer.equals(playerBName)) {
            switchPlayer(playerBName);
        } else {
            displayToastMessage(getString(R.string.turn_message));
        }
    }

    /**
     * Player B has used all of his or her letters,
     * the sum of the other players' un-played letters is added to that player's score.
     * Other player's score is reduced by the sum of his or her unplayed letters
     *
     * @param v
     */
    public void endGamePlayerB(View v) {
        int scoreBeforeTilesCountForPlayerA = scorePlayerA;
        int scoreBeforeTilesCountForPlayerB = scorePlayerB;
        //This player ended the game if this button is pressed
        currentPlayer = playerBName;
        String scoreBInputText = scoreBInput.getText().toString();
        //if no input from EditText then Integer.parseInt will resolve to an exception and the App will terminate
        //lets catch the error and display a Toast if it occurs
        if ((!scoreBInputText.equals(""))) {
            //https://stackoverflow.com/questions/4903515/how-do-i-return-an-int-from-edittext-android
            int points = Integer.parseInt(scoreBInputText);
            scorePlayerB += points;
            scorePlayerA -= points;
            displayForPlayerA(scorePlayerA);
            displayForPlayerB(scorePlayerB);
            displayWinner(scorePlayerA, scorePlayerB, scoreBeforeTilesCountForPlayerA, scoreBeforeTilesCountForPlayerB);
        } else {
            displayToastMessage(getString(R.string.end_game_message));
        }
    }

    /**
     * Reset the score, starting a new game.
     */
    public void resetGame(View v) {
        scorePlayerA = 0;
        scorePlayerB = 0;
        bingosPlayerA = 0;
        bingosPlayerB = 0;
        currentPlayer = playerAName;
        displayForPlayerA(scorePlayerA);
        displayForPlayerB(scorePlayerB);
        displayBingosForPlayerA(bingosPlayerA);
        displayBingosForPlayerB(bingosPlayerB);
        displayCurrentPlayer(currentPlayer);
        scoreAInput.requestFocus();
        scoreAInput.setText("");
        scoreBInput.setText("");
    }


    private void switchPlayer(String currentPlayerName) {
        if (currentPlayerName.equals(playerAName)) {
            currentPlayer = playerBName;
            scoreAInput.setText("");
            scoreBInput.requestFocus();
            displayCurrentPlayer(currentPlayer);
        } else if (currentPlayerName.equals(playerBName)) {
            currentPlayer = playerAName;
            scoreBInput.setText("");
            scoreAInput.requestFocus();
            displayCurrentPlayer(currentPlayer);
        }
    }

    private void displayCurrentPlayer(String currentPlayerName) {

        if (currentPlayerName.equals(playerAName)) {
            playerATurnIcon.setImageResource(R.drawable.player_go);
            playerBTurnIcon.setImageResource(R.drawable.player_stop);
        } else if (currentPlayerName.equals(playerBName)) {
            playerATurnIcon.setImageResource(R.drawable.player_stop);
            playerBTurnIcon.setImageResource(R.drawable.player_go);
        }

    }

    private void displayWinner(int playerAFinalScore, int playerBFinalScore, int playerAScoreBeforeUnUsedTiles, int playerBScoreBeforeUnUsedTiles) {
        String winner = "";
        //The player with the highest final score wins the game.
        if (playerAFinalScore > playerBFinalScore) {
            winner = playerAName;
        } else if (playerAFinalScore < playerBFinalScore) {
            winner = playerBName;
        } else {
            //In case of a tie, the player with the highest score before adding or deducting unplayed letters wins.
            if (playerAScoreBeforeUnUsedTiles > playerBScoreBeforeUnUsedTiles) {
                winner = playerAName;
            } else if (playerAScoreBeforeUnUsedTiles < playerBScoreBeforeUnUsedTiles) {
                winner = playerBName;
            }
        }

        displayToastMessage(getString(R.string.winner, winner));
    }

    /**
     * Displays the given score for Player A.
     */
    private void displayForPlayerA(int score) {
        scoreAView.setText(String.valueOf(score));
    }

    /**
     * Displays the given score for Player B.
     */
    private void displayForPlayerB(int score) {
        scoreBView.setText(String.valueOf(score));
    }

    /**
     * Displays the given bingo score for Player A.
     */
    private void displayBingosForPlayerA(int bingos) {
        bingosAView.setText(String.valueOf(bingos));
    }

    /**
     * Displays the given bingo score for Player B.
     */
    private void displayBingosForPlayerB(int bingos) {
        bingosBView.setText(String.valueOf(bingos));
    }

    /**
     * Displays the given player names.
     */
    private void displayPlayersNames(String playerAName, String playerBName){
        playerAView.setText(playerAName);
        playerBView.setText(playerBName);

    };

    //https://developer.android.com/guide/topics/ui/notifiers/toasts.html
    private void displayToastMessage(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

}
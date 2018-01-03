package co.elisavet.scorekeeper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class StartGameActivity extends AppCompatActivity {

    //https://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android-application/40569184#40569184
    static final String PLAYER_A_NAME_EXTRA = "co.elisavet.scorekeeper.PLAYER_A_NAME";
    static final String PLAYER_B_NAME_EXTRA = "co.elisavet.scorekeeper.PLAYER_B_NAME";

    private EditText PlayerAEditText;
    private EditText PlayerBEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        PlayerAEditText = (EditText) findViewById(R.id.player_a_name);
        PlayerBEditText = (EditText) findViewById(R.id.player_b_name);
    }
    /**
     * Get the names of the players and start game!
     *
     * @param v
     */
    public void startGame(View v){

        String playerAName = PlayerAEditText.getText().toString();
        String playerBName = PlayerBEditText.getText().toString();

        if (!playerAName.trim().isEmpty() && !playerBName.trim().isEmpty()) {
            //https://stackoverflow.com/a/45979367/9132003
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(PLAYER_A_NAME_EXTRA, playerAName);
            intent.putExtra(PLAYER_B_NAME_EXTRA, playerBName);
            //https://developer.android.com/training/basics/firstapp/starting-activity.html
            startActivity(intent);
        } else {
            displayToastMessage("Please enter your names to start the game!");
        }

    }

    //https://developer.android.com/guide/topics/ui/notifiers/toasts.html
    private void displayToastMessage(String message) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}

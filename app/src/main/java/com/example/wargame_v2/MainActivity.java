package com.example.wargame_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

//glide
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int PLAYER1_TURN = 1;
    private static final int PLAYER2_TURN = 2;
    private static final int LARGE_ATTACK_POINTS = 50;
    private static final int MEDIUM_ATTACK_POINTS = 30;
    private static final int SMALL_ATTACK_POINTS = 10;

    private ProgressBar player1_PB;
    private ProgressBar player2_PB;
    private ImageView player1_image;
    private ImageView player2_image;
    private ImageView player1_cube;
    private ImageView player2_cube;
    private Button largeAttack_player1;
    private Button mediumAttack_player1;
    private Button smallAttack_player1;
    private Button largeAttack_player2;
    private Button mediumAttack_player2;
    private Button smallAttack_player2;
    private Button pick;
    private ArrayList<Button> player1_Buttons;
    private ArrayList<Button> player2_Buttons;
    private Random rand = new Random();
    private final String PLAYER1_NAME = "Superman";
    private final String PLAYER2_NAME = "Ironman";
    private Handler handler;
    private Runnable runnable;
    private final int DELAY = 2000;
    private int turn = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* initialize values */
        setValues();

        /* add buttons to lists */
        initialize_player1_list();
        initialize_player2_list();

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start game only if players didn't get the same value in dice
                if (choose_who_start() == true) {
                    /* enable buttons of the player that won in dice */
                    Enable_buttons();
                }
            }
        });

        runnable = new Runnable() {
            @Override
            public void run() {
                /* choose random attack */
                int number_of_attack = rand.nextInt(3) + 1;
                /* update life bar */
                setProgressBar(number_of_attack);
                /* switch turn -> Turns off the current player's buttons and activates the opponent's */
                switchTurn();

                if (!gameOver())
                    handler.postDelayed(this, DELAY);
            }
        };
    }

    /* enable buttons of the player that won in dice */
    private void Enable_buttons() {
        if(turn == PLAYER1_TURN) {
            for (Button btn : player1_Buttons)
                btn.setEnabled(true);
        } else if(turn == PLAYER2_TURN) {
            for (Button btn : player2_Buttons)
                btn.setEnabled(true);
        }
    }

    private boolean choose_who_start() {
        /* drop the cubes and choose random numbers */
        int player1_number = diceCube(player1_cube);
        int player2_number = diceCube(player2_cube);
        if(player1_number > player2_number) {
            turn = PLAYER1_TURN;
            pick.setEnabled(false);
            return true;
        }
        else if (player2_number > player1_number) {
            turn  = PLAYER2_TURN;
            pick.setEnabled(false);
            return true;
        }
        // even numbers
        return false;
    }

    /* make random number for each player and update the photo of the cubes */
    private int diceCube(ImageView cube_image) {
        int random = rand.nextInt(6) + 1;
        /* change photo of Image View */
        change_IV(cube_image, random);
        return random;
    }

    /* change cube photo using glide */
    private void change_IV(ImageView cube_image, int number) {
        switch(number) {
            case 1:
                setImage(cube_image, ContextCompat.getDrawable(this, R.drawable.dice_1));
                break;
            case 2:
                setImage(cube_image, ContextCompat.getDrawable(this, R.drawable.dice_2));
                break;
            case 3:
                setImage(cube_image, ContextCompat.getDrawable(this, R.drawable.dice_3));
                break;
            case 4:
                setImage(cube_image, ContextCompat.getDrawable(this, R.drawable.dice_4));
                break;
            case 5:
                setImage(cube_image, ContextCompat.getDrawable(this, R.drawable.dice_5));
                break;
            case 6:
                setImage(cube_image, ContextCompat.getDrawable(this, R.drawable.dice_6));
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(runnable, DELAY);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }

    private void setProgressBar(int number_of_attack) {
        if (turn == PLAYER1_TURN) {
            switch (number_of_attack) {
                case 1:
                    smallAttack_player1.setEnabled(false);
                    decreasePB(player2_PB, SMALL_ATTACK_POINTS);
                    break;
                case 2:
                    mediumAttack_player1.setEnabled(false);
                    decreasePB(player2_PB, MEDIUM_ATTACK_POINTS);
                    break;
                case 3:
                    largeAttack_player1.setEnabled(false);
                    decreasePB(player2_PB, LARGE_ATTACK_POINTS);
                    break;
            }
        } else if (turn == PLAYER2_TURN) {
            switch (number_of_attack) {
                case 1:
                    smallAttack_player2.setEnabled(false);
                    decreasePB(player1_PB, SMALL_ATTACK_POINTS);
                    break;
                case 2:
                    mediumAttack_player2.setEnabled(false);
                    decreasePB(player1_PB, MEDIUM_ATTACK_POINTS);
                    break;
                case 3:
                    largeAttack_player2.setEnabled(false);
                    decreasePB(player1_PB, LARGE_ATTACK_POINTS);
                    break;
            }
        }
    }

    private void decreasePB(ProgressBar pb, int points) {
        pb.setProgress(pb.getProgress() - points);
        if(pb.getProgress() < 40) {
            pb.setProgressDrawable(ContextCompat.getDrawable(this, R.drawable.red_progress_bar));
        }
    }

    /* if game over -> send victory name to Victory Activity and switch activity */
    private boolean gameOver() {
        if(player1_PB.getProgress() == 0 || player2_PB.getProgress() == 0) {
            openVictoryActivity();
            finish();
            return true;
        }
        return false;
    }

    private void openVictoryActivity() {
        Intent intent = new Intent(MainActivity.this, VictoryActivity.class);
        if(turn == PLAYER1_TURN)
            intent.putExtra(VictoryActivity.EXTRA_KEY_VICTORY, PLAYER2_NAME);
        else
            intent.putExtra(VictoryActivity.EXTRA_KEY_VICTORY, PLAYER1_NAME);
        startActivity(intent);
    }

    private void initialize_player1_list() {
        player1_Buttons = new ArrayList<>();
        player1_Buttons.add(largeAttack_player1);
        player1_Buttons.add(mediumAttack_player1);
        player1_Buttons.add(smallAttack_player1);
    }

    private void initialize_player2_list() {
        player2_Buttons = new ArrayList<>();
        player2_Buttons.add(largeAttack_player2);
        player2_Buttons.add(mediumAttack_player2);
        player2_Buttons.add(smallAttack_player2);
    }

    private void setValues() {
        player1_PB = findViewById(R.id.main_PB_player1_PB);
        player2_PB = findViewById(R.id.main_PB_player2_PB);
        largeAttack_player1 = findViewById(R.id.main_BTN_50pt_player1);
        mediumAttack_player1 = findViewById(R.id.main_BTN_30pt_player1);
        smallAttack_player1 = findViewById(R.id.main_BTN_10pt_player1);
        largeAttack_player2 = findViewById(R.id.main_BTN_50pt_player2);
        mediumAttack_player2 = findViewById(R.id.main_BTN_30pt_player2);
        smallAttack_player2 = findViewById(R.id.main_BTN_10pt_player2);
        pick = findViewById(R.id.main_BTN_pick);
        player1_image = findViewById(R.id.main_IV_player1);
        player2_image = findViewById(R.id.main_IV_player2);
        player1_cube = findViewById(R.id.main_IV_player1_Cube);
        player2_cube = findViewById(R.id.main_IV_player2_Cube);

        /* set images using glide library*/
        setImage(player1_image, ContextCompat.getDrawable(this, R.drawable.superman));
        setImage(player2_image, ContextCompat.getDrawable(this, R.drawable.iron));
        setImage(player1_cube, ContextCompat.getDrawable(this, R.drawable.dice_1));
        setImage(player2_cube, ContextCompat.getDrawable(this, R.drawable.dice_2));

        /* initialize value of progress bars to 100 */
        player1_PB.setProgress(100);
        player2_PB.setProgress(100);
    }

    /* set images using glide library*/
    private void setImage(ImageView iv, Drawable photo) {
        Glide.with(MainActivity.this)
                .load(photo)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(iv);
    }

    private void switchTurn() {
        ArrayList<Button> switchTo;
        ArrayList<Button> current;
        if (turn == PLAYER1_TURN) {
            current = player1_Buttons;
            switchTo = player2_Buttons;
        }
        else {
            current = player2_Buttons;
            switchTo = player1_Buttons;
        }
        makeSwitch(current, switchTo);
    }

    private void makeSwitch(ArrayList<Button> current, ArrayList<Button> switchTo) {
        for(Button btn: current)
            btn.setEnabled(false);
        for(Button btn: switchTo)
            btn.setEnabled(true);
        changeTurn();
    }

    private void changeTurn() {
        if (turn == PLAYER1_TURN)
            turn = PLAYER2_TURN;
        else
            turn = PLAYER1_TURN;
    }
}
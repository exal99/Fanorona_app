package sweden.alexander.fanorona;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.NumberPicker;


public class MainActivity extends AppCompatActivity {
    private Fanorona game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Conferm?")
                .setView(R.layout.timer_dialog)
                .create();

//        NumberPicker np = (NumberPicker) dialog.findViewById(R.id.minutes);
//        np.setMaxValue(60);
//        np.setMinValue(0);
        dialog.show();
        //setContentView(R.layout.timer_dialog);
//        setContentView(R.layout.activity_main);
//        FragmentManager fragmentManager = getFragmentManager();
//        game = new Fanorona();
//        Fragment fragment = game;
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, fragment)
//                .commit();
    }

    @Override
    public void onBackPressed() {
        if (game.getCurrScreen() == Fanorona.Screen.GAME) {
            new AlertDialog.Builder(this)
                    .setTitle("Quit current game?")
                    .setMessage("Do you really want to exit the current game? All progress will be lost")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            game.back();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
        } else {
            game.back();
        }
    }
}

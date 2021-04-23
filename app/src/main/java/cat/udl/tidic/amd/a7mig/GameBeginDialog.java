package cat.udl.tidic.amd.a7mig;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class GameBeginDialog extends DialogFragment {

    private static final String TAG = "GameBeginDialog";
    private LinearLayout gameSettingLayout;
    private EditText players;
    private int jugadores;
    private View rootView;
    private GameActivity activity;

    public static GameBeginDialog newInstance(GameActivity activity) {
        GameBeginDialog dialog = new GameBeginDialog();
        dialog.activity = activity;
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        initViews();
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(rootView)
                .setTitle(R.string.game_dialog_title)
                .setCancelable(false)
                .setPositiveButton(R.string.start, null)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setOnShowListener(dialog -> {
            onDialogShow(alertDialog);
        });
        return alertDialog;
    }

    @SuppressLint("InflateParams")
    private void initViews() {

        rootView = LayoutInflater.from(getContext())
                .inflate(R.layout.game_begin_dialog, null, false);

        gameSettingLayout = rootView.findViewById(R.id.gameEndLayout);
        players = rootView.findViewById(R.id.numeroJugadorsET);


        addTextWatchers();
    }

    private void onDialogShow(AlertDialog dialog) {
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            onDoneClicked();
        });
    }
    
    private void onDoneClicked() {
        List<String> noms = new ArrayList<>();
        List<Integer> apostes = new ArrayList<>();
        boolean error=false;

        for (int i = 0; i < jugadores; i++) {
            EditText editText = gameSettingLayout.findViewById(20000+i);
            String value = editText.getText().toString();

            try{
                String nom = value.split(";")[0];
                Log.d("Nom:", nom);
                int aposta = Integer.parseInt(value.split(";")[1]);
                Log.d("Aposta:", String.valueOf(aposta));
                if ((aposta >= 5 && aposta<=1000) && validació(nom)){
                    noms.add(i,nom);
                    apostes.add(i,aposta);
                    activity.setNomAposta(noms,apostes);
                    this.dismiss();
                }
                else {
                    editText.setError("Error de format");
                    error = true;
                }
            }
            catch (Exception e){
                editText.setError("Error de format");
                e.printStackTrace();
                error = true;
            }
        }
        if (!error){
            activity.setNomAposta(noms,apostes);
            this.dismiss();
        }

    }


    private void addTextWatchers() {
        players.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    jugadores = Integer.parseInt(s.toString());
                    for (int i = 0; i < jugadores; i++) {
                        EditText nomET = new EditText(rootView.getContext());
                        nomET.setHint(R.string.player_hint);
                        nomET.setId(20000+i);
                        gameSettingLayout.addView(nomET);
                    }
                }
                catch(Exception e) {
                    gameSettingLayout.removeAllViews();
                    gameSettingLayout.addView(players);
                }
            }
        });

    }

    private boolean validació(String string){
        return patternIsValid(string, "[a-z]{3,7}");
    }

    private static boolean patternIsValid(String entrada, String patro){
        return Pattern.matches(patro,entrada);
    }
}

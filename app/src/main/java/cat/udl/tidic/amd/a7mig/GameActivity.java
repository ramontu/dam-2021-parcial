package cat.udl.tidic.amd.a7mig;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.PrimitiveIterator;

import cat.udl.tidic.amd.a7mig.models.Carta;
import cat.udl.tidic.amd.a7mig.models.Jugador;
import cat.udl.tidic.amd.a7mig.models.Partida;
import cat.udl.tidic.amd.a7mig.preferences.PreferenceProvider;
import cat.udl.tidic.amd.a7mig.viewmodel.GameActivityViewModel;


public class GameActivity extends AppCompatActivity {

    private static final String GAME_BEGIN_DIALOG_TAG = "game_dialog_tag";
    private static final String GAME_END_DIALOG_TAG = "game_end_dialog_tag";


    private ImageView _imageView;
    private TextView textView_Nom;
    private TextView textView_aposta;
    private TextView textView_puntuació;



    public GameActivityViewModel gameActivityViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        gameActivityViewModel = new GameActivityViewModel(this);
        _imageView = findViewById(R.id.UltimaCarta);
        textView_Nom = findViewById(R.id.textView);
        textView_aposta = findViewById(R.id.textView2);
        textView_puntuació = findViewById(R.id.textView3);
    }

    public void initView(){
        if (PreferenceProvider.providePreferences().getInt("banca",-1) < 0){
            PreferenceProvider.providePreferences().edit().putInt("banca",30000).apply();
        }
        promptForPlayer();

    }

    private void promptForPlayer() {
        GameBeginDialog dialog = GameBeginDialog.newInstance(this);
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), GAME_BEGIN_DIALOG_TAG);
    }

    public void finalPartida(){
        GameEndDialog dialog = GameEndDialog.newInstance(this,
                gameActivityViewModel.getPartidaMutableLiveData().getValue().getJugadores());
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), GAME_END_DIALOG_TAG);
    }


    public void plantarsebutton(View view){
        gameActivityViewModel.accio_plantarse();
    }

    public void seguir(View view){
        Carta c = gameActivityViewModel.demanar_carta();
        _imageView.setImageResource(c.getResource());
        textView_puntuació.setText(String.valueOf(gameActivityViewModel.getM_juga().getValue().getPuntuacion()));
        if (gameActivityViewModel.getM_juga().getValue().getPuntuacion() > 7.5){
            gameActivityViewModel.accio_plantarse();
        }
    }

    public void afegir_observadors(){
        gameActivityViewModel.getM_juga().observe(this, new Observer<Jugador>() {
            @Override
            public void onChanged(Jugador jugador) {
                textView_Nom.setText(jugador.getNombre());
                textView_aposta.setText(String.valueOf(jugador.getApuesta()));
                textView_puntuació.setText(String.valueOf(jugador.getPuntuacion()));
            }
        });
    }
}
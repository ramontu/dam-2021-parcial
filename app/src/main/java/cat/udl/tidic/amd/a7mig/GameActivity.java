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


public class GameActivity extends AppCompatActivity {

    private static final String GAME_BEGIN_DIALOG_TAG = "game_dialog_tag";
    private static final String GAME_END_DIALOG_TAG = "game_end_dialog_tag";

    private Button plantar;
    private Button seguir;
    private Jugador jugador_actual;
    private int numju = 0;
    private Partida partida;
    private ImageView _imageView;
    private TextView textView_Nom;
    private TextView textView_aposta;
    private TextView textView_puntuació;
    private MutableLiveData<Boolean> m_seguir;
    private MutableLiveData<Boolean> m_plantarse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        plantar = findViewById(R.id.button2);
        seguir = findViewById(R.id.seguirButton);
        _imageView = findViewById(R.id.UltimaCarta);
        textView_Nom = findViewById(R.id.textView);
        textView_aposta = findViewById(R.id.textView2);
        textView_puntuació = findViewById(R.id.textView3);
        m_seguir = new MutableLiveData<>();
        m_plantarse = new MutableLiveData<>();
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

    private void finalPartida(){
        GameEndDialog dialog = GameEndDialog.newInstance(this,
                new ArrayList<>());
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), GAME_END_DIALOG_TAG);
    }

    public void setNomAposta(List<String> d_jugadors, List<Integer> d_apostes){
        List<Jugador> llista = null;

        for(int i = 0; i < d_jugadors.size(); i++){
            Jugador juga = new Jugador(d_jugadors.get(i), d_apostes.get(i));
            Log.d("Nom",d_jugadors.get(i));
            Log.d("aposta",String.valueOf(d_apostes.get(i)));
            llista.add(juga);
        }
        partida(llista);
    }

    private void partida(List<Jugador> jugadors){
        partida = new Partida();
        partida.setJugadores(jugadors);
        jugador_actual = partida.getJugadores().get(numju);
        textView_Nom.setText(jugador_actual.getNombre());
        textView_aposta.setText(jugador_actual.getApuesta());
        textView_puntuació.setText(String.valueOf(jugador_actual.getPuntuacion()));
        //TODO agafar nova carta etc

        m_plantarse.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    jugador_actual = new Jugador(jugador_actual.getNombre(), (int) Math.round(jugador_actual.getApuesta()*0.9));
                    jugador_actual = partida.getJugadores().get(numju++);
                    nou_jugador();
                    m_plantarse.setValue(false);
                }
            }
        });

        m_seguir.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    Carta carta = partida.cogerCarta();
                    jugador_actual.setPuntuacion(jugador_actual.getPuntuacion() + carta.getValue());
                    textView_aposta.setText(String.valueOf(jugador_actual.getPuntuacion()));
                    _imageView.setImageResource(carta.getResource());
                    m_seguir.setValue(false);
                }
            }
        });


        //finalPartida();


    }

    public void nou_jugador(){
        if (numju > partida.getJugadores().size()){
            numju = 0;
        }
        else {
            jugador_actual = partida.getJugadores().get(numju++);
        }
        textView_Nom.setText(jugador_actual.getNombre());
        textView_aposta.setText(jugador_actual.getApuesta());
        textView_puntuació.setText(String.valueOf(jugador_actual.getPuntuacion()));
    }

    public void plantarsebutton(View view){
        m_plantarse.setValue(true);
    }

    public void seguir(View view){
        m_seguir.setValue(true);
    }
}
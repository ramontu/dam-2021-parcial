package cat.udl.tidic.amd.a7mig.viewmodel;


import android.content.SharedPreferences;
import android.provider.Telephony;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import cat.udl.tidic.amd.a7mig.GameActivity;
import cat.udl.tidic.amd.a7mig.R;
import cat.udl.tidic.amd.a7mig.models.Carta;
import cat.udl.tidic.amd.a7mig.models.Jugador;
import cat.udl.tidic.amd.a7mig.models.Partida;
import cat.udl.tidic.amd.a7mig.preferences.PreferenceProvider;

public class GameActivityViewModel {
    private MutableLiveData<Partida> partidaMutableLiveData;
    private int jugador_actual = 0;
    private MutableLiveData<Jugador> m_juga_actual;

    private GameActivity gameActivity;

    public GameActivityViewModel(GameActivity gameActivity){
        partidaMutableLiveData = new MutableLiveData<>();
        m_juga_actual = new MutableLiveData<>();
        this.gameActivity = gameActivity;
    }


    public MutableLiveData<Partida> getPartidaMutableLiveData(){
        return partidaMutableLiveData;
    }

    public void setjugadors(List<String> d_jugadors, List<Integer> d_apostes){
        List<Jugador> llista = new ArrayList<>();

        for(int i = 0; i < d_jugadors.size(); i++){
            Jugador juga = new Jugador(d_jugadors.get(i), d_apostes.get(i));
            Log.d("Nom",d_jugadors.get(i));
            Log.d("aposta",String.valueOf(d_apostes.get(i)));
            llista.add(juga);
        }
        Partida p = new Partida();
        p.setJugadores(llista);
        partidaMutableLiveData.setValue(p);
        m_juga_actual.setValue(partidaMutableLiveData.getValue().getJugadores().get(jugador_actual));
        demanar_carta();
    }

    public MutableLiveData<Jugador> getM_juga(){
        return m_juga_actual;
    }

    public void accio_plantarse(){
        if (jugador_actual < partidaMutableLiveData.getValue().getJugadores().size()-1){
            jugador_actual++;
            m_juga_actual.setValue(partidaMutableLiveData.getValue().getJugadores().get(jugador_actual));
            demanar_carta();
        }
        else {
            gameActivity.finalPartida();
        }
    }

    public Carta demanar_carta(){
        Carta c = partidaMutableLiveData.getValue().cogerCarta();
        m_juga_actual.getValue().setPuntuacion(m_juga_actual.getValue().getPuntuacion()+c.getValue());
        return c;
    }

    public void processar_final_ronda(){
        List<Jugador> llj = partidaMutableLiveData.getValue().getJugadores();
        for (int i = 0; i < llj.size(); i++){
            double p = llj.get(i).getPuntuacion();
            double multiplicador = 0;
            if (p == 7.5){
                multiplicador = 2;
                int banca = PreferenceProvider.providePreferences().getInt("banca",0);
                banca = banca - Math.round(m_juga_actual.getValue().getApuesta());
                PreferenceProvider.providePreferences().edit().putInt("banca", banca).apply();
            }
            else{
                if (p < 7.5){
                    multiplicador = 0.9;
                    int banca = PreferenceProvider.providePreferences().getInt("banca",0);
                    banca = banca + (int) Math.round(m_juga_actual.getValue().getApuesta()*0.1);
                    PreferenceProvider.providePreferences().edit().putInt("banca", banca).apply();
                }
                else {
                    multiplicador = 0;
                    int banca = PreferenceProvider.providePreferences().getInt("banca",0);
                    banca = banca + (int) Math.round(m_juga_actual.getValue().getApuesta());
                    PreferenceProvider.providePreferences().edit().putInt("banca", banca).apply();
                }

            }

            Jugador jp = new Jugador(llj.get(i).getNombre(), (int) Math.round(llj.get(i).getApuesta()*multiplicador));
            llj.remove(i);
            llj.add(i, jp);
        }
        jugador_actual = 0;
        partidaMutableLiveData.getValue().setJugadores(llj);
    }
}

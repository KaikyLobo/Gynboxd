package br.edu.pucgo.gynboxd;

import static org.junit.Assert.*;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EventoIntegracaoTest {
    private final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Before @After public void limparSomenteDadosDeTeste() {
        SharedPreferences prefs = context.getSharedPreferences("eventos_detalhes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        for (String key : prefs.getAll().keySet()) {
            if (key.startsWith("teste integração ")) editor.remove(key);
        }
        editor.commit();
    }

    private ActivityScenario<EventoDetalheActivity> abrir(String nome) {
        return ActivityScenario.launch(EventoDetalheActivity.criarIntent(context,
                nome, "Setor Sul", EventoDetalheActivity.SEM_DATA, 3.5f, R.drawable.capa_1));
    }

    @Test public void usaDadosDoItemSemExibirConteudoDoDeboxe() {
        try (ActivityScenario<EventoDetalheActivity> scenario = abrir("Teste integração A")) {
            scenario.onActivity(activity -> {
                assertEquals("Teste integração A", ((TextView) activity.findViewById(R.id.eventoNome)).getText().toString());
                assertEquals("Setor Sul", ((TextView) activity.findViewById(R.id.eventoSubtitulo)).getText().toString());
                assertEquals("3,5", ((TextView) activity.findViewById(R.id.eventoNota)).getText().toString());
                assertEquals(View.GONE, activity.findViewById(R.id.eventoOrganizacao).getVisibility());
                assertEquals(View.GONE, activity.findViewById(R.id.eventoCriticas).getVisibility());
            });
        }
    }

    @Test public void escolhasPersistemEEventosNaoCompartilhamEstado() {
        try (ActivityScenario<EventoDetalheActivity> scenario = abrir("Teste integração A")) {
            scenario.onActivity(activity -> {
                activity.findViewById(R.id.eventoFavorito).performClick();
                activity.findViewById(R.id.eventoPresenca).performClick();
            });
            scenario.recreate();
            scenario.onActivity(activity -> {
                assertTrue(activity.findViewById(R.id.eventoFavorito).isSelected());
                assertTrue(activity.findViewById(R.id.eventoPresenca).isSelected());
            });
        }
        try (ActivityScenario<EventoDetalheActivity> scenario = abrir("Teste integração B")) {
            scenario.onActivity(activity -> {
                assertFalse(activity.findViewById(R.id.eventoFavorito).isSelected());
                assertFalse(activity.findViewById(R.id.eventoPresenca).isSelected());
            });
        }
        try (ActivityScenario<EventoDetalheActivity> scenario = abrir("Teste integração A")) {
            scenario.onActivity(activity -> assertTrue(activity.findViewById(R.id.eventoFavorito).isSelected()));
        }
    }

    @Test public void menuAbreTelasDosColegas() {
        testarDestino(R.id.nav_inicio, MainActivity.class);
        testarDestino(R.id.nav_diario, DiarioActivity.class);
        testarDestino(R.id.nav_perfil, PerfilActivity.class);
    }

    @Test public void abaLugaresAbreTelaDoEventoAPartirDoInicio() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(EventoDetalheActivity.class.getName(), null, false);
        Activity opened = null;
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> ((BottomNavigationView) activity.findViewById(R.id.bottomNav)).setSelectedItemId(R.id.nav_lugares));
            opened = instrumentation.waitForMonitorWithTimeout(monitor, 5000);
            assertNotNull(opened);
        } finally {
            if (opened != null) {
                Activity toClose = opened;
                instrumentation.runOnMainSync(toClose::finish);
            }
            instrumentation.removeMonitor(monitor);
        }
    }

    @Test public void listasSoMudamAoSalvar() {
        try (ActivityScenario<EventoDetalheActivity> scenario = abrir("Teste integração A")) {
            scenario.onActivity(activity -> activity.findViewById(R.id.eventoLista).performClick());
            onView(withText("Preciso ir")).perform(click());
            onView(withText("Cancelar")).perform(click());
            scenario.onActivity(activity -> {
                assertFalse(activity.findViewById(R.id.eventoLista).isSelected());
                activity.findViewById(R.id.eventoLista).performClick();
            });
            onView(withText("Preciso ir")).perform(click());
            onView(withText("Salvar")).perform(click());
            scenario.recreate();
            scenario.onActivity(activity -> assertTrue(activity.findViewById(R.id.eventoLista).isSelected()));
        }
    }

    private void testarDestino(int id, Class<? extends Activity> destino) {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(destino.getName(), null, false);
        Activity opened = null;
        try (ActivityScenario<EventoDetalheActivity> scenario = abrir("Teste integração A")) {
            scenario.onActivity(activity -> ((BottomNavigationView) activity.findViewById(R.id.bottomNav)).setSelectedItemId(id));
            opened = instrumentation.waitForMonitorWithTimeout(monitor, 5000);
            assertNotNull("A aba precisa abrir " + destino.getSimpleName(), opened);
        } finally {
            if (opened != null) {
                Activity toClose = opened;
                instrumentation.runOnMainSync(toClose::finish);
            }
            instrumentation.removeMonitor(monitor);
        }
    }
}

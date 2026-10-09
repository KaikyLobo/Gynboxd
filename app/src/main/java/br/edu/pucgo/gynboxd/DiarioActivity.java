package br.edu.pucgo.gynboxd;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import br.edu.pucgo.gynboxd.databinding.ActivityDiarioBinding;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Tela 3 do Gynboxd — "Seu diário" (tela-lista-evento).
 *
 *  - View Binding (nenhum findViewById)
 *  - Lambdas nos listeners
 *  - RecyclerView com padrão ViewHolder (DiarioAdapter)
 *  - MaterialDatePicker para registrar a data de um novo rolê
 *  - Intent explícita com extras para abrir o detalhe do evento
 *  - Estado salvo em onSaveInstanceState (não perde dados ao girar a tela)
 */
public class DiarioActivity extends AppCompatActivity {

    private static final String TAG_DATE_PICKER = "date_picker_novo_role";
    private static final String STATE_ENTRADAS = "state_entradas";
    private static final String STATE_ABA = "state_aba";

    private ActivityDiarioBinding binding;
    private DiarioAdapter adapter;
    private ArrayList<EntradaDiario> entradas;
    private int abaSelecionada = 0; // 0 = Diário, 1 = Avaliações

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDiarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Mesmo tratamento da MainActivity: o conteúdo não fica atrás da barra de status
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(barras.left, barras.top, barras.right, barras.bottom);
            return insets;
        });

        restaurarEstado(savedInstanceState);
        configurarLista();
        configurarAbas();
        configurarBotaoAdicionar();
        configurarBottomNav();
        reanexarDatePickerSeExistir();

        atualizarTela();
    }

    // ---------------- Estado / ciclo de vida ----------------

    @SuppressWarnings("unchecked")
    private void restaurarEstado(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Object salvo = savedInstanceState.getSerializable(STATE_ENTRADAS);
            if (salvo instanceof ArrayList) {
                entradas = (ArrayList<EntradaDiario>) salvo;
            }
            abaSelecionada = savedInstanceState.getInt(STATE_ABA, 0);
        }
        if (entradas == null) {
            entradas = dadosDeExemplo();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_ENTRADAS, entradas);
        outState.putInt(STATE_ABA, abaSelecionada);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // evita vazamento de memória da view
    }

    // ---------------- Configuração da UI ----------------

    private void configurarLista() {
        // Clique em um item abre a tela de detalhe via Intent explícita
        adapter = new DiarioAdapter(this::abrirDetalhe);
        binding.rvDiario.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDiario.setHasFixedSize(true);
        binding.rvDiario.setAdapter(adapter);
    }

    private void configurarAbas() {
        binding.tabs.addTab(binding.tabs.newTab().setText(R.string.aba_diario));
        binding.tabs.addTab(binding.tabs.newTab().setText(R.string.aba_avaliacoes));

        TabLayout.Tab aba = binding.tabs.getTabAt(abaSelecionada);
        if (aba != null) aba.select();

        // Interface com 3 métodos: não dá para ser lambda
        binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                abaSelecionada = tab.getPosition();
                atualizarTela();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) {
                binding.rvDiario.smoothScrollToPosition(0);
            }
        });
    }

    private void configurarBotaoAdicionar() {
        binding.btnAdicionar.setOnClickListener(v -> abrirDatePicker());
    }

    private void configurarBottomNav() {
        binding.bottomNav.setSelectedItemId(R.id.nav_diario);
        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_diario) {
                return true;
            }
            if (id == R.id.nav_inicio) {
                // Intent explícita de volta para a home, sem empilhar telas repetidas
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                return true;
            }
            // Lugares e Perfil são telas dos colegas — trocar o Toast pelo Intent da tela deles
            Toast.makeText(this, item.getTitle() + " — em desenvolvimento", Toast.LENGTH_SHORT).show();
            return false;
        });
    }

    // ---------------- MaterialDatePicker ----------------

    private void abrirDatePicker() {
        if (getSupportFragmentManager().findFragmentByTag(TAG_DATE_PICKER) != null) return; // evita abrir 2x

        // Só deixa escolher datas até hoje: só registra rolê que já aconteceu
        CalendarConstraints restricoes = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
                .build();

        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTheme(R.style.ThemeOverlay_Gynboxd_DatePicker)
                .setTitleText(R.string.titulo_date_picker)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(restricoes)
                .build();

        anexarListenersDatePicker(picker);
        picker.show(getSupportFragmentManager(), TAG_DATE_PICKER);
    }

    /** Se a tela girar com o calendário aberto, o fragment volta sem listener — reanexamos aqui. */
    @SuppressWarnings("unchecked")
    private void reanexarDatePickerSeExistir() {
        Object fragment = getSupportFragmentManager().findFragmentByTag(TAG_DATE_PICKER);
        if (fragment instanceof MaterialDatePicker) {
            anexarListenersDatePicker((MaterialDatePicker<Long>) fragment);
        }
    }

    private void anexarListenersDatePicker(MaterialDatePicker<Long> picker) {
        picker.addOnPositiveButtonClickListener(selecao -> {
            if (selecao == null) return;
            entradas.add(new EntradaDiario(
                    getString(R.string.novo_role),
                    getString(R.string.local_a_definir),
                    selecao,
                    0f,
                    R.drawable.capa_padrao));
            atualizarTela();
            if (binding != null) {
                Snackbar.make(binding.getRoot(),
                        getString(R.string.role_registrado, picker.getHeaderText()),
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    // ---------------- Navegação (Intent explícita) ----------------

    private void abrirDetalhe(EntradaDiario entrada) {
        Intent intent = new Intent(this, EventoDetalheActivity.class);
        intent.putExtra(EventoDetalheActivity.EXTRA_NOME, entrada.getNome());
        intent.putExtra(EventoDetalheActivity.EXTRA_LOCAL, entrada.getLocal());
        intent.putExtra(EventoDetalheActivity.EXTRA_DATA, entrada.getDataUtcMillis());
        intent.putExtra(EventoDetalheActivity.EXTRA_NOTA, entrada.getNota());
        intent.putExtra(EventoDetalheActivity.EXTRA_CAPA, entrada.getCapaRes());
        startActivity(intent);
    }

    // ---------------- Atualização dos dados ----------------

    private void atualizarTela() {
        List<EntradaDiario> visiveis = new ArrayList<>();
        int avaliados = 0;
        float somaNotas = 0f;

        for (EntradaDiario e : entradas) {
            if (e.getNota() > 0) {
                avaliados++;
                somaNotas += e.getNota();
            }
            if (abaSelecionada == 0 || e.getNota() > 0) {
                visiveis.add(e);
            }
        }

        binding.tvValorRoles.setText(String.valueOf(entradas.size()));
        binding.tvValorAvaliacoes.setText(String.valueOf(avaliados));
        binding.tvValorMedia.setText(avaliados == 0
                ? "—"
                : String.format(new Locale("pt", "BR"), "%.1f", somaNotas / avaliados));

        adapter.setEntradas(visiveis);
    }

    // ---------------- Dados de exemplo (sem backend nesta entrega) ----------------

    private ArrayList<EntradaDiario> dadosDeExemplo() {
        ArrayList<EntradaDiario> lista = new ArrayList<>();
        lista.add(new EntradaDiario("Pelourinho Ferveinho & Café", "Setor Sul", dataUtc(2026, Calendar.SEPTEMBER, 28), 5f, R.drawable.capa_1));
        lista.add(new EntradaDiario("Sambasoma", "Setor Bueno", dataUtc(2026, Calendar.SEPTEMBER, 21), 4f, R.drawable.capa_2));
        lista.add(new EntradaDiario("Deboxe Imperium", "Setor Marista", dataUtc(2026, Calendar.SEPTEMBER, 5), 4.5f, R.drawable.capa_3));
        lista.add(new EntradaDiario("Sjá Café", "St. Marista", dataUtc(2026, Calendar.AUGUST, 23), 3.5f, R.drawable.capa_4));
        lista.add(new EntradaDiario("Woodstock Rock Bar", "Setor Oeste", dataUtc(2026, Calendar.AUGUST, 21), 0f, R.drawable.capa_5));
        return lista;
    }

    private static long dataUtc(int ano, int mes, int dia) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(ano, mes, dia);
        return cal.getTimeInMillis();
    }
}
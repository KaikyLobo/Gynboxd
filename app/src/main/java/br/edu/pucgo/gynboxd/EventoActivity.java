package br.edu.pucgo.gynboxd;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import br.edu.pucgo.gynboxd.databinding.ActivityEventoBinding;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/** Tela 2: recebe os dados de início, diário e perfil e mantém ações por evento. */
public class EventoActivity extends BaseActivity {
    public static final String EXTRA_NOME = "br.edu.pucgo.gynboxd.EXTRA_NOME";
    public static final String EXTRA_SUBTITULO = "br.edu.pucgo.gynboxd.EXTRA_SUBTITULO";
    public static final String EXTRA_DATA = "br.edu.pucgo.gynboxd.EXTRA_DATA";
    public static final String EXTRA_NOTA = "br.edu.pucgo.gynboxd.EXTRA_NOTA";
    public static final String EXTRA_CAPA = "br.edu.pucgo.gynboxd.EXTRA_CAPA";
    public static final long SEM_DATA = -1L;

    private ActivityEventoBinding binding;
    private SharedPreferences preferences;
    private String prefixo;

    public static Intent criarIntent(Context context, String nome, String subtitulo,
                                     long dataUtcMillis, float nota, @DrawableRes int capaRes) {
        return new Intent(context, EventoDetalheActivity.class)
                .putExtra(EXTRA_NOME, nome)
                .putExtra(EXTRA_SUBTITULO, subtitulo)
                .putExtra(EXTRA_DATA, dataUtcMillis)
                .putExtra(EXTRA_NOTA, nota)
                .putExtra(EXTRA_CAPA, capaRes);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        aplicarBarrasDoSistema(binding.getRoot(), true);
        configurarNavegacao(binding.bottomNav, R.id.nav_lugares);
        binding.eventoVoltar.setOnClickListener(view -> finish());

        Intent intent = getIntent();
        String nome = intent.getStringExtra(EXTRA_NOME);
        if (nome == null || nome.trim().isEmpty()) nome = "Deboxe";
        boolean deboxe = nome.equalsIgnoreCase("Deboxe") || nome.equalsIgnoreCase("Deboxe Imperium");
        prefixo = (deboxe ? "deboxe" : nome.trim().toLowerCase(Locale.ROOT)) + ":";
        preferences = getSharedPreferences("eventos_detalhes", MODE_PRIVATE);
        if (deboxe) migrarEscolhasAnteriores();

        binding.eventoNome.setText(nome);
        String subtitulo = intent.getStringExtra(EXTRA_SUBTITULO);
        long data = intent.getLongExtra(EXTRA_DATA, SEM_DATA);
        String dataTexto = "";
        if (data != SEM_DATA) {
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy", new Locale("pt", "BR"));
            fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            dataTexto = fmt.format(data);
        }
        if (deboxe) {
            binding.eventoSubtitulo.setText(dataTexto.isEmpty()
                    ? "05-09-2026 · Automotivo" : dataTexto + " · Automotivo");
            binding.eventoCapa.setImageResource(R.drawable.evento_deboxe_capa);
        } else {
            String local = subtitulo == null ? "" : subtitulo;
            binding.eventoSubtitulo.setText(dataTexto.isEmpty() ? local
                    : (local.isEmpty() ? dataTexto : dataTexto + " · " + local));
            int capa = intent.getIntExtra(EXTRA_CAPA, R.drawable.capa_padrao);
            binding.eventoCapa.setImageResource(capa == 0 ? R.drawable.capa_padrao : capa);
            float nota = intent.getFloatExtra(EXTRA_NOTA, 0f);
            boolean temNota = !Float.isNaN(nota) && nota > 0 && nota <= 5;
            binding.eventoNota.setText(temNota
                    ? String.format(new Locale("pt", "BR"), "%.1f", nota) : "—");
            binding.eventoNotaLegenda.setText(temNota ? R.string.evento_sua_nota : R.string.evento_sem_nota);
            binding.eventoDirecao.setVisibility(View.GONE);
            binding.eventoDescricao.setText(R.string.evento_descricao_pendente);
            binding.eventoTags.setVisibility(View.GONE);
            binding.eventoOrganizacaoTitulo.setVisibility(View.GONE);
            binding.eventoOrganizacao.setVisibility(View.GONE);
            binding.eventoCriticas.setVisibility(View.GONE);
            binding.eventoCriticasTitulo.setText(R.string.evento_sem_criticas);
        }
        binding.eventoCapa.setContentDescription(getString(R.string.evento_capa_de, nome));
        configurarAlternancia(binding.eventoPresenca, "presenca", "✓", R.string.evento_marcar_presenca);
        configurarAlternancia(binding.eventoFavorito, "favorito", "♥\uFE0E", R.string.evento_favoritar);
        binding.eventoLista.setOnClickListener(view -> abrirListas());
        atualizarBotaoLista();
    }

    private void migrarEscolhasAnteriores() {
        SharedPreferences antigo = getSharedPreferences("evento_deboxe", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        for (String key : new String[]{"presenca", "favorito", "lista_0", "lista_1"}) {
            if (!preferences.contains(prefixo + key) && antigo.contains(key)) {
                editor.putBoolean(prefixo + key, antigo.getBoolean(key, false));
            }
        }
        editor.apply();
    }

    private void configurarAlternancia(Button botao, String chave, String simbolo, int descricao) {
        botao.setText(simbolo);
        atualizarBotao(botao, preferences.getBoolean(prefixo + chave, false), descricao);
        botao.setOnClickListener(view -> {
            boolean selecionado = !preferences.getBoolean(prefixo + chave, false);
            preferences.edit().putBoolean(prefixo + chave, selecionado).apply();
            atualizarBotao(botao, selecionado, descricao);
        });
    }

    private void atualizarBotao(Button botao, boolean selecionado, int descricao) {
        botao.setSelected(selecionado);
        botao.setTextColor(ContextCompat.getColor(this, selecionado ? R.color.green : R.color.text_secondary));
        botao.setContentDescription(getString(descricao) + ": "
                + getString(selecionado ? R.string.evento_selecionado : R.string.evento_nao_selecionado));
    }

    private void abrirListas() {
        String[] listas = getResources().getStringArray(R.array.evento_listas);
        boolean[] selecionadas = {
            preferences.getBoolean(prefixo + "lista_0", false),
            preferences.getBoolean(prefixo + "lista_1", false)
        };
        new AlertDialog.Builder(this)
                .setTitle(R.string.evento_adicionar_listas)
                .setMultiChoiceItems(listas, selecionadas,
                        (dialog, index, checked) -> selecionadas[index] = checked)
                .setNegativeButton(R.string.evento_cancelar, null)
                .setPositiveButton(R.string.evento_salvar, (dialog, which) -> {
                    preferences.edit()
                            .putBoolean(prefixo + "lista_0", selecionadas[0])
                            .putBoolean(prefixo + "lista_1", selecionadas[1]).apply();
                    atualizarBotaoLista();
                }).show();
    }

    private void atualizarBotaoLista() {
        atualizarBotao(binding.eventoLista,
                preferences.getBoolean(prefixo + "lista_0", false)
                        || preferences.getBoolean(prefixo + "lista_1", false),
                R.string.evento_adicionar_listas);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

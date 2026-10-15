package br.edu.pucgo.gynboxd;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.DrawableRes;

import br.edu.pucgo.gynboxd.databinding.ActivityEventoDetalheBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Detalhe de um evento/lugar — PROVISÓRIA.
 *
 * Esta tela existe para todas as listas do app já serem clicáveis (Intent explícita com dados).
 * Quando a tela 2 (tela-foco-evento) ficar pronta, ela substitui esta: basta trocar o conteúdo
 * do layout/Activity mantendo o método criarIntent(), e nenhuma outra tela precisa mudar.
 */
public class EventoDetalheActivity extends BaseActivity {

    // Chaves dos extras: com o nome do pacote na frente, como recomenda o Android
    public static final String EXTRA_NOME = "br.edu.pucgo.gynboxd.EXTRA_NOME";
    public static final String EXTRA_SUBTITULO = "br.edu.pucgo.gynboxd.EXTRA_SUBTITULO";
    public static final String EXTRA_DATA = "br.edu.pucgo.gynboxd.EXTRA_DATA";
    public static final String EXTRA_NOTA = "br.edu.pucgo.gynboxd.EXTRA_NOTA";
    public static final String EXTRA_CAPA = "br.edu.pucgo.gynboxd.EXTRA_CAPA";

    /** Valor de data para quando a origem não tem data (ex.: destaques da home). */
    public static final long SEM_DATA = -1L;

    private ActivityEventoDetalheBinding binding;

    /**
     * Único jeito de abrir esta tela. Quem chama não precisa saber o nome dos extras.
     *
     * @param subtitulo bairro, ano ou qualquer texto curto abaixo do nome
     * @param dataUtcMillis data em milissegundos UTC, ou {@link #SEM_DATA}
     * @param nota 0 a 5 (0 = sem nota, esconde as estrelas)
     */
    public static Intent criarIntent(Context context, String nome, String subtitulo,
                                     long dataUtcMillis, float nota, @DrawableRes int capaRes) {
        Intent intent = new Intent(context, EventoDetalheActivity.class);
        intent.putExtra(EXTRA_NOME, nome);
        intent.putExtra(EXTRA_SUBTITULO, subtitulo);
        intent.putExtra(EXTRA_DATA, dataUtcMillis);
        intent.putExtra(EXTRA_NOTA, nota);
        intent.putExtra(EXTRA_CAPA, capaRes);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventoDetalheBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Sem barra inferior nesta tela: a raiz recebe também o espaço de baixo
        aplicarBarrasDoSistema(binding.getRoot(), false);

        Intent intent = getIntent();
        // Valores padrão evitam crash se algum extra não vier
        String nome = intent.getStringExtra(EXTRA_NOME);
        String subtitulo = intent.getStringExtra(EXTRA_SUBTITULO);
        long data = intent.getLongExtra(EXTRA_DATA, SEM_DATA);
        float nota = intent.getFloatExtra(EXTRA_NOTA, 0f);
        int capa = intent.getIntExtra(EXTRA_CAPA, R.drawable.capa_padrao);

        binding.tvNome.setText(nome != null ? nome : getString(R.string.evento_sem_nome));
        binding.tvLocal.setText(subtitulo != null ? subtitulo : "");
        binding.ivCapa.setImageResource(capa);

        if (nota > 0) {
            binding.ratingNota.setRating(nota);
        } else {
            binding.ratingNota.setVisibility(View.GONE);
        }

        if (data != SEM_DATA) {
            SimpleDateFormat fmt = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));
            fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            binding.tvData.setText(fmt.format(data));
        } else {
            binding.tvData.setVisibility(View.GONE);
        }

        binding.btnVoltar.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

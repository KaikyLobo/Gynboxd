package br.edu.pucgo.gynboxd;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import br.edu.pucgo.gynboxd.databinding.ActivityEventoDetalheBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Tela de destino simples, para demonstrar a Intent explícita + leitura segura dos extras.
 * Quando a tela 2 (tela-foco-evento) estiver pronta, basta trocar
 * EventoDetalheActivity.class pela Activity dela em DiarioActivity.abrirDetalhe().
 */
public class EventoDetalheActivity extends AppCompatActivity {

    public static final String EXTRA_NOME = "br.edu.pucgo.gynboxd.EXTRA_NOME";
    public static final String EXTRA_LOCAL = "br.edu.pucgo.gynboxd.EXTRA_LOCAL";
    public static final String EXTRA_DATA = "br.edu.pucgo.gynboxd.EXTRA_DATA";
    public static final String EXTRA_NOTA = "br.edu.pucgo.gynboxd.EXTRA_NOTA";
    public static final String EXTRA_CAPA = "br.edu.pucgo.gynboxd.EXTRA_CAPA";

    private ActivityEventoDetalheBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityEventoDetalheBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Soma as barras do sistema ao padding de 16dp que já vem do layout
        int padding = binding.getRoot().getPaddingLeft();
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(padding + barras.left, padding + barras.top,
                    padding + barras.right, padding + barras.bottom);
            return insets;
        });

        Intent intent = getIntent();
        // Valores padrão evitam NullPointerException se algum extra não vier
        String nome = intent.getStringExtra(EXTRA_NOME);
        String local = intent.getStringExtra(EXTRA_LOCAL);
        long data = intent.getLongExtra(EXTRA_DATA, -1L);
        float nota = intent.getFloatExtra(EXTRA_NOTA, 0f);
        int capa = intent.getIntExtra(EXTRA_CAPA, R.drawable.capa_padrao);

        binding.tvNome.setText(nome != null ? nome : getString(R.string.evento_sem_nome));
        binding.tvLocal.setText(local != null ? local : "");
        binding.ivCapa.setImageResource(capa);
        binding.ratingNota.setRating(nota);

        if (data > 0) {
            SimpleDateFormat fmt = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));
            fmt.setTimeZone(TimeZone.getTimeZone("UTC"));
            binding.tvData.setText(fmt.format(data));
        }

        binding.btnVoltar.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
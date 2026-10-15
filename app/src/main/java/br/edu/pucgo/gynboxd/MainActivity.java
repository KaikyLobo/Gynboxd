package br.edu.pucgo.gynboxd;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import br.edu.pucgo.gynboxd.databinding.ActivityMainBinding;

import java.util.Arrays;
import java.util.List;

/** Tela 1 — Início (home): atividade recente + "em alta essa semana". */
public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // BaseActivity: barras do sistema + barra inferior com a aba "Início" marcada
        aplicarBarrasDoSistema(binding.getRoot(), true);
        configurarNavegacao(binding.bottomNav, R.id.nav_inicio);

        configurarListas();
    }

    private void configurarListas() {
        List<Atividade> atividades = Arrays.asList(
                new Atividade("Marina Alves", " avaliou um rolê que já curtiu.", null,
                        "Palavrear livraria & café", "2019", 4f,
                        R.drawable.bg_avatar_green, R.drawable.poster_1),
                new Atividade("Rafael Souza",
                        " adicionou um rolê à lista “Curtir sem parar”.",
                        "“Curtir sem parar”",
                        "Órbita Zero", "2023", 0f,
                        R.drawable.bg_avatar_orange, R.drawable.poster_2),
                new Atividade("Bia Nogueira", " curtiu uma crítica de Thiago Ramos.",
                        "Thiago Ramos",
                        "Último Trem", "2021", 5f,
                        R.drawable.bg_avatar_blue, R.drawable.poster_3)
        );

        List<Destaque> destaques = Arrays.asList(
                new Destaque("Palavrear livraria & café", "2016", R.drawable.poster_1),
                new Destaque("Deboxe", "2026", R.drawable.poster_2),
                new Destaque("Analloween", "2026", R.drawable.poster_3),
                new Destaque("Nord 23", "2020", R.drawable.poster_4)
        );

        // Clique em qualquer item abre o detalhe (Intent explícita com os dados do item)
        binding.rvAtividades.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAtividades.setAdapter(new AtividadeAdapter(atividades, a ->
                startActivity(EventoDetalheActivity.criarIntent(this,
                        a.titulo, a.ano, EventoDetalheActivity.SEM_DATA, a.nota, a.posterRes))));

        binding.rvDestaques.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvDestaques.setAdapter(new DestaqueAdapter(destaques, d ->
                startActivity(EventoDetalheActivity.criarIntent(this,
                        d.nome, d.ano, EventoDetalheActivity.SEM_DATA, 0f, d.imagemRes))));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

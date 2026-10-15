package br.edu.pucgo.gynboxd;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import br.edu.pucgo.gynboxd.databinding.ActivityPerfilBinding;

import java.util.ArrayList;
import java.util.List;

/** Tela 4 — Perfil do usuário: números, lugares favoritos e listas. */
public class PerfilActivity extends BaseActivity {

    private static final String STATE_SEGUINDO = "state_seguindo";

    private ActivityPerfilBinding binding;
    private boolean seguindo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // BaseActivity: barras do sistema + barra inferior com a aba "Perfil" marcada
        aplicarBarrasDoSistema(binding.getRoot(), true);
        configurarNavegacao(binding.bottomNav, R.id.nav_perfil);

        if (savedInstanceState != null) {
            seguindo = savedInstanceState.getBoolean(STATE_SEGUINDO, true);
        }

        configurarListas();
        configurarBotaoSeguindo();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SEGUINDO, seguindo); // não perde o estado ao girar a tela
    }

    private void configurarListas() {
        List<ListaItem> favoritos = new ArrayList<>();
        favoritos.add(new ListaItem("Festival", "Lugar favorito", R.drawable.lugar1));
        favoritos.add(new ListaItem("Bar Allow", "Lugar favorito", R.drawable.lugar2));
        favoritos.add(new ListaItem("Woodstock", "Lugar favorito", R.drawable.lugar3));
        favoritos.add(new ListaItem("Café", "Lugar favorito", R.drawable.lugar4));

        binding.recyclerFavoritos.setLayoutManager(
                new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        // Clique num favorito abre o detalhe (Intent explícita com os dados do item)
        binding.recyclerFavoritos.setAdapter(new FavoritosAdapter(favoritos, item ->
                startActivity(EventoDetalheActivity.criarIntent(this,
                        item.getTitulo(), item.getQuantidade(),
                        EventoDetalheActivity.SEM_DATA, 0f, item.getImagem()))));

        List<ListaItem> listas = new ArrayList<>();
        listas.add(new ListaItem("Preciso ir", "14 lugares", R.drawable.lugar1));
        listas.add(new ListaItem("Melhores de 2026", "8 lugares", R.drawable.lugar2));

        binding.recyclerListas.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerListas.setAdapter(new ListaAdapter(listas));
    }

    private void configurarBotaoSeguindo() {
        atualizarBotaoSeguindo();
        binding.btnSeguindo.setOnClickListener(v -> {
            seguindo = !seguindo;
            atualizarBotaoSeguindo();
        });
    }

    private void atualizarBotaoSeguindo() {
        binding.btnSeguindo.setText(seguindo ? R.string.perfil_seguindo : R.string.perfil_seguir);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

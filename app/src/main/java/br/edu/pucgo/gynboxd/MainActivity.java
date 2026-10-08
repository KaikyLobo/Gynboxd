package br.edu.pucgo.gynboxd;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import br.edu.pucgo.gynboxd.databinding.ActivityMainBinding;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        configurarListas();
        configurarBotaoSeguindo();
    }

    private void configurarListas() {
        List<ListaItem> favoritos = new ArrayList<>();
        favoritos.add(new ListaItem("Festival", "Lugar favorito", R.drawable.lugar1));
        favoritos.add(new ListaItem("Bar Allow", "Lugar favorito", R.drawable.lugar2));
        favoritos.add(new ListaItem("Woodstock", "Lugar favorito", R.drawable.lugar3));
        favoritos.add(new ListaItem("Café", "Lugar favorito", R.drawable.lugar4));

        binding.recyclerFavoritos.setLayoutManager(
            new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        );
        binding.recyclerFavoritos.setAdapter(new FavoritosAdapter(favoritos));

        List<ListaItem> listas = new ArrayList<>();
        listas.add(new ListaItem("Preciso ir", "14 lugares", R.drawable.lugar1));
        listas.add(new ListaItem("Melhores de 2026", "8 lugares", R.drawable.lugar2));

        binding.recyclerListas.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerListas.setAdapter(new ListaAdapter(listas));
    }

    private void configurarBotaoSeguindo() {
        binding.btnSeguindo.setOnClickListener(v ->
            binding.btnSeguindo.setText("SEGUINDO")
        );
    }
}

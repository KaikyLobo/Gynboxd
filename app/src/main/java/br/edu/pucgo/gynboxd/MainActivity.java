package br.edu.pucgo.gynboxd;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Ícones claros nas barras do sistema (o app é sempre escuro)
        EdgeToEdge.enable(this,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.dark(Color.TRANSPARENT));
        setContentView(R.layout.activity_main);

        // Só o topo/laterais: a bottom nav trata o inset de baixo sozinha
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, 0);
            return insets;
        });

        configurarListas();
        configurarNavegacao();
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

        RecyclerView rvAtividades = findViewById(R.id.rvAtividades);
        rvAtividades.setLayoutManager(new LinearLayoutManager(this));
        rvAtividades.setAdapter(new AtividadeAdapter(atividades));

        RecyclerView rvDestaques = findViewById(R.id.rvDestaques);
        rvDestaques.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvDestaques.setAdapter(new DestaqueAdapter(destaques));
    }

    private void configurarNavegacao() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setSelectedItemId(R.id.nav_inicio);
        nav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_inicio) return true;
            // Enquanto as outras telas não existem, mantém Início selecionado
            Toast.makeText(this, "Tela em construção", Toast.LENGTH_SHORT).show();
            return false;
        });
    }
}
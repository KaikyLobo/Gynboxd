package br.edu.pucgo.gynboxd;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EventoActivity extends AppCompatActivity {
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_evento);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.eventoRoot), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        preferences = getSharedPreferences("evento_deboxe", MODE_PRIVATE);
        configureToggle(R.id.eventoPresenca, "presenca", "✓", "Marcar presença");
        configureToggle(R.id.eventoFavorito, "favorito", "♥", "Favoritar evento");
        findViewById(R.id.eventoLista).setOnClickListener(view -> {
            String[] lists = {"Preciso ir", "Melhores de 2026"};
            boolean[] selected = {
                preferences.getBoolean("lista_0", false),
                preferences.getBoolean("lista_1", false)
            };
            new AlertDialog.Builder(this)
                .setTitle("Adicionar às listas")
                .setMultiChoiceItems(lists, selected, (dialog, index, checked) -> selected[index] = checked)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Salvar", (dialog, which) -> preferences.edit()
                    .putBoolean("lista_0", selected[0])
                    .putBoolean("lista_1", selected[1]).apply())
                .show();
        });
        findViewById(R.id.eventoInicio).setOnClickListener(view -> finish());
    }

    private void configureToggle(int id, String key, String symbol, String description) {
        Button button = findViewById(id);
        updateToggle(button, preferences.getBoolean(key, false), symbol, description);
        button.setOnClickListener(view -> {
            boolean selected = !preferences.getBoolean(key, false);
            preferences.edit().putBoolean(key, selected).apply();
            updateToggle(button, selected, symbol, description);
        });
    }

    private void updateToggle(Button button, boolean selected, String symbol, String description) {
        button.setText(symbol);
        button.setSelected(selected);
        button.setTextColor(android.graphics.Color.parseColor(selected ? "#00E054" : "#8A95A3"));
        button.setContentDescription(description + (selected ? ": selecionado" : ": não selecionado"));
    }
}

package br.edu.pucgo.gynboxd;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Classe base de TODAS as telas do Gynboxd.
 *
 * Centraliza o que antes cada tela repetia:
 *  1. Barras do sistema (status bar / navegação) escuras e sem conteúdo por baixo delas.
 *  2. A barra de navegação inferior: qual aba fica marcada e para qual tela cada aba leva.
 *
 * Como usar numa tela nova:
 *   public class MinhaActivity extends BaseActivity {
 *       protected void onCreate(Bundle b) {
 *           super.onCreate(b);
 *           binding = ActivityMinhaBinding.inflate(getLayoutInflater());
 *           setContentView(binding.getRoot());
 *           aplicarBarrasDoSistema(binding.getRoot(), true);
 *           configurarNavegacao(binding.bottomNav, R.id.nav_xxx);
 *       }
 *   }
 */
public abstract class BaseActivity extends AppCompatActivity {

    private BottomNavigationView navInferior;
    @IdRes private int abaAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // App sempre escuro: barras do sistema transparentes com ícones claros.
        // Fica aqui (antes do setContentView das telas filhas), como o Android recomenda.
        EdgeToEdge.enable(this,
                SystemBarStyle.dark(Color.TRANSPARENT),
                SystemBarStyle.dark(Color.TRANSPARENT));
    }

    /**
     * Aplica o espaçamento das barras do sistema na view raiz, para o conteúdo não ficar
     * atrás da barra de status / navegação.
     *
     * @param temBottomNav true quando a tela tem a BottomNavigationView: ela mesma trata o
     *                     espaço de baixo, então a raiz só recebe o espaço de cima/laterais.
     */
    protected void aplicarBarrasDoSistema(View raiz, boolean temBottomNav) {
        // Guarda o padding original do layout para somar com o das barras
        final int pEsq = raiz.getPaddingLeft();
        final int pTopo = raiz.getPaddingTop();
        final int pDir = raiz.getPaddingRight();
        final int pBaixo = raiz.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(raiz, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(pEsq + barras.left, pTopo + barras.top, pDir + barras.right,
                    pBaixo + (temBottomNav ? 0 : barras.bottom));
            return insets;
        });
    }

    /** Marca a aba desta tela e define para onde cada aba leva (Intents explícitas). */
    protected void configurarNavegacao(BottomNavigationView nav, @IdRes int abaDestaTela) {
        this.navInferior = nav;
        this.abaAtual = abaDestaTela;

        nav.setSelectedItemId(abaDestaTela);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == abaDestaTela) return true;

            Class<?> destino = telaDaAba(id);
            if (destino == null) {
                Toast.makeText(this, R.string.tela_em_construcao, Toast.LENGTH_SHORT).show();
                return false; // mantém a aba atual marcada
            }

            Intent intent = new Intent(this, destino);
            // Reaproveita a tela se ela já estiver aberta, em vez de empilhar cópias
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0); // troca de aba sem animação, como num app real
            return false; // quem marca a aba é a tela de destino
        });
    }

    /** Liga cada item do menu (res/menu/bottom_nav_menu.xml) à sua Activity. */
    private static Class<?> telaDaAba(@IdRes int id) {
        if (id == R.id.nav_inicio) return MainActivity.class;
        if (id == R.id.nav_diario) return DiarioActivity.class;
        if (id == R.id.nav_perfil) return PerfilActivity.class;
        // nav_lugares: tela "Cafés, bares e festas" ainda não existe
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ao voltar para uma tela já aberta (botão Voltar ou troca de aba), remarca a aba certa
        if (navInferior != null) {
            navInferior.getMenu().findItem(abaAtual).setChecked(true);
        }
    }
}

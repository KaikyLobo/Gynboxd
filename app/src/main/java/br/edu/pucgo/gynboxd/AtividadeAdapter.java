package br.edu.pucgo.gynboxd;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AtividadeAdapter extends RecyclerView.Adapter<AtividadeAdapter.VH> {

    private final List<Atividade> itens;

    public AtividadeAdapter(List<Atividade> itens) {
        this.itens = itens;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_atividade, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Atividade a = itens.get(position);
        Context c = h.itemView.getContext();
        int branco = ContextCompat.getColor(c, R.color.text_primary);
        int cinza = ContextCompat.getColor(c, R.color.text_secondary);

        h.vAvatar.setBackgroundResource(a.avatarRes);
        h.imgPoster.setImageResource(a.posterRes);

        // "Marina Alves" em negrito branco + resto cinza
        SpannableStringBuilder acao = new SpannableStringBuilder(a.autor + a.acao);
        destacar(acao, 0, a.autor.length(), branco);
        if (a.realce != null) {
            int ini = acao.toString().indexOf(a.realce, a.autor.length());
            if (ini >= 0) destacar(acao, ini, ini + a.realce.length(), branco);
        }
        h.txtAcao.setText(acao);

        // Título em negrito branco + ano cinza
        SpannableStringBuilder titulo = new SpannableStringBuilder(a.titulo + " " + a.ano);
        destacar(titulo, 0, a.titulo.length(), branco);
        titulo.setSpan(new ForegroundColorSpan(cinza), a.titulo.length(),
                titulo.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        h.txtTitulo.setText(titulo);

        if (a.nota > 0) {
            h.txtEstrelas.setText(estrelas(a.nota));
            h.txtEstrelas.setVisibility(View.VISIBLE);
        } else {
            h.txtEstrelas.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    private static void destacar(SpannableStringBuilder sb, int ini, int fim, int cor) {
        if (ini < 0 || fim > sb.length() || ini >= fim) return;
        sb.setSpan(new StyleSpan(Typeface.BOLD), ini, fim, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new ForegroundColorSpan(cor), ini, fim, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    // Loop em vez de String.repeat(), que só existe a partir da API 34
    private static String estrelas(float nota) {
        int cheias = Math.max(0, Math.min(5, (int) nota));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(i < cheias ? '★' : '☆');
        return sb.toString();
    }

    static class VH extends RecyclerView.ViewHolder {
        final View vAvatar;
        final ImageView imgPoster;
        final TextView txtAcao, txtTitulo, txtEstrelas;

        VH(@NonNull View v) {
            super(v);
            vAvatar = v.findViewById(R.id.vAvatar);
            imgPoster = v.findViewById(R.id.imgPoster);
            txtAcao = v.findViewById(R.id.txtAcao);
            txtTitulo = v.findViewById(R.id.txtTitulo);
            txtEstrelas = v.findViewById(R.id.txtEstrelas);
        }
    }
}
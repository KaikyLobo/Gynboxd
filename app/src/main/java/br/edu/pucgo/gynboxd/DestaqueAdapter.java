package br.edu.pucgo.gynboxd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DestaqueAdapter extends RecyclerView.Adapter<DestaqueAdapter.VH> {

    private final List<Destaque> itens;

    public DestaqueAdapter(List<Destaque> itens) {
        this.itens = itens;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_destaque, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Destaque d = itens.get(position);
        h.imgDestaque.setImageResource(d.imagemRes);
        h.txtNome.setText(d.nome);
        h.txtAno.setText(d.ano);
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ImageView imgDestaque;
        final TextView txtNome, txtAno;

        VH(@NonNull View v) {
            super(v);
            imgDestaque = v.findViewById(R.id.imgDestaque);
            txtNome = v.findViewById(R.id.txtNome);
            txtAno = v.findViewById(R.id.txtAno);
        }
    }
}
package br.edu.pucgo.gynboxd;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import br.edu.pucgo.gynboxd.databinding.ItemDestaqueBinding;

import java.util.List;

public class DestaqueAdapter extends RecyclerView.Adapter<DestaqueAdapter.VH> {

    /** Clique num item — implementado com lambda na Activity. */
    public interface OnDestaqueClick {
        void onClick(Destaque destaque);
    }

    private final List<Destaque> itens;
    private final OnDestaqueClick onClick;

    public DestaqueAdapter(List<Destaque> itens, OnDestaqueClick onClick) {
        this.itens = itens;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemDestaqueBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Destaque d = itens.get(position);
        h.b.imgDestaque.setImageResource(d.imagemRes);
        h.b.txtNome.setText(d.nome);
        h.b.txtAno.setText(d.ano);
        h.itemView.setOnClickListener(v -> onClick.onClick(d));
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    /** ViewHolder com View Binding: as views vêm de ItemDestaqueBinding, sem findViewById. */
    static class VH extends RecyclerView.ViewHolder {
        final ItemDestaqueBinding b;

        VH(@NonNull ItemDestaqueBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}

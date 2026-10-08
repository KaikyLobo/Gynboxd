package br.edu.pucgo.gynboxd;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import br.edu.pucgo.gynboxd.databinding.ItemListaBinding;
import java.util.List;

public class ListaAdapter extends RecyclerView.Adapter<ListaAdapter.ViewHolder> {

    private final List<ListaItem> lista;

    public ListaAdapter(List<ListaItem> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListaBinding binding = ItemListaBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListaItem item = lista.get(position);

        holder.binding.txtTitulo.setText(item.getTitulo());
        holder.binding.txtQuantidade.setText(item.getQuantidade());
        holder.binding.imgLista.setImageResource(item.getImagem());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemListaBinding binding;

        public ViewHolder(ItemListaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

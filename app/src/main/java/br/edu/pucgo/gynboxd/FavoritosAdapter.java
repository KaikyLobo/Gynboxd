package br.edu.pucgo.gynboxd;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import br.edu.pucgo.gynboxd.databinding.ItemFavoritoBinding;

import java.util.List;

public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.ViewHolder> {

    private final List<ListaItem> lista;

    public FavoritosAdapter(List<ListaItem> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavoritoBinding binding = ItemFavoritoBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.imgFavorito.setImageResource(
                lista.get(position).getImagem()
        );
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemFavoritoBinding binding;

        public ViewHolder(ItemFavoritoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

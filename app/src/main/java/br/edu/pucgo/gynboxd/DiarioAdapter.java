package br.edu.pucgo.gynboxd;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import br.edu.pucgo.gynboxd.databinding.ItemCabecalhoMesBinding;
import br.edu.pucgo.gynboxd.databinding.ItemDiarioBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Adapter do RecyclerView da tela "Seu diário".
 * Usa o padrão ViewHolder com View Binding e dois tipos de linha:
 * cabeçalho do mês ("SETEMBRO 2026") e entrada do diário.
 */
public class DiarioAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /** Callback de clique implementado com lambda na Activity. */
    public interface OnEntradaClickListener {
        void onEntradaClick(EntradaDiario entrada);
    }

    private static final int TIPO_CABECALHO = 0;
    private static final int TIPO_ENTRADA = 1;

    private static final Locale PT_BR = new Locale("pt", "BR");
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private final List<Object> itens = new ArrayList<>();   // String (mês) ou EntradaDiario
    private final OnEntradaClickListener listener;

    public DiarioAdapter(OnEntradaClickListener listener) {
        this.listener = listener;
    }

    /** Recebe as entradas, ordena da mais recente para a mais antiga e insere os cabeçalhos de mês. */
    public void setEntradas(List<EntradaDiario> entradas) {
        List<EntradaDiario> ordenadas = new ArrayList<>(entradas);
        Collections.sort(ordenadas, (a, b) -> Long.compare(b.getDataUtcMillis(), a.getDataUtcMillis()));

        SimpleDateFormat formatoMes = new SimpleDateFormat("MMMM yyyy", PT_BR);
        formatoMes.setTimeZone(UTC);

        itens.clear();
        String mesAtual = null;
        for (EntradaDiario e : ordenadas) {
            String mes = formatoMes.format(e.getDataUtcMillis()).toUpperCase(PT_BR);
            if (!mes.equals(mesAtual)) {
                itens.add(mes);
                mesAtual = mes;
            }
            itens.add(e);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return itens.get(position) instanceof String ? TIPO_CABECALHO : TIPO_ENTRADA;
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TIPO_CABECALHO) {
            return new CabecalhoViewHolder(ItemCabecalhoMesBinding.inflate(inflater, parent, false));
        }
        return new EntradaViewHolder(ItemDiarioBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = itens.get(position);
        if (holder instanceof CabecalhoViewHolder) {
            ((CabecalhoViewHolder) holder).bind((String) item);
        } else {
            ((EntradaViewHolder) holder).bind((EntradaDiario) item);
        }
    }

    // ---------- ViewHolders ----------

    static class CabecalhoViewHolder extends RecyclerView.ViewHolder {
        private final ItemCabecalhoMesBinding binding;

        CabecalhoViewHolder(ItemCabecalhoMesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String mes) {
            binding.tvMes.setText(mes);
        }
    }

    class EntradaViewHolder extends RecyclerView.ViewHolder {
        private final ItemDiarioBinding binding;

        EntradaViewHolder(ItemDiarioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(EntradaDiario entrada) {
            Calendar cal = Calendar.getInstance(UTC);
            cal.setTimeInMillis(entrada.getDataUtcMillis());

            binding.tvDia.setText(String.format(PT_BR, "%02d", cal.get(Calendar.DAY_OF_MONTH)));
            binding.tvNome.setText(entrada.getNome());
            binding.tvLocal.setText(entrada.getLocal());
            binding.ivCapa.setImageResource(entrada.getCapaRes());

            if (entrada.getNota() > 0) {
                binding.ratingNota.setRating(entrada.getNota());
                binding.ratingNota.setVisibility(View.VISIBLE);
            } else {
                binding.ratingNota.setVisibility(View.INVISIBLE);
            }

            // Lambda no lugar de classe anônima
            binding.cardEntrada.setOnClickListener(v -> listener.onEntradaClick(entrada));
        }
    }
}
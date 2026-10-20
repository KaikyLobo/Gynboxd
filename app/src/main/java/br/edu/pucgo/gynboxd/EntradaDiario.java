package br.edu.pucgo.gynboxd;

import androidx.annotation.DrawableRes;

import java.io.Serializable;

/**
 * Modelo de uma entrada do diário (um rolê registrado pelo usuário).
 * A data é guardada em milissegundos UTC, que é o formato devolvido pelo MaterialDatePicker.
 * Serializable para sobreviver à rotação de tela (onSaveInstanceState).
 */
public class EntradaDiario implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String nome;
    private final String local;
    private final long dataUtcMillis;
    private final float nota;          // 0 = sem avaliação
    @DrawableRes
    private final int capaRes;

    public EntradaDiario(String nome, String local, long dataUtcMillis, float nota, @DrawableRes int capaRes) {
        this.nome = nome;
        this.local = local;
        this.dataUtcMillis = dataUtcMillis;
        this.nota = nota;
        this.capaRes = capaRes;
    }

    public String getNome() { return nome; }

    public String getLocal() { return local; }

    public long getDataUtcMillis() { return dataUtcMillis; }

    public float getNota() { return nota; }

    @DrawableRes
    public int getCapaRes() { return capaRes; }
}
package br.edu.pucgo.gynboxd;

public class Atividade {
    public final String autor;
    public final String acao;      // texto depois do nome, ex.: " avaliou um filme..."
    public final String realce;    // trecho extra em negrito dentro da ação (pode ser null)
    public final String titulo;
    public final String ano;
    public final float nota;       // 0 = sem nota (esconde as estrelas)
    public final int avatarRes;
    public final int posterRes;

    public Atividade(String autor, String acao, String realce, String titulo,
                     String ano, float nota, int avatarRes, int posterRes) {
        this.autor = autor;
        this.acao = acao;
        this.realce = realce;
        this.titulo = titulo;
        this.ano = ano;
        this.nota = nota;
        this.avatarRes = avatarRes;
        this.posterRes = posterRes;
    }
}
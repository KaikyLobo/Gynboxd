package br.edu.pucgo.gynboxd;
public class ListaItem {

    private final String titulo;
    private final String quantidade;
    private final int imagem;

    public ListaItem(String titulo, String quantidade, int imagem) {
        this.titulo = titulo;
        this.quantidade = quantidade;
        this.imagem = imagem;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public int getImagem() {
        return imagem;
    }
}

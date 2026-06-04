package br.edu.ifba.lagos.servidor.impl;

public class Lago implements Comparable<Lago> {
    private int id;
    private String nome;

    public Lago(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }

    @Override
    public String toString() {
        return "Lago ID: " + id + " | Nome: " + nome;
    }

    @Override
    public int compareTo(Lago o) {
        return this.nome.compareTo(o.getNome());
    }
}
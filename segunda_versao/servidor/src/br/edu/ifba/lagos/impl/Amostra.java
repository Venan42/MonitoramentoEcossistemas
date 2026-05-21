package br.edu.ifba.lagos.impl;

public class Amostra implements Comparable<Amostra> {
    private Lago lago;
    private double valorPH;

    public Amostra(Lago lago, double valorPH) {
        this.lago = lago;
        this.valorPH = valorPH;
    }

    public Lago getLago() {
        return this.lago;
    }

    public double getValorPH() {
        return valorPH;
    }

    public void setValorPH(double valorPH) {
        this.valorPH = valorPH;
    }

    @Override
    public String toString() {
        return "ID Lago: " + lago.getId() + " | pH registrado: " + valorPH;
    }

    @Override
    public int compareTo(Amostra o) {
        return Double.compare(this.valorPH, o.getValorPH());
    }
}

public class ModeloLinha {
    private Integer numLinha;
    private Integer ano;
    private Integer precosCorrentes;
    private Integer variacaoRealAnual;

    public ModeloLinha() {
    }

    public void setNumLinha(Object numLinha) {
        this.numLinha = (Integer) numLinha;
    }

    public void setAno(Object ano) {
        this.ano = (Integer) ano;
    }

    public void setPrecosCorrentes(Object precosCorrentes) {
        this.precosCorrentes = (Integer) precosCorrentes;
    }

    public void setVariacaoRealAnual(Object variacaoRealAnual) {
        this.variacaoRealAnual = (Integer) variacaoRealAnual;
    }

    public Integer getNumLinha() {
        return numLinha;
    }

    public Integer getAno() {
        return ano;
    }

    public Integer getPrecosCorrentes() {
        return precosCorrentes;
    }

    public Integer getVariacaoRealAnual() {
        return variacaoRealAnual;
    }

    public void printarLinha() {
        System.out.println("Linha: " + getNumLinha() + "Ano: " + getAno() + "Preços Correntes: " + getPrecosCorrentes() + "Variação Real Anual: " + getVariacaoRealAnual());
    }


}

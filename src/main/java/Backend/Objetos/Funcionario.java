package Backend.Objetos;

public class Funcionario {
    private String nome;
    private String cpf;
    private String cargo;
    private Boolean temGestor;
    private Double valorHora;
    private Integer cargaHoraria;

    public Funcionario() {
    }

    public Funcionario(String nome, String cpf, String cargo, Boolean temGestor, Double valorHora, Integer cargaHoraria) {
        this.nome = nome;
        this.cpf = cpf;
        this.cargo = cargo;
        this.temGestor = temGestor;
        this.valorHora = valorHora;
        this.cargaHoraria = cargaHoraria;
    }

    public Double calcularSalario() {
        return valorHora * cargaHoraria;
    }

    @Override
    public String toString() {
        return """
                Nome: %s
                CPF: %s
                Cargo: %s
                TemGestor: %b""".formatted(nome,cpf,cargo,temGestor);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public Boolean getTemGestor() {
        return temGestor;
    }

    public void setTemGestor(Boolean temGestor) {
        this.temGestor = temGestor;
    }

    public Double getValorHora() {
        return valorHora;
    }

    public void setValorHora(Double valorHora) {
        this.valorHora = valorHora;
    }

    public Integer getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Integer cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }
}

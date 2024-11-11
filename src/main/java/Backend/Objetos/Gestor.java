package Backend.Objetos;

import java.util.ArrayList;
import java.util.List;

public class Gestor extends Funcionario {
    private Boolean temFuncionario;
    private List<Funcionario> funcionarios = new ArrayList<>();

    public Gestor() {
        this.funcionarios = new ArrayList<>();
    }

    public Gestor(String nome, String cpf, String cargo, Boolean temGestor, Double valorHora, Integer cargaHoraria, Boolean temFuncionario) {
        super(nome, cpf, cargo, temGestor, valorHora, cargaHoraria);
        this.temFuncionario = temFuncionario;
    }

    public void atribuirFuncionario(Funcionario funcionario) {
        this.funcionarios.add(funcionario);

        if (!temFuncionario) temFuncionario = true;

        if (!funcionario.getTemGestor()) funcionario.setTemGestor(true);
    }

    public void demitirFuncionario(Funcionario funcionario) {
        this.funcionarios.remove(funcionario);
    }

    @Override
    public String toString() {
        return """
                Nome: %s
                CPF: %s
                Cargo: %s
                TemFuncionario: %b
                Funcionarios: %s""".formatted(getNome(), getCpf(), getCargo(), temFuncionario, funcionarios);
    }

    public Boolean getTemFuncionario() {
        return temFuncionario;
    }

    public void setTemFuncionario(Boolean temFuncionario) {
        this.temFuncionario = temFuncionario;
    }

    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }
}

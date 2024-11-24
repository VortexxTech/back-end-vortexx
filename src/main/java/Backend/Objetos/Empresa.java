package Backend.Objetos;

import java.util.ArrayList;
import java.util.List;

public class Empresa{
    private String cpnj;
    private Gestor gestor;
    private List<Funcionario> funcionarios = new ArrayList();
    private List<Gestor> gestores = new ArrayList();

    public Empresa(String cpnj, Gestor gestor) {
        this.cpnj = cpnj;
        this.gestor = gestor;
    }

    public Empresa() {
    }

    public void adicionarGestor(Gestor gestor) {
        gestores.add(gestor);
    }

    public void adicionarFuncionario(Funcionario funcionario) {
        funcionarios.add(funcionario);
    }

    public void demitirFuncionario(Funcionario funcionario) { this.funcionarios.remove(funcionario); }

    public void demitirGestor(Gestor gestor) { this.gestores.remove(gestor); }

    public String getCpnj() {
        return cpnj;
    }

    public void setCpnj(String cpnj) {
        this.cpnj = cpnj;
    }

    public Gestor getGestor() {
        return gestor;
    }

    public void setGestor(Gestor gestor) {
        this.gestor = gestor;
    }

    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }

    public void setFuncionarios(List<Funcionario> funcionarios) {
        this.funcionarios = funcionarios;
    }
}

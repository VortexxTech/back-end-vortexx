package Backend.Objetos;

import java.util.ArrayList;
import java.util.List;

public class Empresa{
    private String cpnj;
    private List<Funcionario> funcionarios = new ArrayList();
    private List<Usuario> usuarios = new ArrayList();

    public Empresa(String cpnj) {
        this.cpnj = cpnj;
        this.funcionarios = new ArrayList<>();
        this.usuarios = new ArrayList<>();
    }

    public void adicionarUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    public void adicionarFuncionario(Funcionario funcionario) {
        funcionarios.add(funcionario);
    }

    public void removerUsuario(Usuario usuario) { this.usuarios.remove(usuario); }

    public void removerFuncionario(Funcionario funcionario) { this.funcionarios.remove(funcionario); }

    public String getCpnj() {
        return cpnj;
    }

    public void setCpnj(String cpnj) {
        this.cpnj = cpnj;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public List<Funcionario> getFuncionarios() {
        return funcionarios;
    }

    public void setFuncionarios(List<Funcionario> funcionarios) {
        this.funcionarios = funcionarios;
    }
}

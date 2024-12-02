package Backend.Objetos;

public class Funcionario extends Usuario {
    private String cpf;
    private String cargo;

    public Funcionario(String nome, String email, String senha, String cpf, String cargo) {
        super(nome, email, senha);
        this.cpf = cpf;
        this.cargo = cargo;
    }

    public void fazerSimulacao(String email, String senha, String cargo){
        if(email.equals(super.getEmail()) && senha.equals(super.getSenha())){
            System.out.println("Acessando a Simulação...");
        } else {
            System.out.println("E-mail e(ou) senha incorretos!");
        }
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
}

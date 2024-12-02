package Backend.Objetos;

import org.hibernate.event.spi.PreInsertEvent;

import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private String nome;
    private String email;
    private String senha;

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    public void consultarDashboard(String email, String senha){
        if(email.equals(this.email) && senha.equals(this.senha)){
            System.out.println("Acessando a Dashboard...");
        } else {
            System.out.println("E-mail e(ou) senha incorretos!");
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
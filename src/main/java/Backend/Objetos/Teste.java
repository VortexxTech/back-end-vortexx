package Backend.Objetos;

public class Teste {
    public static void main(String[] args) {
        Gestor gestor = new Gestor("Pedro", "01", "Gerente", false, 4.5, 600, false);

        Double salario = gestor.calcularSalario();

        System.out.println(salario);
    }
}

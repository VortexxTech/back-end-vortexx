package Backend.ApiArquivos;

import javax.annotation.processing.Generated;

@Entity
@Table(name = "DadosInseridos")
public class DadosInseridos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bairro;
    private Double valorM2;
    private Double densidade;
    private Double idh;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public Double getValorM2() {
        return valorM2;
    }

    public void setValorM2(Double valorM2) {
        this.valorM2 = valorM2;
    }

    public Double getDensidade() {
        return densidade;
    }

    public void setDensidade(Double densidade) {
        this.densidade = densidade;
    }

    public Double getIdh() {
        return idh;
    }

    public void setIdh(Double idh) {
        this.idh = idh;
    }
}

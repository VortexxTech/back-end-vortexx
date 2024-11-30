package Backend;

public enum Bairros {
    AC("Acre", "AC", "Norte"),
    AL("Alagoas", "AL", "Nordeste"),
    AM("Amazonas", "AM", "Norte"),
    AP("Amapá", "AP", "Norte"),
    BA("Bahia", "BA", "Nordeste"),
    CE("Ceará", "CE", "Nordeste"),
    DF("Distrito Federal", "DF", "Centro-Oeste"),
    ES("Espírito Santo", "ES", "Sudeste"),
    GO("Goiás", "GO", "Centro-Oeste"),
    MA("Maranhão", "MA", "Nordeste"),
    MG("Minas Gerais", "MG", "Sudeste"),
    MS("Mato Grosso do Sul", "MS", "Centro-Oeste"),
    MT("Mato Grosso", "MT", "Centro-Oeste"),
    PA("Pará", "PA", "Norte"),
    PB("Paraíba", "PB", "Nordeste"),
    PE("Pernambuco", "PE", "Nordeste"),
    PI("Piauí", "PI", "Nordeste"),
    PR("Paraná", "PR", "Sul"),
    RJ("Rio de Janeiro", "RJ", "Sudeste"),
    RN("Rio Grande do Norte", "RN", "Nordeste"),
    RO("Rondônia", "RO", "Norte"),
    RR("Roraima", "RR", "Norte"),
    RS("Rio Grande do Sul", "RS", "Sul"),
    SC("Santa Catarina", "SC", "Sul"),
    SE("Sergipe", "SE", "Nordeste"),
    SP("São Paulo", "SP", "Sudeste"),
    TO("Tocantins", "TO", "Norte");

    private final String nome;
    private final String zona;

    Bairros(String nome, String sigla, String zona) {
        this.nome = nome;
        this.zona = zona;
    }

    public String getNome() {
        return nome;
    }

    public String getZona() {
        return zona;
    }
}

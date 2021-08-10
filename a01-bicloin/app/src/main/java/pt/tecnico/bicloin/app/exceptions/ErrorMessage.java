package pt.tecnico.bicloin.app.exceptions;

public enum ErrorMessage {
    INVALID_INPUT("ERRO input invalido"),
    INVALID_VALUE("ERRO valor invalido"),
    INVALID_COORDINATES("ERRO coordenadas invalidas"),
    INVALID_STATION("ERRO estacao invalida"),
    OUT_OF_RANGE("ERRO fora do alcance"),
    INVALID_TAG("ERRO tag nao existe");

    public final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}

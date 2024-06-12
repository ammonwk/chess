package DTOs;

public record JoinGameRequest(String authToken, int gameID, String playerColor) {}

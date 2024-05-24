package service;

import java.util.List;

public record ListGamesResult(List<GameSummary> games) {
    public static record GameSummary(int gameID, String whiteUsername, String blackUsername, String gameName) {}
}

package ru.bastrich.exception

class GameDoesntExistException(gameId: Long) : Exception("GameFull $gameId doesn't exist")
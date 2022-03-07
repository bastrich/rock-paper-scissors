package ru.bastrich.exception

class IllegalMoveException(userId: Long, gameId: Long) : Exception("Game $gameId dosn't relate to the user $userId")
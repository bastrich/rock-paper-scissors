package ru.bastrich.exception

class UserDoesntExistException(userId: Long) : Exception("User $userId doesn't exist")
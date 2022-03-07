package ru.bastrich.service

import org.springframework.stereotype.Service
import ru.bastrich.db.dao.UserDao

@Service
class UserService(
    private val userDao: UserDao
) {

    fun createUser() = userDao.addUser()
}
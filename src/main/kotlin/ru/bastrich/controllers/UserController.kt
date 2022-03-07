package ru.bastrich.controllers

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import ru.bastrich.dto.CreateUserResponse
import ru.bastrich.service.UserService

@RestController
class UserController(
    private val userService: UserService
) {

    @PostMapping("/users/create")
    fun createUser() = CreateUserResponse(userService.createUser().id)
}


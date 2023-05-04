package com.tw2.prepaid.domain.member

import com.tw2.prepaid.common.response.Response
import com.tw2.prepaid.domain.member.dto.request.UserRequest
import com.tw2.prepaid.domain.member.dto.response.UserResponse
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/prepaid/v1.0/users"])
class UserController(
    private val userService: UserService
) {
    @GetMapping("/{userId}")
    fun getUser(@PathVariable userId: Long): Response<UserResponse> =
        Response(data = userService.getUser(userId))

    @PutMapping("/{userId}")
    fun updateMember(@PathVariable userId: Long, @RequestBody req: UserRequest): Response<UserResponse> =
        Response(data = userService.updateUser(userId = userId, req = req))

    @DeleteMapping("/{userId}")
    fun deleteMember(@PathVariable userId: Long): Response<Unit> = Response(data = userService.deleteUser(userId = userId))
}
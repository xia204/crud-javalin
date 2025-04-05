package mx.edu.uttt.auth

import mx.edu.uttt.auth.AccessManager.userInfo
import mx.edu.uttt.auth.accounts.UserAccountService
import io.javalin.http.*
import io.javalin.http.util.NaiveRateLimit
import org.mindrot.jbcrypt.BCrypt
import java.util.concurrent.TimeUnit

object LoginController {
    private data class Credentials(val nombreUsuario: String, val contraseña: String, val perfil: String)

    fun signIn(ctx: Context) {
        try {
            NaiveRateLimit.requestPerTimeUnit(ctx, 3, TimeUnit.MINUTES)
        } catch (e: HttpResponseException) {
            throw InternalServerErrorResponse("Límite de intentos excedido")
        }

        val (nombreUsuario, contraseña, perfil) = ctx.bodyValidator<Credentials>()
            .check({ it.nombreUsuario.isNotBlank() && it.contraseña.isNotBlank() && it.perfil.isNotBlank() }, "Usuario, contraseña y rol no pueden ser nulos")
            .get()

        val user = UserAccountService.selectByUserName(nombreUsuario)
            ?: throw BadRequestResponse("Usuario no encontrado")

        if (!BCrypt.checkpw(contraseña, user.contraseña)) throw BadRequestResponse("Contraseña incorrecta")

        if (!perfil.equals(user.perfil.name, ignoreCase = true)) {
            throw BadRequestResponse("Perfil incorrecto")
        }

        ctx.sessionAttribute("user", user)

        // NUEVO: guardar también en userInfo
        val info = AccessManager.UserInfo(
            id = user.id!!,
            nombreUsuario = user.nombreUsuario,
            contraseña = user.contraseña,
            perfil = user.perfil,
            modulos = user.permisos
        )
        ctx.userInfo = info

        ctx.json(mapOf("message" to "success", "user" to user))
    }


    fun signOut(ctx: Context) {
        ctx.sessionAttribute("user", null)
        ctx.result("success")
    }


    /* Clears Context userInfo
    fun signOut(ctx: Context) {
        ctx.userInfo = null
        ctx.result("success")
    }*/
}
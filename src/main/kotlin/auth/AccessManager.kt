package mx.edu.uttt.auth

import mx.edu.uttt.auth.Perfil.*
import mx.edu.uttt.auth.accounts.UserAccountService
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import io.javalin.http.UnauthorizedResponse
import io.javalin.security.RouteRole
import java.io.Serializable

enum class Perfil : RouteRole {
    ADMINISTRADOR, GERENTE, SECRETARIA, UNAUTHENTICATED
}

object AccessManager {

    /* Manejo de permisos según el perfil del usuario */
    fun handleAccess(ctx: Context) {
        ctx.refreshUserInfo()
        val permittedRoles = ctx.routeRoles()

        when {
            UNAUTHENTICATED in permittedRoles || permittedRoles.isEmpty() -> return
            ctx.userInfo == null -> ctx.redirect("login") // Redirige a login si no está autenticado
            ctx.userInfo!!.perfil in permittedRoles -> return
            else -> throw UnauthorizedResponse()
        }
    }

    /* Actualiza la información del usuario */
    private fun Context.refreshUserInfo() {
        userInfo?.also {
            val user = UserAccountService.selectById(it.id) // Obtiene los datos del usuario
            val permisos = UserAccountService.getUserPermissions(it.id) // Obtiene los permisos
            userInfo = AccessManager.UserInfo(
                id = user.id ?: 0,
                nombreUsuario = user.nombreUsuario,
                contraseña = user.contraseña,
                perfil = user.perfil,
                modulos = permisos, // Aquí se asigna correctamente
            )

        }
    }

    /* Clase que representa la información del usuario */
    data class UserInfo(
        var id: Int,
        val nombreUsuario: String,
        var contraseña: String,
        var perfil: Perfil = UNAUTHENTICATED,
        val modulos: List<ModuloPermisos> = listOf(),
    ) : Serializable {

        /* Mapeo de perfil desde la base de datos */
        fun mapPerfil(nombrePerfil: String) {
            perfil = when (nombrePerfil) {
                "Administrador" -> Perfil.ADMINISTRADOR
                "Gerente" -> Perfil.GERENTE
                "Secretaria" -> Perfil.SECRETARIA
                else -> Perfil.UNAUTHENTICATED
            }
            contraseña = "**************" // Oculta la contraseña en memoria
        }
    }

    /* Clase que representa los permisos de un módulo */
    data class ModuloPermisos(
        val nombreModulo: String,
        val urlModulo: String,
        val puedeAgregar: Boolean,
        val puedeEditar: Boolean,
        val puedeEliminar: Boolean,
        val puedeConsultar: Boolean,
        val puedeExportar: Boolean,
        val puedeBitacora: Boolean
    )

    /* Extensión de Context para gestionar userInfo */
    var Context.userInfo: UserInfo?
        get() = this.sessionAttribute("USER_INFO")
        set(userInfo) = this.sessionAttribute("USER_INFO", userInfo)
}

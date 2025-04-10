package mx.edu.uttt.auth

import mx.edu.uttt.auth.Perfil.*
import mx.edu.uttt.auth.accounts.UserAccountService
import io.javalin.http.BadRequestResponse
import io.javalin.http.Context
import io.javalin.http.UnauthorizedResponse
import io.javalin.security.RouteRole
import mx.edu.uttt.routes.RutaModuloService
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
    /* Actualiza la información del usuario */
    private fun Context.refreshUserInfo() {
        userInfo?.also {
            val user = UserAccountService.selectById(it.id) // Obtiene los datos del usuario
            val permisos = UserAccountService.getUserPermissions(it.id) // Obtiene los permisos

            // Obtén las rutas asociadas al perfil
            val rutas = RutaModuloService.getRoutesByPerfil().filter { ruta ->
                ruta.perfiles.contains(it.perfil) // Filtra las rutas según el perfil del usuario
            }

            // Asigna los módulos obtenidos
            val modulos = rutas.map { ruta ->
                // Mapea las rutas a la estructura de ModuloPermisos
                ModuloPermisos(
                    nombreModulo = ruta.vueComponent,
                    urlModulo = ruta.path,
                    puedeAgregar = true,  // Ajusta según la lógica de permisos
                    puedeEditar = true,
                    puedeEliminar = true,
                    puedeConsultar = true,
                    puedeExportar = true,
                    puedeBitacora = true
                )
            }

            // Actualiza la información del usuario
            userInfo = AccessManager.UserInfo(
                id = user.id ?: 0,
                nombreUsuario = user.nombreUsuario,
                contraseña = user.contraseña,
                perfil = user.perfil,
                modulos = modulos, // Asigna los módulos aquí
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
        fun mapPerfil(nombrePerfil: String): Perfil {
            val mapped = mapPerfil(nombrePerfil) // usa la función global
            perfil = mapped
            contraseña = "**************"
            return mapped
        }
    }

    /* Mapeo de perfil desde la base de datos */
    // Al final o al inicio del archivo, fuera de cualquier clase u objeto
    fun mapPerfil(nombrePerfil: String): Perfil {
        return when (nombrePerfil.lowercase()) {
            "administrador" -> Perfil.ADMINISTRADOR
            "gerente" -> Perfil.GERENTE
            "secretaria" -> Perfil.SECRETARIA
            else -> Perfil.UNAUTHENTICATED
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

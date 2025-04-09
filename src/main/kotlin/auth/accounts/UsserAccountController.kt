package mx.edu.uttt.auth.accounts

import io.javalin.apibuilder.CrudHandler
import io.javalin.http.Context
import mx.edu.uttt.auth.AccessManager.userInfo
import mx.edu.uttt.auth.Perfil.ADMINISTRADOR
import mx.edu.uttt.auth.accounts.UserAccountService
import com.vas.Utils.properTrim
import io.javalin.http.bodyValidator
import org.mindrot.jbcrypt.BCrypt
import java.util.concurrent.CompletableFuture.supplyAsync

object UsserAccountController : CrudHandler {
    override fun create(ctx: Context) {
        ctx.bodyValidator<UserAccount>().get().apply {
            nombreUsuario = nombreUsuario.properTrim()
            contraseña = BCrypt.hashpw(contraseña, BCrypt.gensalt()) // Hashear la contraseña
        }.also { userAccount ->
            ctx.future { supplyAsync { UserAccountService.insert(userAccount) }.thenAccept(ctx::result) }
        }

    }

    override fun delete(ctx: Context, resourceId: String) {
        val id = resourceId.toIntOrNull() ?: throw IllegalArgumentException("ID inválido")
        ctx.future { supplyAsync { UserAccountService.delete(id) }.thenAccept(ctx::result) }
    }

    override fun getAll(ctx: Context) {
        val usuariosBase = if (ctx.userInfo!!.perfil != ADMINISTRADOR) {
            UserAccountService.selectAll().filter { it.nombreUsuario != "sys.admin" }
        } else {
            UserAccountService.selectAll()
        }

        val usuariosConPermisos = usuariosBase.map { usuario ->
            usuario.copy(
                permisos = UserAccountService.getUserPermissions(usuario.id ?: 0)
            )
        }

        ctx.json(usuariosConPermisos)
    }

    override fun getOne(ctx: Context, resourceId: String) {
        val id = resourceId.toIntOrNull() ?: throw IllegalArgumentException("ID inválido")
        ctx.future { supplyAsync { UserAccountService.selectById(id) }.thenAccept(ctx::json) }
    }

    override fun update(ctx: Context, resourceId: String) {
        val id = resourceId.toIntOrNull() ?: throw IllegalArgumentException("ID inválido")
        ctx.bodyValidator<UserAccount>().get().apply {
            this.id = id
            nombreUsuario = nombreUsuario.properTrim()
        }.also { userAccount ->
            ctx.future { supplyAsync { UserAccountService.update(userAccount) }.thenAccept(ctx::result) }
        }
    }
}
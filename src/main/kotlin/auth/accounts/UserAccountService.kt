package mx.edu.uttt.auth.accounts

import mx.edu.uttt.auth.Perfil.*
import mx.edu.uttt.auth.AccessManager.ModuloPermisos
import mx.edu.uttt.auth.Perfil
import com.vas.Utils.dbErrorHandler
import com.vas.Utils.hashPwd
import io.javalin.http.InternalServerErrorResponse
import kotliquery.HikariCP
import kotliquery.Row
import kotliquery.queryOf
import kotliquery.sessionOf
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.SQLException

data class UserAccount(
    var id: Int? = null,
    var nombreUsuario: String = "",
    var contraseña: String = "***************",
    var perfil: Perfil = Perfil.UNAUTHENTICATED,
    var permisos: List<ModuloPermisos> = listOf()
)

object UserAccountService {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private fun toUserAccount(row: Row) = UserAccount(
        id = row.int("id"),
        nombreUsuario = row.string("nombreUsuario"),
        contraseña = row.string("contraseña"),
    )

    fun selectAll(): List<UserAccount> {
        val qry = queryOf(
            """
        SELECT u.id, u.nombreUsuario, p.nombrePerfil
        FROM usuarios u
        JOIN perfiles p ON u.perfil_id = p.id
        ORDER BY u.nombreUsuario
        """.trimIndent()
        ).map { row ->
            val id = row.int("id")
            UserAccount(
                id = id,
                nombreUsuario = row.string("nombreUsuario"),
                perfil = mapPerfil(row.string("nombrePerfil")),
                permisos = getUserPermissions(id) // ← AQUI AGREGAS LOS PERMISOS
            )
        }.asList

        return sessionOf(HikariCP.dataSource()).use { it.run(qry) }
    }


    fun selectById(id: Int): UserAccount {
        val qry = queryOf(
            """
            SELECT u.id, u.nombreUsuario, p.nombrePerfil
            FROM usuarios u
            JOIN perfiles p ON u.perfil_id = p.id
            WHERE u.id = ?
            """.trimIndent(), id
        ).map { row ->
            UserAccount(
                id = row.int("id"),
                nombreUsuario = row.string("nombreUsuario"),
                perfil = mapPerfil(row.string("nombrePerfil")),
                permisos = getUserPermissions(id)
            )
        }.asSingle

        return sessionOf(HikariCP.dataSource()).use { it.run(qry) }
            ?: throw InternalServerErrorResponse("No existe ese ID")
    }

    fun getUserPermissions(userId: Int): List<ModuloPermisos> {
        val qry = queryOf(
            """
            SELECT m.nombreModulo, m.url, pm.bitAgregar, pm.bitEditar, pm.bitEliminar, 
                   pm.bitConsultar, pm.bitExportar, pm.bitBitacora
            FROM perilModulo pm
            JOIN modulos m ON pm.modulo_id = m.id
            JOIN usuarios u ON u.perfil_id = pm.perfil_id
            WHERE u.id = ?
            """.trimIndent(), userId
        ).map { row ->
            ModuloPermisos(
                nombreModulo = row.string("nombreModulo"),
                urlModulo = row.string("url"),
                puedeAgregar = row.boolean("bitAgregar"),
                puedeEditar = row.boolean("bitEditar"),
                puedeEliminar = row.boolean("bitEliminar"),
                puedeConsultar = row.boolean("bitConsultar"),
                puedeExportar = row.boolean("bitExportar"),
                puedeBitacora = row.boolean("bitBitacora")
            )
        }.asList

        return sessionOf(HikariCP.dataSource()).use { it.run(qry) }
    }

    fun selectByUserName(userName: String): UserAccount? {
        val qry = queryOf(
            """
        SELECT u.id, u.nombreUsuario, u.contraseña, p.nombrePerfil
        FROM usuarios u
        JOIN perfiles p ON u.perfil_id = p.id
        WHERE u.nombreUsuario = ?
        """.trimIndent(), userName
        ).map { row ->
            UserAccount(
                id = row.int("id"),
                nombreUsuario = row.string("nombreUsuario"),
                contraseña = row.string("contraseña"),
                perfil = mapPerfil(row.string("nombrePerfil")),
                permisos = getUserPermissions(row.int("id"))
            )
        }.asSingle

        return sessionOf(HikariCP.dataSource()).use { it.run(qry) }
    }


    fun insert(acc: UserAccount): String {
        val qry = queryOf(
            """
            INSERT INTO usuarios (nombreUsuario, contraseña, perfil_id)
            VALUES (?, ?, (SELECT id FROM perfiles WHERE nombrePerfil = ?))
            """.trimIndent(),
            acc.nombreUsuario,
            acc.contraseña,
            acc.perfil.name
        )

        return sessionOf(HikariCP.dataSource()).use {
            try {
                if (it.run(qry.asUpdate) > 0) "success" else "failed"
            } catch (ex: SQLException) {
                dbErrorHandler(log, ex.message)
                "failed"
            }
        }
    }

    fun update(acc: UserAccount): String {
        val passwordUpdate = if (!acc.contraseña.startsWith("*")) {
            "contraseña = ?,"
        } else ""

        val qry = queryOf(
            """
            UPDATE usuarios SET
                nombreUsuario = ?,
                $passwordUpdate
                perfil_id = (SELECT id FROM perfiles WHERE nombrePerfil = ?)
            WHERE id = ?
            """.trimIndent(),
            acc.nombreUsuario,
            if (!acc.contraseña.startsWith("*")) hashPwd(acc.contraseña) else null,
            acc.perfil.name,
            acc.id
        )

        return sessionOf(HikariCP.dataSource()).use {
            try {
                if (it.run(qry.asUpdate) > 0) "success" else "failed"
            } catch (ex: SQLException) {
                dbErrorHandler(log, ex.message)
                "failed"
            }
        }
    }

    fun delete(id: Int): String {
        val qry = queryOf("DELETE FROM usuarios WHERE id = ?", id)
        return sessionOf(HikariCP.dataSource()).use {
            try {
                if (it.run(qry.asUpdate) > 0) "success" else "failed"
            } catch (ex: SQLException) {
                dbErrorHandler(log, ex.message)
                "failed"
            }
        }
    }

    private fun mapPerfil(nombrePerfil: String): Perfil {
        return when (nombrePerfil) {
            "Administrador" -> ADMINISTRADOR
            "Gerente" -> GERENTE
            "Secretaria" -> SECRETARIA
            else -> UNAUTHENTICATED
        }
    }
}



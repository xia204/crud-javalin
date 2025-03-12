package mx.edu.uttt.empleados

import io.javalin.http.InternalServerErrorResponse
import kotliquery.HikariCP
import kotliquery.Row
import kotliquery.queryOf
import kotliquery.sessionOf

data class Empleado(
    var id: String,
    var nombre: String,
    var apellido_paterno: String,
    var apellido_materno: String,
    var correo_empresarial: String,
    var nombre_usuario: String,
    var contraseña: String
)

object EmpleadosService {

    private fun rowToEmpleados(row: Row) = Empleado(
        id = row.string("id"),
        nombre = row.string("nombre"),
        apellido_paterno = row.string("apellido_paterno"),
        apellido_materno = row.string("apellido_materno"),
        correo_empresarial = row.string("correo_empresarial"),
        nombre_usuario = row.string("nombre_usuario"),
        contraseña = row.string("contraseña"),
    )

    fun selectAll(): List<Empleado> {
        val qry = queryOf("""
            SELECT id, nombre, apellido_paterno, apellido_materno, correo_empresarial, nombre_usuario, contraseña
            FROM empleados ORDER BY id
        """.trimIndent())
            .map(::rowToEmpleados)
            .asList

        val result: List<Empleado>
        sessionOf(HikariCP.dataSource()).use { conn ->
            result = conn.run(qry)
        }
        return result
    }

    fun selectById(id: String): Empleado {
        val qry = queryOf("""
            SELECT id, nombre, apellido_paterno, apellido_materno, correo_empresarial, nombre_usuario, contraseña
            FROM empleados
            WHERE id = ?
        """.trimIndent(), id)
            .map(::rowToEmpleados)
            .asSingle

        val result: Empleado
        sessionOf(HikariCP.dataSource()).use { conn ->
            result = conn.run(qry) ?: throw InternalServerErrorResponse("No existe ese elemento en la BD")
        }
        return result
    }

    //Funcion de Insercion
    fun insert(empleado: Empleado): String {
        val qry = queryOf(
            """
        INSERT INTO empleados (nombre, apellido_paterno, apellido_materno, 
                               correo_empresarial, nombre_usuario, contraseña) 
        VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent(),
            empleado.nombre,
            empleado.apellido_paterno,
            empleado.apellido_materno,
            empleado.correo_empresarial,
            empleado.nombre_usuario,
            empleado.contraseña
        )

        var result = "failed"
        sessionOf(HikariCP.dataSource()).use { conn ->
            result = if (conn.run(qry.asUpdate) > 0) empleado.id
            else throw InternalServerErrorResponse("No se puede insertar en la base de datos")
        }
        return """{"id": "$result"}"""
    }

    //Función para eliminar
    fun delete(id: String): String {
        val qry = queryOf(
            """
        DELETE FROM empleados
        WHERE id = ?
        """.trimIndent(), id
        )

        var result = "failed"
        sessionOf(HikariCP.dataSource()).use { conn ->
            result = if (conn.run(qry.asUpdate) > 0) "success"
            else throw InternalServerErrorResponse("No se puede borrar")
        }
        return result
    }
    //Función para actualizar
    fun update(empleado: Empleado): String {
        val qry = queryOf("""
        UPDATE empleados SET 
        nombre = ?,
        apellido_paterno = ?, 
        apellido_materno = ?,
        correo_empresarial = ?,
        nombre_usuario = ?, 
        contraseña = ?
        WHERE id = ?
        """.trimIndent(),
            empleado.nombre,
            empleado.apellido_paterno,
            empleado.apellido_materno,
            empleado.correo_empresarial,
            empleado.nombre_usuario,
            empleado.contraseña,
            empleado.id
        )

        var result = "failed"
        sessionOf(HikariCP.dataSource()).use { conn ->
            result = if (conn.run(qry.asUpdate) > 0) "success"
            else throw InternalServerErrorResponse("No se puede actualizar")
        }
        return result
    }

}

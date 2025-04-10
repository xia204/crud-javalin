package mx.edu.uttt.routes

import kotliquery.HikariCP
import kotliquery.queryOf
import kotliquery.sessionOf
import mx.edu.uttt.auth.Perfil
import mx.edu.uttt.auth.AccessManager.mapPerfil

data class RutaModulo(
    val path: String,
    val vueComponent: String,
    val perfiles: List<Perfil>
)
object RutaModuloService{
    fun getRoutesByPerfil(): List<RutaModulo> {
        val qry = queryOf(
            """
        SELECT p.nombrePerfil, m.nombreModulo, m.url
        FROM perilModulo pm
        JOIN perfiles p ON pm.perfil_id = p.id
        JOIN modulos m ON pm.modulo_id = m.id
        """.trimIndent()
        ).map { row ->
            Triple(
                row.string("nombrePerfil"),
                row.string("nombreModulo"),
                row.string("url")
            )
        }.asList

        val registros = sessionOf(HikariCP.dataSource()).use { it.run(qry) }

        return registros
            .groupBy { it.third } // agrupar por URL
            .map { (url, lista) ->
                RutaModulo(
                    path = url.trim('/'), // "empleados"
                    vueComponent = lista.first().second + "-page", // "empleados-page"
                    perfiles = lista.map { mapPerfil(it.first) }.distinct()
                )
            }
    }

}

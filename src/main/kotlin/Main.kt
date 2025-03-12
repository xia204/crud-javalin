package mx.edu.uttt

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.http.staticfiles.Location
import io.javalin.vue.VueComponent
import kotliquery.HikariCP
import mx.edu.uttt.empleados.EmpleadosController
import mx.edu.uttt.empleados.EmpleadosService

fun main() {
    HikariCP.default(
        "jdbc:mysql://database-1.cj8syu8iitpc.us-east-2.rds.amazonaws.com:3306/crudjavalin?serverTimezone=UTC&useSSL=false",
        "admin",
        "Axcvp.2031"
    )
    println(EmpleadosService.selectAll())

    val app = Javalin.create { config ->
        config.staticFiles.apply {
            enableWebjars()
            add("public", Location.CLASSPATH)
        }
        //connect to vue
        config.vue.apply {
            vueInstanceNameInJs = "app"
            rootDirectory("/vue", Location.CLASSPATH)
        }
        //
        config.router.mount {
        //montar la session
        }.apiBuilder {
            get("/", VueComponent("home-page"))
            get("empleados", VueComponent("empleados-page"))
            get("form", VueComponent("form-page"))
            //RestFull API End Points
            path("api"){
                crud("empleados/{id}", EmpleadosController)
            }
        }
    //Este .start() sirve de forma local
    //}.start()
    //volver esta linea a la normalidad para subir a AWS
    }.start("0.0.0.0", 8080)
}
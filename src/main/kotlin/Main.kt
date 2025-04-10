package mx.edu.uttt

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.http.staticfiles.Location
import io.javalin.vue.VueComponent
import kotliquery.HikariCP
import mx.edu.uttt.auth.AccessManager
import mx.edu.uttt.auth.AccessManager.userInfo
import mx.edu.uttt.auth.LoginController
import mx.edu.uttt.auth.Perfil
import mx.edu.uttt.auth.accounts.UserAccountService
import mx.edu.uttt.auth.accounts.UsserAccountController
import mx.edu.uttt.empleados.EmpleadosController
import mx.edu.uttt.empleados.EmpleadosService
import mx.edu.uttt.routes.RutaModulo
import mx.edu.uttt.routes.RutaModuloService.getRoutesByPerfil

fun main() {
    HikariCP.default(
        //AWS

        //"jdbc:mysql://database-1.cj8syu8iitpc.us-east-2.rds.amazonaws.com:3306/crudjavalin?serverTimezone=UTC&useSSL=false",
        //"admin",
        //"Axcvp.2031"

        //loacal
        "jdbc:mysql://localhost:3306/crudjavalin?serverTimezone=UTC&useSSL=false",
        "root",
        "12345678"
    )
    println(EmpleadosService.selectAll())
    println(UserAccountService.selectAll())

    val app = Javalin.create { config ->
        config.staticFiles.apply {
            enableWebjars()
            add("public", Location.CLASSPATH)
        }
        //connect to vue
        config.vue.apply {
            vueInstanceNameInJs = "app"
            rootDirectory("/vue", Location.CLASSPATH)
            stateFunction = { ctx -> println("  userInfo en stateFunction: ${ctx.userInfo}")
                mapOf("userInfo" to ctx.userInfo) }
        }
        //
        config.router.mount {
            it.beforeMatched(AccessManager::handleAccess)
        //montar la session
        }.apiBuilder {
            get("login",VueComponent("login-page"), Perfil.UNAUTHENTICATED)
            get("/", VueComponent("home-page"), Perfil.ADMINISTRADOR, Perfil.GERENTE)
//            get("empleados", VueComponent("empleados-page"), Perfil.ADMINISTRADOR,Perfil.GERENTE)
//            get("form", VueComponent("form-page"), Perfil.ADMINISTRADOR)
//            get("proveedores", VueComponent("proveedores-page"), Perfil.ADMINISTRADOR)

            //Rutas dinamicas
            getRoutesByPerfil().forEach { ruta ->
                get(ruta.path, VueComponent(ruta.vueComponent), *ruta.perfiles.toTypedArray())
            }

            //RestFull API End Points
            path("api"){
                post("login", LoginController::signIn, Perfil.UNAUTHENTICATED)
                post("logout", LoginController::signOut, Perfil.UNAUTHENTICATED)

                crud("empleados/{id}", EmpleadosController)
                crud("usuarios/{id}", UsserAccountController)
            }
        }
    //Este .start() sirve de forma local
    }.start()
    //volver esta linea a la normalidad para subir a AWS
    //}.start("0.0.0.0", 8080)
}
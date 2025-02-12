package mx.edu.uttt

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.http.staticfiles.Location
import io.javalin.vue.VueComponent

fun main() {
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
        }.apiBuilder{
            get("/", VueComponent("home-page"))
            get("crud", VueComponent("crud-page"))
        }
    }.start()
}
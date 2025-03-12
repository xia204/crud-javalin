package mx.edu.uttt.empleados

import io.javalin.apibuilder.CrudHandler
import io.javalin.http.Context
import io.javalin.http.bodyValidator
import java.util.*
import java.util.concurrent.CompletableFuture

object EmpleadosController: CrudHandler {
    override fun create(ctx: Context) {
        ctx.bodyValidator<Empleado>().get().apply {
            id = UUID.randomUUID().toString().uppercase()
        }.also { empleado: Empleado ->
            ctx.future { CompletableFuture.supplyAsync { EmpleadosService.insert(empleado) }
                .thenAccept(ctx::result)
            }
        }
    }

    override fun delete(ctx: Context, resourceId: String) {
        ctx.future {
            CompletableFuture.supplyAsync { EmpleadosService.delete(resourceId) }
                .thenAccept(ctx::result)
        }
    }

    override fun getAll(ctx: Context) {
        ctx.future {
            CompletableFuture.supplyAsync { EmpleadosService.selectAll() }
                .thenAccept(ctx::json)
        }
    }

    override fun getOne(ctx: Context, resourceId: String) {
        ctx.future {
            CompletableFuture.supplyAsync { EmpleadosService.selectById(resourceId) }
                .thenAccept(ctx::json)
        }
    }

    override fun update(ctx: Context, resourceId: String) {
//        val empleado = ctx.bodyValidator<Empleado>().get()
//        empleado.id = resourceId.toInt().toString() // Convertir `resourceId` a entero y asignarlo
//
//        ctx.future {
//            CompletableFuture.supplyAsync { EmpleadosService.update(empleado) }
//                .thenAccept(ctx::result)
//        }
        ctx.bodyValidator<Empleado>().get().also { empleado ->
            ctx.future {
                CompletableFuture.supplyAsync { EmpleadosService.update(empleado) }
                    .thenAccept(ctx::result)
            }
        }
    }
}
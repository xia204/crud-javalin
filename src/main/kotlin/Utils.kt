package com.vas

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.javalin.http.InternalServerErrorResponse
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.Logger
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties

object Utils {
    /* String Proper Trim */
    fun String.properTrim(separator: String = " ") = this.trim().split("\\s+".toRegex()).joinToString(separator)

    /* Generate a 36 char random UUID */
    fun randId() = UUID.randomUUID().toString().uppercase()

    /* */
    fun hashPwd(password: String) = BCrypt.hashpw(password, BCrypt.gensalt())

    /* Simple Date Format dd-MM-yy HH:mm:ss*/
    fun strDateTime(strDate: String, out: String = "dd-MM-yyyy HH:mm:ss"): String {
        val parse = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(strDate)
        return SimpleDateFormat(out).format(parse)
    }

    fun strDate(): String {
        val date = LocalDate.now()
        val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return date.format(format)
    }

    /* */
    fun dirInspectionPath(id: String, date: String, createDir: Boolean = false): String {
        val dateArray = date.split("-")
        val yyyy = File("photos/inspections/", dateArray[0])
        val mm = File(yyyy, dateArray[1])
        val dd = File(mm, dateArray[2])
        if (createDir) dd.mkdirs()
        return "${dd.path}${File.separator}$id"
    }

    /* */
    val mapper = jacksonObjectMapper()

    /* From Any Object to Map<String, T value> */
    fun <T : Any> toMap(obj: T): MutableMap<String, Any?> {
        return (obj::class as KClass<T>).memberProperties.associate { prop ->
            prop.name to prop.get(obj)?.let { value ->
                if (value::class.isData) toMap(value) else value
            }
        } as MutableMap<String, Any?>
    }

    /* Database exception handler */
    fun dbErrorHandler(log: Logger, msg: String?) {
        msg?.apply {
            val err = split(";")
            log.error(this)
            throw InternalServerErrorResponse(err[2])
        }
    }

    /* Handle Multicatch */
    fun <R> Throwable.multicatch(vararg classes: KClass<*>, block: () -> R): R {
        if (classes.any { this::class.isSubclassOf(it) }) {
            return block()
        } else throw this
    }
}
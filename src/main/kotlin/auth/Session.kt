package mx.edu.uttt.auth
/*
import kotliquery.HikariCP
import org.eclipse.jetty.http.HttpCookie
import org.eclipse.jetty.server.session.*
import java.io.File

object Session {
    init {
        HikariCP.init("jetty", SVR_CONF.dbURL, SVR_CONF.dbUser, SVR_CONF.dbPwd)
    }

    fun fileSessionHandler() = SessionHandler().apply {
        sessionCache = NullSessionCache(this).apply {
            sessionDataStore = FileSessionDataStore().apply {
                //val baseDir = File(System.getProperty("java.io.tmpdir"))
                val baseDir = File("config")
                this.storeDir = File(baseDir, "javalin-session-store").apply { mkdir() }
            }
        }
        httpOnly = true
        isSecureRequestOnly = true
        sameSite = HttpCookie.SameSite.STRICT
    }

    fun sqlSessionHandler() = SessionHandler().apply {
        //sessionCache = NullSessionCache(this).apply {
        sessionCache = DefaultSessionCache(this).apply {
            sessionDataStore = JDBCSessionDataStoreFactory().apply {
                setDatabaseAdaptor(DatabaseAdaptor().apply {
                    datasource = HikariCP.dataSource("jetty")
                })
            }.getSessionDataStore(sessionHandler)
        }
        httpOnly = true
        isSecureRequestOnly = true
        sameSite = HttpCookie.SameSite.STRICT
    }
}*/
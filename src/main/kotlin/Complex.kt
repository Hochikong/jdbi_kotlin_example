package dao.jdbitest

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.extension.ExtensionCallback
import org.jdbi.v3.core.extension.ExtensionConsumer
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.customizer.BindFields
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlScript
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.jdbi.v3.sqlobject.transaction.Transactional
import javax.security.auth.callback.CallbackHandler

class Complex {
    val url = "jdbc:postgresql://192.168.142.130:5432/testdb"
    private val hikariConfig = HikariConfig().apply {
        jdbcUrl = url
        poolName = "CP-001"
        maximumPoolSize = 9
        username = "dbuser"
        password = "dbuserpass"
    }
    private val ds = HikariDataSource(hikariConfig)

    val jdbi = Jdbi.create(ds)

    init {
        jdbi.installPlugins()
    }
}

data class Car(var id: Long = -1, var name: String = "", var descript: String = "", var price: Int = 0)

interface CarDAO : Transactional<CarDAO>{
    @SqlUpdate(
        """
            CREATE TABLE IF NOT EXISTS car_catalog(
                id BIGSERIAL PRIMARY KEY ,
                name VARCHAR(200) NOT NULL ,
                descript VARCHAR(200) NOT NULL ,
                price INT NOT NULL 
            )
        """
    )
    fun create()

    @SqlUpdate("INSERT INTO car_catalog(name, descript, price) VALUES (:name, :descript, :price)")
    @GetGeneratedKeys("id")
    fun insert(@BindBean car: Car): Long

    @SqlQuery("SELECT id, name, descript, price FROM car_catalog")
    @RegisterBeanMapper(Car::class)
    fun query(): List<Car>

    @SqlUpdate("UPDATE car_catalog SET name = :c.name, descript = :c.descript, price = :c.price WHERE id = :id")
    @GetGeneratedKeys("id")
    fun update(@Bind("id") id: Long, @BindBean("c") car: Car): Long

    @SqlUpdate("DELETE FROM car_catalog WHERE id = :id")
    @GetGeneratedKeys("id")
    fun delete(@Bind("id") id: Long): Long

    @SqlScript("DROP TABLE car_catalog;")
    fun drop(): Any
}

fun main() {
    val x = Complex()
//    x.jdbi.useExtension(CarDAO::class.java, ExtensionConsumer {
//        it.create()
//        it.insert(Car(name = "A", descript = "DESC A", price = 21))
//        it.insert(Car(name = "B", descript = "DESC B", price = 23))
//    })

//    val cars_ = x.jdbi.withExtension(CarDAO::class.java, ExtensionCallback {
//        it.query()
//    })
//    println("All cars:")
//    cars_.forEach { println(it) }
//
//    val id = x.jdbi.withExtension(CarDAO::class.java, ExtensionCallback {
//        it.update(3, Car(name = "C new", descript = "DESC C new", price = 31))
//    })

//    val id = x.jdbi.withExtension(CarDAO::class.java, ExtensionCallback {
//        it.insert(Car(name = "C", descript = "DESC C", price = 2111))
//    })
//
//    println(id)


//    val t = x.jdbi.withExtension(CarDAO::class.java, ExtensionCallback {
//        it.delete(3L)
//    })
//
//    println(t)
//
//    val cars = x.jdbi.withExtension(CarDAO::class.java, ExtensionCallback {
//        it.query()
//    })
//    println("All cars:")
//    cars.forEach { println(it) }

    val unk = x.jdbi.withExtension(CarDAO::class.java, ExtensionCallback {
        it.drop()
    })
    print(unk)

    val dao = x.jdbi.onDemand(CarDAO::class.java)
}
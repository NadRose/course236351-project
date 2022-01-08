package rest_api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import rest_api.repository.model.TimedTransaction
import rest_api.repository.model.Transfer
import rest_api.repository.model.UTxO
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

@SpringBootApplication
class SpringBootBoilerplateApplication

fun main(args: Array<String>) {
	runApplication<SpringBootBoilerplateApplication>(*args)
}

%dw 2.0
import java!java::util::HashMap
import java!java::util::ArrayList

fun HashMap() = {
	map: HashMap::new()
}

fun ArrayList() = {
	list: ArrayList::new()
}

fun jsonToString(data : Object) : String = (
    if(isEmpty(data)) ""
    else write(data, "application/json") as String
)

fun xmlToString(data : Object) : String = (
    if(isEmpty(data)) ""
    else write(data, "application/xml") as String
)
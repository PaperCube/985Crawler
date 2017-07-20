package studio.papercube.crawler985

import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun main(args: Array<String>) {
    var length:Int = 0
    val result = University985Crawler().fetchAllPagesIndexes().apply { length = size }
            .withIndex()
            .flatMap { (index, universityCode)->
                (2000..2016).map{ year->
                    println("${index+1}/$length, $year")
                    DetailedPage.from(universityCode, year, EntityCodes.Provices.SHANDONG, EntityCodes.SubjectType.SCIENCE).resolveOrNull()
                }.filterNotNull()
            }

    println("Done.")
}


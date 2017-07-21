package studio.papercube.crawler985

import java.io.File
import java.io.FileWriter
import java.io.PrintStream
import java.io.PrintWriter

val targetFile985 = File("/Users/papercube/Documents/985.csv")
val targetFile211 = File("/Users/papercube/Documents/211.csv")
fun main(args: Array<String>) {
    writeToCSV(University985Crawler(), targetFile985)
    writeToCSV(University211Crawler(), targetFile211)
    println(targetFile985.absolutePath)
    println(targetFile211.absolutePath)
    println("Done.")
}

fun getAll(universityListCrawler: UniversityListCrawler, logOut: PrintStream? = null): List<DetailedPage.SpecialtiesScores> {
    var length: Int = 0
    val result = universityListCrawler.fetchAllPagesIndexes().apply { length = size }
            .withIndex()
            .flatMap { (index, universityCode) ->
                (2000..2016).map { year ->
                    logOut?.println("${index + 1}/$length, $year")
                    DetailedPage.from(universityCode, year, EntityCodes.Provices.SHANDONG, EntityCodes.SubjectType.SCIENCE).resolveOrNull()
                }.filterNotNull()
            }
    return result
}

fun writeToCSV(universityListCrawler: UniversityListCrawler, file: File) {
    targetFile985.parentFile.mkdirs()
    targetFile985.createNewFile()
    PrintWriter(FileWriter(file).buffered()).use { out ->
        getAll(universityListCrawler, System.out).forEach { score ->
            score.data.forEach { specialtyName, (year, max, avg, min, batch) ->
                out.println("${score.universityName},$year$specialtyName,$max,$avg,$min,$batch")
            }
        }
    }
}


package studio.papercube.crawler985

import java.io.File
import java.io.PrintStream
import java.io.PrintWriter
import java.nio.charset.Charset

//val executor: ExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 80) //W/C ratio, N(cpu) * (1+w/c)

fun writeToCSV(universityListCrawler: UniversityListCrawler, file: File, encoding:Charset = Charset.defaultCharset()) {
    file.parentFile.mkdirs()
    file.createNewFile()
    val writer = PrintWriter(file, encoding.name())
    ParallelScoreCollector(
            universityListCrawler,
            province = EntityCodes.Provices.SHANDONG,
            subjectType = EntityCodes.SubjectType.SCIENCE)
            .getAsStream()
            .map(ScoreCollectors.mapToCSV())
            .forEach{
                writer.println(it)
            }
    writer.close()
}

@Deprecated("Use Stream-styled instead", ReplaceWith("ScoreCollector().getAsStream()"))
fun getAll(universityListCrawler: UniversityListCrawler, logOut: PrintStream? = null): List<DetailedPage.SpecialtiesScores> {
    var length: Int = 0
    val result = universityListCrawler.fetchAllUniversityCodes().apply { length = size }
            .withIndex()
            .flatMap { (index, universityCode) ->
                (2000..2016).map { year ->
                    logOut?.println("${index + 1}/$length, $year")
                    DetailedPage.from(universityCode, year, EntityCodes.Provices.SHANDONG, EntityCodes.SubjectType.SCIENCE).resolveOrNull()
                }.filterNotNull()
            }
    return result
}

//fun getAllConcurrently(universityListCrawler: UniversityListCrawler, logOut: PrintStream? = null): List<DetailedPage.SpecialtiesScores> {
//
//}


//private class ConcurrentResolveImpl(val pageIndexes: List<Int>) {
//    var logOut: PrintStream? = null
//    var yearScanRange = 2001..2016
//
//    fun resolve(): List<DetailedPage.SpecialtiesScores> {
//        val length = pageIndexes.size
//        pageIndexes.withIndex()
//                .flatMap { (index, universityCode) ->
//                    yearScanRange.map { year ->
//                        logOut?.println("${ index + 1}/$length, $year")
//                        DetailedPage.from(universityCode, year, EntityCodes.Provices.SHANDONG, EntityCodes.SubjectType.SCIENCE)
//                    }
//                }
//    }
//}
package studio.papercube.crawler985

import studio.papercube.crawler985.DetailedPage.SpecialtiesScores
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Supplier
import java.util.stream.Collectors.toList
import java.util.stream.Stream

private val resolveExecutor: ExecutorService = Executors.newFixedThreadPool(
        Runtime.getRuntime().availableProcessors() * 80) {
    Thread("ResolveExecutorService").apply {
        isDaemon = true
    }
} //W/C ratio, N(cpu) * (1+w/c)

@Suppress("UNCHECKED_CAST")
fun <R> Stream<R?>.filterNotNull(): Stream<R> {
    return filter { it != null } as Stream<R>
}

abstract class ScoreCollector {
    abstract fun getAsStream(): Stream<SpecialtiesScores>
}

open class ParallelScoreCollector(private val universityListCrawler: UniversityListCrawler,
                                  private val years: IntRange = 2001..2016,
                                  private val province: Int,
                                  private val subjectType: Int) : ScoreCollector() {
    override fun getAsStream(): Stream<SpecialtiesScores> {
        return PagesIndexingImpl(universityListCrawler, years)
                .getPagesBuilder()
                .stream()
                .map { builder -> builder(province, subjectType) }
                .map { detailedPage: DetailedPage ->
                    CompletableFuture.supplyAsync(Supplier { detailedPage.resolveOrNull() }, resolveExecutor)
                }
                .collect(toList())
                .stream()
                .map { it.get() }
                .filterNotNull()
    }
}

private class PagesIndexingImpl(private val universityListCrawler: UniversityListCrawler,
                                private val years: IntRange = 2001..2016) {
    /**
     * @return (Int provinceCode, Int subjectType)->DetailedPage
     */
    fun getPagesBuilder(): List<(Int, Int) -> DetailedPage> {
        return universityListCrawler.fetchAllUniversityCodes()
                .flatMap { universityCode ->
                    years.map { year ->
                        { provinceCode: Int, subjectType: Int ->
                            DetailedPage.from(universityCode, year, provinceCode, subjectType)
                        }
                    }
                }
                .toList()
    }
}

object ScoreCollectors {
    @JvmStatic
    fun mapToCSV() = { specialtyScore: SpecialtiesScores ->
        StringBuilder().also { b: StringBuilder ->
            specialtyScore.data.entries.joinToString {
                val specialtyName = it.key
                val (year, max, avg, min, batch) = it.value
                "${specialtyScore.universityName},$year,$specialtyName,$max,$avg,$min,$batch"
            }
        }.toString()
    }
}
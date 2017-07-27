package studio.papercube.crawler985

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

open class DetailedPage protected constructor() {
    companion object {
        @JvmStatic fun from(universityCode: Int, year: Int, provinceCode: Int, subjectType: Int) = DetailedPage().apply {
            address = "http://gkcx.eol.cn/schoolhtm/specialty/$universityCode/$subjectType/specialtyScoreDetail_${year}_$provinceCode.htm"
        }
    }

    lateinit var address: String private set

    fun resolve(): SpecialtiesScores = Jsoup.connect(address)
            .get()
            .let { SpecialtyScoresDocumentResolver.resolve(it) }

    fun resolveOrNull(): SpecialtiesScores? = try {
        resolve()
    } catch (e: Exception) {
        null
    }

    class SpecialtyScoresDocumentResolver {
        companion object {
            @JvmStatic fun resolve(document: Document): SpecialtiesScores {
                val name = document.select("title")
                        .takeIf { it.size > 0 }
                        ?.get(0)
                        ?.text()
                        ?.substringBefore("学校专业分数线")

                val head: List<String> = document
                        .select("table")[0]
                        .select("thead")[0]
                        .select("tr")
                        .select("th")
                        .map { it.text() }

                val detailedDataList = document.select("table")[0]
                        .select("tbody")
                        .select("tr")
                        .map {
                            it.select("td")
                                    .map { it.text() }
                        }

                val map: MutableMap<String, SpecialtiesScores.SingleSpecialtyScores> = HashMap()

                for (detailedData in detailedDataList) {
                    map.put(detailedData[0], SpecialtiesScores.SingleSpecialtyScores(
                            detailedData[1].toIntOrNull() ?: -1,
                            detailedData[2].toIntOrNull() ?: -1,
                            detailedData[3].toIntOrNull() ?: -1,
                            detailedData[4].toIntOrNull() ?: -1,
                            detailedData[5]
                    ))
                }

                return SpecialtiesScores(name, head, map)
            }
        }
    }

    /**
     * @param data 一个Map，键是专业名称，值是这个专业的分数
     */
    class SpecialtiesScores(
            val universityName: String?,
            val head: List<String>,
            val data: Map<String, SingleSpecialtyScores>
    ) {
        data class SingleSpecialtyScores(val year: Int, val max: Int, val avg: Int, val min: Int, val batch: String)
    }
}
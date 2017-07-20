package studio.papercube.crawler985

import okhttp3.Request

open class UniversityListCrawler(private val address:String){
    fun fetchAllPagesAdresses():List<String>{
        val responseString = Request.Builder()
                .url(address)
                .get()
                .build()
                .let { sharedOkHttpClient.newCall(it).execute() }
                .body()
                ?.string() ?: throw UnknownResultException()

        var searchIndex = 0
        val fetchedUrls = ArrayList<String>()
        while (true) {
            val index = responseString.indexOf("http://gkcx.eol.cn/schoolhtm/schoolSpecailtyMark/", searchIndex, true)
            if (index == -1) break
            searchIndex = index + 1
            val substring = responseString.substring(index,responseString.indexOf(".htm", searchIndex, true) + 4)
            fetchedUrls.add(substring)
        }

        return fetchedUrls
    }

    fun fetchAllPagesIndexes():List<Int>{
        return fetchAllPagesAdresses().map {
            it.replace("http://gkcx.eol.cn/schoolhtm/schoolSpecailtyMark/(.*)/schoolSpecailtyMark.htm".toRegex(),"$1").toIntOrNull()
        }.filterNotNull()
    }
}

open class University985Crawler:UniversityListCrawler(indexPageOf985UrlString)
open class University211Crawler:UniversityListCrawler(indexPageOf211UrlString)
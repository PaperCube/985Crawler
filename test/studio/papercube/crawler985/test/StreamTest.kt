package studio.papercube.crawler985.test

import org.testng.annotations.Test
import java.util.*
import java.util.stream.Collectors.toList
import kotlin.collections.ArrayList
import kotlin.test.assertEquals

class StreamTest {
    @Test
    fun streamOrderTest() {
        val result:MutableList<Int> = ArrayList()
        val random = (1 until 100).toMutableList().apply { Collections.shuffle(this) }
        random.stream()
                .map { v ->
                    {
                        Thread.sleep(Random().nextInt(100).toLong())
                        println("$v done.")
                        v
                    }
                }
                .parallel()
                .unordered()
                .map { it() }
                .forEach { result.add(it) }

        assertEquals(result, random)
        println(Arrays.toString(result.toTypedArray()))

    }
}
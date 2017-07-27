package studio.papercube.crawler985

import java.io.File
import java.io.FileWriter
import java.io.PrintStream
import java.io.PrintWriter

val targetFile985 = File("$userHome/Documents/985.csv")
val targetFile211 = File("$userHome/Documents/211.csv")
fun main(args: Array<String>) {
    writeToCSV(University985Crawler(), targetFile985)
//    writeToCSV(University211Crawler(), targetFile211)
    println(targetFile985.absolutePath)
//    println(targetFile211.absolutePath)
    println("Done.")
}



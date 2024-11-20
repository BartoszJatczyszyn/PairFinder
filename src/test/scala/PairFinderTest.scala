import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfter
import java.io.File
import java.io.PrintWriter
import scala.util.Using // For safely handling file reading and writing

// Test suite for PairFinder
class PairFinderTest extends AnyFunSuite with BeforeAndAfter {

  // File paths for input and output test files
  val inputFile = "src/test/resources/test_input.txt"
  val outputFile = "src/test/resources/test_output.txt"

  // Set up before each test: ensure the input file directory exists
  before {
    new File(inputFile).getParentFile.mkdirs()
  }

  // Clean up after each test: delete test files
  after {
    new File(inputFile).delete()
    new File(outputFile).delete()
  }

  // Helper method to write content to the input file
  def setupInputFile(content: String): Unit = {
    Using(new PrintWriter(inputFile)) { writer =>
      writer.write(content)
    }.getOrElse {
      fail(s"Failed to create the input file: $inputFile")
    }
  }

  // Helper method to read and return the content of the output file
  def readOutputFile(): List[String] = {
    Using(scala.io.Source.fromFile(outputFile)) { source =>
      source.getLines().toList
    }.getOrElse {
      fail(s"Failed to read the output file: $outputFile")
    }
  }

  // Helper method to normalize pairs by removing brackets and sorting
  def normalizePairs(pairs: List[String]): Set[Set[Int]] = {
    pairs.map { line =>
      val pair = line
        .replaceAll("[\\[\\]]", "") // Remove square brackets
        .split(",") // Split by comma
        .map(_.trim.toInt) // Convert to integers
        .toSet // Convert pair to a set
      pair
    }.toSet // Convert the list of pairs to a set of sets
  }

  // Test for finding all pairs that sum to 12, ignoring order and duplicates
  test("finds all pairs that sum to 12, ignoring pair order and duplicates") {
    setupInputFile("4, 8, 9, 0, 12, 1, 4, 2, 12, 12, 4, 4, 8, 11, 12, 0")

    val pairFinder = new PairFinder(inputFile, outputFile)
    pairFinder.findPairs()

    val output = readOutputFile()
    val expected = List("[4, 8]", "[0, 12]", "[4, 8]", "[1, 11]", "[0, 12]")

    assert(
      normalizePairs(output) == normalizePairs(expected),
      s"Expected pairs: ${normalizePairs(expected)}, but got: ${normalizePairs(output)}"
    )
  }

  // Test for an empty input file (should produce empty output)
  test("handles empty input file") {
    setupInputFile("")

    val pairFinder = new PairFinder(inputFile, outputFile)
    pairFinder.findPairs()

    val output = readOutputFile()
    assert(output.isEmpty, "Output file should be empty for an empty input list")
  }

  // Test when no pairs sum to 12
  test("returns empty output when no pairs are possible") {
    setupInputFile("1, 2, 3, 4, 5")

    val pairFinder = new PairFinder(inputFile, outputFile)
    pairFinder.findPairs()

    val output = readOutputFile()
    assert(output.isEmpty, "No pairs should result in an empty output")
  }

  // Test when there is only one number in the file (no pairs possible)
  test("handles single number in the file") {
    setupInputFile("6")

    val pairFinder = new PairFinder(inputFile, outputFile)
    pairFinder.findPairs()

    val output = readOutputFile()
    assert(output.isEmpty, "A single number cannot form a pair, so output should be empty")
  }

  // Test when the file contains only invalid data
  test("handles file with only invalid data") {
    setupInputFile("abc, xyz, !@#")

    val pairFinder = new PairFinder(inputFile, outputFile)
    pairFinder.findPairs()

    val output = readOutputFile()
    assert(output.isEmpty, "File with only invalid data should produce no output")
  }

  // Test when numbers are separated by spaces or newlines
  test("handles numbers separated by spaces or newlines") {
    setupInputFile("4 8\n9 0\n12\n1\n4 2 12 12 11 1")

    val pairFinder = new PairFinder(inputFile, outputFile)
    pairFinder.findPairs()

    val output = readOutputFile()
    val expected = List("[4, 8]", "[0, 12]", "[1, 11]")

    assert(
      normalizePairs(output) == normalizePairs(expected),
      s"Expected pairs: ${normalizePairs(expected)}, but got: ${normalizePairs(output)}"
    )
  }

  // Performance test on large datasets
  test("measures performance on large datasets") {
    val largeInput = (1 to 1000000).map(_ => 6).mkString(", ")
    setupInputFile(largeInput)

    val pairFinder = new PairFinder(inputFile, outputFile)

    val startTime = System.nanoTime()
    pairFinder.findPairs()
    val endTime = System.nanoTime()

    val elapsedTime = (endTime - startTime) / 1e9 // Convert to seconds
    val output = readOutputFile()

    assert(output.size == 500000, s"Expected 500,000 pairs, but got ${output.size}")
    assert(elapsedTime < 1, s"Performance issue: took $elapsedTime seconds for large dataset")
  }

  // Test for handling negative numbers and complements correctly
  test("handles negative numbers and complements correctly") {
    setupInputFile("-6, 18, -12, 24, 0, -6, 12")

    val pairFinder = new PairFinder(inputFile, outputFile)
    pairFinder.findPairs()

    val output = readOutputFile()
    val expected = List("[0, 12]")

    assert(
      normalizePairs(output) == normalizePairs(expected),
      s"Expected pairs: ${normalizePairs(expected)}, but got: ${normalizePairs(output)}"
    )
  }

  // Test for handling repeated numbers forming multiple pairs
  test("handles repeated numbers forming multiple pairs") {
    setupInputFile("4, 8, 4, 8, 0, 12, 0, 12")

    val pairFinder = new PairFinder(inputFile, outputFile)
    pairFinder.findPairs()

    val output = readOutputFile()
    val expected = List("[4, 8]", "[4, 8]", "[0, 12]", "[0, 12]")

    assert(
      normalizePairs(output) == normalizePairs(expected),
      s"Expected pairs: ${normalizePairs(expected)}, but got: ${normalizePairs(output)}"
    )
  }

  // Test to check if invalid data is skipped while processing valid numbers
  test("correctly skips invalid data while processing valid numbers") {
    setupInputFile("4, 8, abc, 12, 0")

    val pairFinder = new PairFinder(inputFile, outputFile)
    pairFinder.findPairs()

    val output = readOutputFile()
    val expected = List("[4, 8]", "[0, 12]")

    assert(
      normalizePairs(output) == normalizePairs(expected),
      s"Expected pairs: ${normalizePairs(expected)}, but got: ${normalizePairs(output)}"
    )
  }
}
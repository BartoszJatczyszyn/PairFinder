import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfter
import java.io._
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
}
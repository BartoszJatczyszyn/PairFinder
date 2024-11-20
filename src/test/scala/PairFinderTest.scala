import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfter
import java.io._

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
}
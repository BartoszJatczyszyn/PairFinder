import scala.io.Source
import scala.util.Using // For safely handling file reading and writing
import java.io._

// Class to find pairs from an input file and write them to an output file
class PairFinder(inputFile: String, outputFile: String) {

  // Main method to find pairs and write to output file
  def findPairs(): Unit = {
    val numbers = readNumbers() // Read numbers from input file
    val pairs = findPairsWithSum(numbers, 12) // Find pairs that sum to 12
    writePairs(pairs) // Write pairs to output file
  }

  // Reads numbers from the input file, filters out negatives, and returns them as a list
  private def readNumbers(): List[Int] = {
    Using(Source.fromFile(inputFile)) { source =>
      source.getLines()
        .flatMap(_.split("[,\\s]+")) // Split by commas, spaces, or newlines
        .flatMap { token =>
          try {
            val number = token.trim.toInt
            if (number >= 0) Some(number) // Only include non-negative numbers
            else {
              println(s"Warning: Skipping negative number '$number' in the input file.")
              None
            }
          } catch {
            case _: NumberFormatException =>
              println(s"Warning: Skipping invalid entry '$token' in the input file.")
              None
          }
        }
        .toList
    }.getOrElse {
      println(s"Error reading from file $inputFile")
      List() // Return empty list on error
    }
  }

  // Finds all unique pairs that sum to a target value (12 in this case)
  private def findPairsWithSum(numbers: List[Int], targetSum: Int): List[(Int, Int)] = {
    val countMap = scala.collection.mutable.Map[Int, Int]()
    numbers.foreach(num => countMap(num) = countMap.getOrElse(num, 0) + 1) // Count occurrences

    val result = scala.collection.mutable.ListBuffer[(Int, Int)]()

    for (num <- numbers) {
      val complement = targetSum - num

      // Check if both number and its complement are available for pairing
      if (countMap.getOrElse(num, 0) > 0 && countMap.getOrElse(complement, 0) > 0) {
        // Ensure valid pairing for distinct or the same number used twice
        if (num != complement || countMap(num) > 1) {
          // Add pair (sorted for consistency)
          result.append((Math.min(num, complement), Math.max(num, complement)))
          countMap(num) -= 1
          countMap(complement) -= 1
        }
      }
    }

    result.toList
  }

  // Writes the found pairs to the output file
  private def writePairs(pairs: List[(Int, Int)]): Unit = {
    Using(new PrintWriter(new File(outputFile))) { writer =>
      pairs.foreach { case (a, b) => writer.println(s"[$a, $b]") } // Format pair as [a, b]
    }.recover {
      case ex: Exception => println(s"Error while writing to file: ${ex.getMessage}")
    }
  }
}

// Main object to run the program
object Main extends App {
  private val pairFinder = new PairFinder("src/main/resources/input.txt", "src/main/resources/output.txt")
  pairFinder.findPairs() // Run the pair finding process
}

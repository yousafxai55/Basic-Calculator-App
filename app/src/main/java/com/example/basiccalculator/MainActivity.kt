package com.example.basiccalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.basiccalculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var canAddOperation = false
    private var canAddDecimal = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("MainActivity", "Calculator App Started")
    }

    fun numberAction(view: View) {
        if (view is Button) {
            val buttonText = view.text.toString()
            Log.d("MainActivity", "Number Button Pressed: $buttonText")

            if (buttonText == ".") {
                if (canAddDecimal) {
                    binding.workingsTv.append(buttonText)
                    canAddDecimal = false
                    Log.d("MainActivity", "Decimal Added")
                }
            } else {
                binding.workingsTv.append(buttonText)
            }
            canAddOperation = true
            Log.d("MainActivity", "Current Input: ${binding.workingsTv.text}")
        }
    }

    fun operationAction(view: View) {
        if (view is Button && canAddOperation) {
            binding.workingsTv.append(view.text)
            canAddOperation = false
            canAddDecimal = true
            Log.d("MainActivity", "Operation Added: ${view.text}")
            Log.d("MainActivity", "Current Input: ${binding.workingsTv.text}")
        }
    }

    fun allClearAction(view: View) {
        binding.workingsTv.text = ""
        binding.resultTv.text = ""
        canAddOperation = false
        canAddDecimal = true
        Log.d("MainActivity", "All Cleared")
    }

    fun backSpaceAction(view: View) {
        val length = binding.workingsTv.length()
        if (length > 0) {
            binding.workingsTv.text = binding.workingsTv.text.subSequence(0, length - 1)
            Log.d("MainActivity", "Backspace Pressed, Current Input: ${binding.workingsTv.text}")
        }
    }

    fun equalsAction(view: View) {
        Log.d("MainActivity", "Equals Button Pressed")
        binding.resultTv.text = calculateResult()
        Log.d("MainActivity", "Result: ${binding.resultTv.text}")
    }

    private fun calculateResult(): String {
        return try {

            // identify number and operators

            val digitsOperators = digitsOperators()
            Log.d("MainActivity", "Parsed Digits and Operators: $digitsOperators")

            if (digitsOperators.isEmpty()) return ""

            val timesDivision = timesDivisionCalculate(digitsOperators)
            Log.d("MainActivity", "After Multiplication/Division: $timesDivision")

            if (timesDivision.isEmpty()) return ""

            val result = addSubtractCalculate(timesDivision)
            Log.d("MainActivity", "Final Result: $result")

            result.toString()

        } catch (e: ArithmeticException) {
            Log.e("MainActivity", "Error: Division by Zero", e)
            "Error: Division by zero"
        } catch (e: NumberFormatException) {
            Log.e("MainActivity", "Error: Invalid Number Format", e)
            "Error: Invalid number"
        } catch (e: Exception) {
            Log.e("MainActivity", "Unexpected Error", e)
            "Error"
        }
    }

    private fun digitsOperators(): MutableList<Any> {
        val list = mutableListOf<Any>() // store number and operator
        var currentDigit = ""

        //input text loop
        for (character in binding.workingsTv.text) {
            if (character.isDigit() || character == '.') {
                currentDigit += character
            } else {
                if (currentDigit.isNotEmpty()) {
                    val number = currentDigit.toFloatOrNull() ?: throw NumberFormatException("Invalid number format")
                    list.add(number)
                    currentDigit = ""
                    Log.d("MainActivity", "Added Number: $number")
                }
                list.add(character.toString()) // Store operator as string
                Log.d("MainActivity", "Added Operator: $character")
            }
        }

        if (currentDigit.isNotEmpty()) {
            val number = currentDigit.toFloatOrNull() ?: throw NumberFormatException("Invalid number format")
            list.add(number)
            Log.d("MainActivity", "Added Last Number: $number")
        }

        return list
    }

    // if there is any of these operators in the list, we will keep doing the calculations.
    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains("x") || list.contains("/")) {
            list = calcTimesDiv(list)
            Log.d("MainActivity", "List After One Pass of Multiplication/Division: $list")
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size //variable track

        for (i in passedList.indices) {
            if (passedList[i] is String && i != passedList.lastIndex && i < restartIndex) {
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float

                when (operator) {
                    "x" -> {
                        newList.add(prevDigit * nextDigit)
                        restartIndex = i + 1 //calculation next step
                        Log.d("MainActivity", "Multiplication: $prevDigit * $nextDigit = ${prevDigit * nextDigit}")
                    }
                    "/" -> {
                        // Handle division by zero
                        if (nextDigit == 0f) {
                            throw ArithmeticException("Division by zero")
                        }
                        newList.add(prevDigit / nextDigit)
                        restartIndex = i + 1
                        Log.d("MainActivity", "Division: $prevDigit / $nextDigit = ${prevDigit / nextDigit}")
                    }
                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            } else if (i > restartIndex) {
                newList.add(passedList[i])
            }
        }

        return newList
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in passedList.indices) {
            if (passedList[i] is String && i != passedList.lastIndex) {
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float

                when (operator) {
                    "+" -> {
                        result += nextDigit
                        Log.d("MainActivity", "Addition: $result + $nextDigit = $result")
                    }
                    "-" -> {
                        result -= nextDigit
                        Log.d("MainActivity", "Subtraction: $result - $nextDigit = $result")
                    }
                }
            }
        }

        return result
    }
}




// the library is used in this code


//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityMainBinding
//
//    // Expression variables to store inputs
//    private var workings = ""
//    private var result = ""
//
//    // To control operations and decimal logic
//    private var canAddOperation = false
//    private var canAddDecimal = true
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Initialize display with empty values
//        binding.workingsTv.text = workings
//        binding.resultTv.text = result
//
//        Log.d("MainActivity", "Calculator initialized")
//    }
//
//    // Number button logic
//    fun numberAction(view: View) {
//        if (view is Button) {
//            Log.d("MainActivity", "Number pressed: ${view.text}")
//
//            // Append number to workings
//            workings += view.text
//            binding.workingsTv.text = workings
//
//            // Allow adding operations after a number
//            canAddOperation = true
//            Log.d("MainActivity", "Updated workings: $workings")
//        }
//    }
//
//    // Operation button logic (+, -, *, /)
//    fun operationAction(view: View) {
//        if (view is Button && canAddOperation) {
//            Log.d("MainActivity", "Operation pressed: ${view.text}")
//
//            // Ensure no double operation (e.g., 5++5)
//            workings += view.text
//            binding.workingsTv.text = workings
//
//            // After an operation, disable adding another until a number is added
//            canAddOperation = false
//            canAddDecimal = true
//            Log.d("MainActivity", "Updated workings after operation: $workings")
//        }
//    }
//
//    // Equals button logic (Calculate result)
//    fun equalsAction(view: View) {
//        try {
//            Log.d("MainActivity", "Equals pressed")
//
//            // If workings is valid, calculate the result
//            result = calculateResult()
//            binding.resultTv.text = result
//
//            Log.d("MainActivity", "Result: $result")
//
//        } catch (e: Exception) {
//            Log.e("MainActivity", "Error calculating result: ${e.message}")
//            binding.resultTv.text = "Error"
//        }
//    }
//
//    // Clear all logic
//    fun allClearAction(view: View) {
//        Log.d("MainActivity", "All clear pressed")
//
//        workings = ""
//        result = ""
//        binding.workingsTv.text = workings
//        binding.resultTv.text = result
//
//        Log.d("MainActivity", "Cleared workings and result")
//    }
//
//    // Backspace logic (remove last character)
//    fun backSpaceAction(view: View) {
//        if (workings.isNotEmpty()) {
//            Log.d("MainActivity", "Backspace pressed")
//
//            workings = workings.dropLast(1)
//            binding.workingsTv.text = workings
//
//            Log.d("MainActivity", "Updated workings after backspace: $workings")
//        }
//    }
//
//    // Helper function to calculate result (using eval-like logic)
//    private fun calculateResult(): String {
//        try {
//            // Convert the expression string into a valid expression and evaluate
//            val expression = ExpressionBuilder(workings).build()
//
//            Log.d("MainActivity", "Evaluating expression: $workings")
//            val result = expression.evaluate().toString()
//
//            Log.d("MainActivity", "Evaluation result: $result")
//            return result
//
//        } catch (e: Exception) {
//            Log.e("MainActivity", "Error evaluating expression: ${e.message}")
//            return "Error"
//        }
//    }
//}


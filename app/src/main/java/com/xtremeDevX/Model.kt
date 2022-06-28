package com.xtremeDevX

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

import org.mariuszgromada.math.mxparser.Expression
import org.mariuszgromada.math.mxparser.mXparser
import java.lang.NumberFormatException

class Model : ViewModel() {
    private val mEquationList = mutableListOf<String>("0")
    private val _equationList = MutableListLiveData(mEquationList)
    val stringExp: LiveData<String> =
        Transformations.map(_equationList) { it.joinToString(separator = " ") }

    private val _result = MutableLiveData("0")
    val result: LiveData<String> = _result

    private val _isInverse = MutableLiveData(false)
    val isInverse: LiveData<Boolean> = _isInverse

    private var isResultNaN = false
    private var isResultHasDecimal = false

    private val _isRadian = MutableLiveData(mXparser.checkIfRadiansMode())
    val isRadian: LiveData<Boolean> = _isRadian

    private val _equalsPressed = MutableLiveData(false)
    val equalsPressed: LiveData<Boolean> = _equalsPressed

    private var lastPressed = DIGIT
    private var lastDigit = "0"
    private var isDecimalUsed = false

    private val operator = listOf("+", "-", "×", "÷")
    private val scientificFunction =
        listOf("sqrt(", "log(", "ln()", "sin(", "cos(", "tan(", "sin⁻¹(", "cos⁻¹(", "tan⁻¹(")
    private val scientificOperator = listOf("!", "%")
    private val mathematicsConstant = listOf("π", "e")

    //Digit 0 1 2 3 4 5 6 7 8 9
    fun addDigit(str: String) {
        when (lastPressed) {
            DIGIT, DECIMAL -> {
                lastDigit = if (lastDigit == "0") str else {
                    lastDigit + str
                }
                _equationList.removeLast()
            }
            OPERATOR, SCIENTIFIC_FUNCTION, PARENTHESES_OPEN, POWER_FUNCTION -> lastDigit = str
            PARENTHESES_CLOSE, SCIENTIFIC_OPERATOR, MATHEMATICS_CONSTANT -> {
                _equationList.add("×")
                lastDigit = str
            }
            EQUAL -> {
                if (isResultNaN) {
                    lastDigit + str
                } else {
                    _equationList.clear()
                    lastDigit = "${_result.value}$str"
                }
                _equalsPressed.value = false
            }
        }
        _equationList.add(lastDigit)
        lastPressed = DIGIT
    }

    //Operator +   -    ×    ÷
    fun addOperator(str: String) {
        when (lastPressed) {
            OPERATOR -> _equationList.removeLast()
            DECIMAL -> {
                _equationList.removeLast()
                _equationList.add(lastDigit + 0)
            }
            SCIENTIFIC_FUNCTION, PARENTHESES_OPEN, POWER_FUNCTION ->
                _equationList.add("0")
            EQUAL -> {
                if (!isResultNaN) {
                    _equationList.clear()
                    _equationList.add("${_result.value}")
                }
                _equalsPressed.value = false
            }
        }
        _equationList.add(str)

        lastPressed = OPERATOR
        lastDigit = ""
    }

    //Scientific operator   !   %
    fun addScientificOperator(str: String) {
        when (lastPressed) {
            DECIMAL -> {
                _equationList.removeLast()
                _equationList.add(lastDigit + 0)
            }
            SCIENTIFIC_FUNCTION, PARENTHESES_OPEN, OPERATOR, POWER_FUNCTION -> _equationList.add("0")
            MATHEMATICS_CONSTANT -> {
                _equationList.add("+")
                _equationList.add("0")
            }
            SCIENTIFIC_OPERATOR -> _equationList.removeLast()
            EQUAL -> {
                if (!isResultNaN) {
                    _equationList.clear()
                    _equationList.add("${_result.value}")
                }
                _equalsPressed.value = false
            }
        }
        _equationList.add(str)
        lastPressed = SCIENTIFIC_OPERATOR
        lastDigit = ""
    }

    //Scientific function  sqrt(   log(  ln(
    fun addScientificFunction(str: String) {
        when (lastPressed) {
            DIGIT -> {
                if (_equationList.size == 1 && _equationList.last() == "0") _equationList.removeLast() else _equationList.add(
                    "×"
                )
            }
            MATHEMATICS_CONSTANT, PARENTHESES_CLOSE, SCIENTIFIC_OPERATOR -> {
                _equationList.add("×")
            }
            DECIMAL -> {
                _equationList.removeLast()
                _equationList.add(lastDigit + 0)
                _equationList.add("×")
            }
            EQUAL -> {
                if (!isResultNaN) {
                    _equationList.clear()
                    _equationList.add("${_result.value}")
                    _equationList.add("×")
                }
                _equalsPressed.value = false
            }
        }
        _equationList.add(str)
        lastPressed = SCIENTIFIC_FUNCTION
        lastDigit = ""
    }

    fun addNaturalLogExponential() {
        addScientificFunction(if (isInverse.value == true) "exp(" else "ln(")
    }

    //TrigonometryFunction sin(    con(    tan(    sin⁻¹(    cos⁻¹(    tan⁻¹(
    fun addTrigonometryFunction(str: String) {
        if (isInverse.value == true) {
            when (str) {
                "sin(" -> addScientificFunction("sin⁻¹(")
                "cos(" -> addScientificFunction("cos⁻¹(")
                "tan(" -> addScientificFunction("tan⁻¹(")
            }
        } else addScientificFunction(str)
    }

    fun addDecimal() {
        when (lastPressed) {
            DIGIT -> {
                if (isDecimalUsed) return else {
                    lastDigit = "$lastDigit."
                    _equationList.removeLast()
                }
            }
            OPERATOR, SCIENTIFIC_FUNCTION, PARENTHESES_OPEN, POWER_FUNCTION -> {
                lastDigit = "0."
            }
            PARENTHESES_CLOSE, MATHEMATICS_CONSTANT, SCIENTIFIC_OPERATOR -> {
                _equationList.add("×")
                lastDigit = "0."
            }
            EQUAL -> {
                if (!isResultNaN) {
                    _equationList.clear()

                    if (isResultHasDecimal) {
                        _equationList.add("${_result.value}")
                        resetAllFlags()
                        return
                    } else {
                        lastDigit = "${_result.value}."
                    }

                }
                _equalsPressed.value = false
            }
            DECIMAL -> {
                return
            }
        }
        _equationList.add(lastDigit)
        lastPressed = DECIMAL
        isDecimalUsed = true
    }

    fun backspace() {
        if (lastPressed == EQUAL && !isResultNaN) {
            _equationList.clear()
            _equationList.add("${_result.value}")
        }

        if (isNumber(_equationList.last())) {
            val str = _equationList.last()
            when {
                str.length > 1 -> {
                    _equationList.removeLast()
                    _equationList.add(str.dropLast(1))
                }
                _equationList.size == 1 -> {
                    _equationList.removeLast()
                    _equationList.add("0")
                }
                else -> _equationList.removeLast()
            }
        } else {
            _equationList.removeLast()
            if (_equationList.isEmpty()) _equationList.add("0")
        }
        resetAllFlags()
    }

    private fun resetAllFlags() {
        val string = _equationList.last()
        _equalsPressed.value = false
        if (isNumber(string)) {
            lastDigit = string
            isDecimalUsed = !isInteger(string)
            lastPressed = if (string.last() == '.') DECIMAL else DIGIT
        } else {
            lastDigit = ""
            lastPressed = when (string) {
                in operator -> OPERATOR
                in scientificOperator -> SCIENTIFIC_OPERATOR
                in scientificFunction -> SCIENTIFIC_FUNCTION
                in mathematicsConstant -> MATHEMATICS_CONSTANT
                "(" -> PARENTHESES_OPEN
                ")" -> PARENTHESES_CLOSE
                else -> POWER_FUNCTION
            }
        }
    }

    fun addOpenParentheses() {
        addScientificFunction("(")
        lastPressed = PARENTHESES_OPEN
    }

    fun addCloseParentheses() {
        when (lastPressed) {
            OPERATOR -> _equationList.removeLast()
            DECIMAL -> {
                _equationList.removeLast()
                _equationList.add(lastDigit + 0)
            }
            EQUAL -> {
                if (!isResultNaN) {
                    _equationList.clear()
                    _equationList.add("${_result.value}")
                }
            }
            POWER_FUNCTION -> _equationList.add("0")
        }
        _equationList.add(")")
        lastPressed = PARENTHESES_CLOSE
        lastDigit = ""
    }


    fun addMathematicsConstant(str: String) {
        when (lastPressed) {
            DIGIT -> {
                if (_equationList.size == 1 && _equationList.last() == "0") _equationList.removeLast() else _equationList.add(
                    "×"
                )
            }
            SCIENTIFIC_OPERATOR, PARENTHESES_CLOSE -> _equationList.add("×")
            DECIMAL -> {
                _equationList.removeLast()
                _equationList.add(lastDigit + 0)
                _equationList.add("×")
            }
            EQUAL -> {
                if (isResultNaN) {
                    _equationList.clear()
                    _equationList.add("${_result.value}")
                    _equationList.add("×")
                }
            }
        }
        _equationList.add(str)
        lastPressed = MATHEMATICS_CONSTANT
        lastDigit = ""
    }

    fun addPowerFunction() {
        when (lastPressed) {
            DECIMAL -> {
                _equationList.removeLast()
                _equationList.add(lastDigit + 0)
            }
            OPERATOR, SCIENTIFIC_FUNCTION, PARENTHESES_OPEN -> _equationList.add("0")
            SCIENTIFIC_OPERATOR -> {
                _equationList.add("+")
                _equationList.add("0")
            }
            EQUAL -> {
                if (isResultNaN) {
                    _equationList.clear()
                    _equationList.add("${_result.value}")
                }
            }
        }
        _equationList.add("^(")
        lastPressed = POWER_FUNCTION
        lastDigit = ""
    }


    fun clearAll() {
        _equationList.clear()
        _equationList.add("0")

        resetAllFlags()
    }

    fun calculate() {
        var mExpression: String = _equationList.value!!.joinToString(separator = "")
        mExpression = mExpression.replace("÷".toRegex(), "/")
        mExpression = mExpression.replace("×".toRegex(), "*")
        mExpression = mExpression.replace("π".toRegex(), "pi")
        mExpression = mExpression.replace("√".toRegex(), "sqrt")
        mExpression = mExpression.replace("log".toRegex(), "log10")
        val exp = Expression(mExpression)
        val result = exp.calculate()

        if (!result.isNaN()) {
            val longResult = result.toLong()
            if (result == longResult.toDouble()) {
                _result.value = longResult.toString()
                isResultHasDecimal = false
            } else {
                _result.value = result.toString()
                isResultHasDecimal = true
            }
            isResultNaN = false
        } else {
            isResultNaN = true
            _result.value = "NaN"
        }
        lastPressed = EQUAL
        _equalsPressed.value = true

    }

    fun radianAndDegree() {
        if (_isRadian.value == true) {
            mXparser.setDegreesMode()
            _isRadian.value = false
        } else {
            mXparser.setRadiansMode()
            _isRadian.value = true
        }
    }

    fun inverse() {
        _isInverse.value = _isInverse.value != true
    }

    private fun isNumber(str: String): Boolean {
        return try {
            str.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun isInteger(str: String): Boolean {
        return try {
            str.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }


    companion object {
        const val DIGIT = 0
        const val OPERATOR = 1
        const val SCIENTIFIC_OPERATOR = 2
        const val SCIENTIFIC_FUNCTION = 3
        const val DECIMAL = 4
        const val PARENTHESES_CLOSE = 5
        const val PARENTHESES_OPEN = 6
        const val MATHEMATICS_CONSTANT = 7
        const val POWER_FUNCTION = 8
        const val EQUAL = 9

    }

}
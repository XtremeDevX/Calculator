package com.xtremeDevX.calculator

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.xtremeDevX.Model
import com.xtremeDevX.calculator.databinding.FragmentCalculatorBinding
import android.widget.ScrollView

class CalculatorFragment : Fragment(R.layout.fragment_calculator) {

    private lateinit var binding: FragmentCalculatorBinding

    lateinit var model: Model

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCalculatorBinding.bind(view)
        model = ViewModelProvider(this)[Model::class.java]

        model.result.observe(this) {
            binding.tvResult.text = it
        }

        model.equalsPressed.observe(this) { equalPressed ->
            binding.tvResult.visibility = if (equalPressed) View.VISIBLE else View.INVISIBLE
        }
        model.stringExp.observe(this) {
            binding.tvExpression.text = it
            binding.svExpression.fullScroll(ScrollView.FOCUS_RIGHT)
        }

        model.isInverse.observe(this) { isInverse ->
            if (isInverse) {
                binding.btnSin.text = resources.getString(R.string.trigArcSin)
                binding.btnCos.text = resources.getString(R.string.trigArcCos)
                binding.btnTan.text = resources.getString(R.string.trigArcTan)
                binding.btnNaturalLogExponential.text = resources.getString(R.string.exponential)
            } else {
                binding.btnSin.text = resources.getString(R.string.trigSin)
                binding.btnCos.text = resources.getString(R.string.trigCos)
                binding.btnTan.text = resources.getString(R.string.trigTan)
                binding.btnNaturalLogExponential.text = resources.getString(R.string.naturalLog)
            }
        }

        binding.btnZero.setOnClickListener { model.addDigit("0") }
        binding.btnOne.setOnClickListener { model.addDigit("1") }
        binding.btnTwo.setOnClickListener { model.addDigit("2") }
        binding.btnThree.setOnClickListener { model.addDigit("3") }
        binding.btnFour.setOnClickListener { model.addDigit("4") }
        binding.btnFive.setOnClickListener { model.addDigit("5") }
        binding.btnSix.setOnClickListener { model.addDigit("6") }
        binding.btnSeven.setOnClickListener { model.addDigit("7") }
        binding.btnEight.setOnClickListener { model.addDigit("8") }
        binding.btnNine.setOnClickListener { model.addDigit("9") }

        //Operator
        binding.btnAdd.setOnClickListener { model.addOperator("+") }
        binding.btnSubtract.setOnClickListener { model.addOperator("-") }
        binding.btnDivide.setOnClickListener { model.addOperator("÷") }
        binding.btnMultiply.setOnClickListener { model.addOperator("×") }


//        //Trigonometry Function
        binding.btnSin.setOnClickListener { model.addTrigonometryFunction("sin(") }
        binding.btnCos.setOnClickListener { model.addTrigonometryFunction("cos(") }
        binding.btnTan.setOnClickListener { model.addTrigonometryFunction("tan(") }
//
//        //Scientific Operator
        binding.btnFactorial.setOnClickListener { model.addScientificOperator("!") }
        binding.btnPercent.setOnClickListener { model.addScientificOperator("%") }

//        //Scientific Function
        binding.btnSquareRoot.setOnClickListener { model.addScientificFunction("√(") }
        binding.btnLog.setOnClickListener { model.addScientificFunction("log(") }

        binding.btnNaturalLogExponential.setOnClickListener { model.addNaturalLogExponential() }

        binding.btnParenthesesOpen.setOnClickListener { model.addOpenParentheses() }
        binding.btnParenthesesClose.setOnClickListener { model.addCloseParentheses() }

        binding.btnPercent.setOnClickListener { model.addScientificOperator("%") }
        binding.btnDecimal.setOnClickListener { model.addDecimal() }

        binding.btnBackspace.setOnClickListener { model.backspace() }
        binding.btnBackspace.setOnLongClickListener {
            model.clearAll()
            true
        }
        binding.btnEquals.setOnClickListener {
            model.calculate()
        }

        binding.btnDegreeRadian.setOnClickListener { model.radianAndDegree() }


        binding.btnPie.setOnClickListener { model.addMathematicsConstant("π") }
        binding.btnE.setOnClickListener { model.addMathematicsConstant("e") }

        binding.btnPower.setOnClickListener { model.addPowerFunction() }

        binding.btnInverse.setOnClickListener { model.inverse() }

        model.isRadian.observe(this) { isRadian ->
            if (isRadian) {
                binding.tvDegreeRadian.text = resources.getString(R.string.radian)
                binding.btnDegreeRadian.text = resources.getString(R.string.degree)
            } else {
                binding.tvDegreeRadian.text = resources.getString(R.string.degree)
                binding.btnDegreeRadian.text = resources.getString(R.string.radian)
            }
        }
    }


}
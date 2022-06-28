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

    private lateinit var model: Model

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCalculatorBinding.bind(view)
        model = ViewModelProvider(this)[Model::class.java]

        model.result.observe(viewLifecycleOwner) {
            binding.tvResult.text = it
        }

        model.equalsPressed.observe(viewLifecycleOwner) { equalPressed ->
            binding.tvResult.visibility = if (equalPressed) View.VISIBLE else View.INVISIBLE
        }
        model.stringExp.observe(viewLifecycleOwner) {
            binding.tvExpression.text = it
            binding.svExpression.fullScroll(ScrollView.FOCUS_RIGHT)
        }

        model.isInverse.observe(viewLifecycleOwner) { isInverse ->
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

        binding.btnZero.setOnClickListener { model.addDigit(resources.getString(R.string.zero)) }
        binding.btnOne.setOnClickListener { model.addDigit(resources.getString(R.string.one)) }
        binding.btnTwo.setOnClickListener { model.addDigit(resources.getString(R.string.two)) }
        binding.btnThree.setOnClickListener { model.addDigit(resources.getString(R.string.three)) }
        binding.btnFour.setOnClickListener { model.addDigit(resources.getString(R.string.four)) }
        binding.btnFive.setOnClickListener { model.addDigit(resources.getString(R.string.five)) }
        binding.btnSix.setOnClickListener { model.addDigit(resources.getString(R.string.six)) }
        binding.btnSeven.setOnClickListener { model.addDigit(resources.getString(R.string.seven)) }
        binding.btnEight.setOnClickListener { model.addDigit(resources.getString(R.string.eight)) }
        binding.btnNine.setOnClickListener { model.addDigit(resources.getString(R.string.nine)) }

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

        model.isRadian.observe(viewLifecycleOwner) { isRadian ->
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
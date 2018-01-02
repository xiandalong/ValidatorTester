package com.teva.validatortester.validatortester

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.teva.gecko.validator.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

typealias StringValidator = Validator<String>

class MainActivity : AppCompatActivity() {

    private val emailValidator = StringValidator().required().email()

    private val alphabeticalValidator = StringValidator().required().alphabeticalCharacters()

    private val noAlphabeticalValidator = StringValidator().required().noAlphabeticalCharacters()

    private val specialCharValidator = StringValidator().required().specialCharacters()

    private val noSpecialCharValidator = StringValidator().required().noSpecialCharacters()

    private val stringLengthValidator = StringValidator().required().length(3, 5, true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // validate email
        onTextChanged(email_text, email_validation_text, emailValidator)

        // validate for all alphabetical letters
        onTextChanged(alphabetical_text, alphabetical_text_validation_text, alphabeticalValidator)

        // validate for all non alphabetical letters
        onTextChanged(no_alphabetical_text, no_alphabetical_text_validation_text, noAlphabeticalValidator)

        // validate for special letters
        onTextChanged(special_char_text, special_char_text_validation_text, specialCharValidator)

        // validate for non special letters
        onTextChanged(no_special_char_text, no_special_char_text_validation_text, noSpecialCharValidator)

        // validate for string length
        onTextChanged(string_with_length_limit, string_with_length_limit_validation_text, stringLengthValidator)
    }

    /**
     * @param editTextId the text input field
     * @param validationText the message in response to the text input
     * @param validator the string validator used
     */
    private fun onTextChanged(editTextId: TextView, validationText: TextView, validator: StringValidator) {
        RxTextView.textChanges(editTextId).debounce(250, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val result = validator.evaluate(it.toString())

                    validationText.run {
                        text = when (result) {
                            Result.OK -> "Valid"
                            is Result.EvaluationError -> result.throwable.message
                            is Result.UnexpectedError -> "Unexpected Error!"
                            is Result.TransformationalError -> "Transformational Error!"
                        }
                    }
                }
    }
}

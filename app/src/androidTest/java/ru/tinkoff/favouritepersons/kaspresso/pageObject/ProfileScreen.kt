package ru.tinkoff.favouritepersons.kaspresso.pageObject

import io.github.kakaocup.kakao.edit.KEditText
import io.github.kakaocup.kakao.edit.KTextInputLayout
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KTextView
import ru.tinkoff.favouritepersons.R

class ProfileScreen : BaseScreen() {
    val personScreenTitle = KTextView { withId(R.id.tw_person_screen_title) }
    val placeholderName = KTextInputLayout { withId(R.id.til_name) }
    val etName = KEditText { withId(R.id.et_name) }
    val placeholderSurname = KTextInputLayout { withId(R.id.til_surname) }
    val etSurname = KEditText { withId(R.id.et_surname) }
    val placeholderGender = KTextInputLayout { withId(R.id.til_gender) }
    val etGender = KEditText { withId(R.id.et_gender) }
    val placeholderBirthdate = KTextInputLayout { withId(R.id.til_birthdate) }
    val etBirthdate = KEditText { withId(R.id.et_birthdate) }
    val placeholderEmail = KTextInputLayout { withId(R.id.til_email) }
    val etEmail = KEditText { withId(R.id.et_email) }
    val placeholderPhone = KTextInputLayout { withId(R.id.til_phone) }
    val etPhone = KEditText { withId(R.id.et_phone) }
    val placeholderAddress = KTextInputLayout { withId(R.id.til_address) }
    val etAddress = KEditText { withId(R.id.et_address) }
    val placeholderImage = KTextInputLayout { withId(R.id.til_image_link) }
    val etImage = KEditText { withId(R.id.et_image) }
    val placeholderScore = KTextInputLayout { withId(R.id.til_score) }
    val etScore = KEditText { withId(R.id.et_score) }

    val saveButton = KButton { withId(R.id.submit_button) }

    fun checkProfileScreenTitile() {
        personScreenTitle.isDisplayed()
        personScreenTitle.hasText(R.string.person_layout_titile_add)
    }

    fun typeAndCheckName(name: String) {
        placeholderName.isDisplayed()
        placeholderName.hasHint(R.string.name)
        etName.typeText(name)
        etName.hasText(name)
    }

    fun typeAndCheckSurname(surname: String) {
        placeholderSurname.isDisplayed()
        placeholderSurname.hasHint(R.string.surname)
        etSurname.typeText(surname)
        etSurname.hasText(surname)
    }

    fun typeAndCheckGender(gender: String) {
        placeholderGender.isDisplayed()
        placeholderGender.hasHint(R.string.gender_hint)
        etGender.typeText(gender)
        etGender.hasText(gender)
    }

    fun typeAndCheckBirthdate(birthdate: String) {
        placeholderBirthdate.isDisplayed()
        placeholderBirthdate.hasHint(R.string.birthdate_hint)
        etBirthdate.typeText(birthdate)
        etBirthdate.hasText(birthdate)
    }

    fun typeAndCheckEmail(email: String) {
        placeholderEmail.isDisplayed()
        placeholderEmail.hasHint(R.string.e_mail_hint)
        etEmail.typeText(email)
        etEmail.hasText(email)
    }

    fun typeAndCheckPhone(phone: String) {
        placeholderPhone.isDisplayed()
        placeholderPhone.hasHint(R.string.phone_hint)
        etPhone.typeText(phone)
        etPhone.hasText(phone)
    }

    fun typeAndCheckAddress(address: String) {
        placeholderAddress.isDisplayed()
        placeholderAddress.hasHint(R.string.address_hint)
        etAddress.typeText(address)
        etAddress.hasText(address)
    }

    fun typeAndCheckImageLink(imageLink: String) {
        placeholderImage.isDisplayed()
        placeholderImage.hasHint(R.string.image_link)
        etImage.replaceText(imageLink)
        etImage.hasText(imageLink)
    }

    fun typeAndCheckScore(score: String) {
        placeholderScore.isDisplayed()
        placeholderScore.hasHint(R.string.score_hint)
        etScore.replaceText(score)
        etScore.hasText(score)
    }

    fun buttonSaveClick() {
        saveButton.click()
    }

    companion object {
        inline operator fun invoke(crossinline block: ProfileScreen.() -> Unit) =
            ProfileScreen().block()
    }
}